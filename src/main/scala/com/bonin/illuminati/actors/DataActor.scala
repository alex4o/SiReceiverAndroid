package com.bonin.illuminati.actors

import java.io.{BufferedOutputStream, File, FileOutputStream, OutputStreamWriter}
import java.nio.ByteBuffer

import akka.actor.{Actor, ActorRef}
import akka.actor.Actor.Receive
import android.bluetooth.{BluetoothDevice, BluetoothSocket}
import android.content.Context
import android.location.{Location, LocationListener, LocationManager}
import android.os.{Bundle, Environment}
import android.util.Log
import com.bonin.illuminati.BluetoothActor.{Received, Subscribe}
import com.bonin.illuminati.Data
import com.bonin.illuminati.actors.DataActor._


/**
  * Created by alex4o on 10/18/16.
  */
object DataActor {
  case class Data()
  case class Init(status: Boolean)
  case class CrcError()
  case class ReceivedPayload(payload: Payload)
  case class Position(location: Location)
  case class GetError(t: Int)// 0 => fifo, 1 => int
}

class Payload(payloadBuff: Array[Int])
{

  var Rssi: Int = 0x00
  var PwrLevel : Int = 0x00
  var Longtitude : Int = -0
  var Latitude : Int = -0
  var PacketId : Int = -0

  if (payloadBuff.length != 12) throw new IllegalArgumentException(s"Buffer is not correct size: ${payloadBuff.length}");


  Rssi = payloadBuff(0);
  PwrLevel = payloadBuff(1);
  PacketId = payloadBuff(2) | (payloadBuff(3) << 8);
  Latitude = (payloadBuff(4)) | (payloadBuff(5) << 8) | (payloadBuff(6) << 16) | (payloadBuff(7) << 24);
  Longtitude = (payloadBuff(8)) | (payloadBuff(9) << 8) | (payloadBuff(10) << 16) | (payloadBuff(11) << 24);

  override def toString: String = s" Id: ${PacketId}, RSSI: ${Rssi}, Pwr: ${PwrLevel}, Lon: ${Longtitude}, Lat: ${Latitude}"
}
class DataActor(actor: ActorRef) extends Actor {
  var pi = 0 // Payload index
  var payloadBuff = new Array[Int](64);
  var payloadLen : Int = 0x00
  var ui : ActorRef = null
  actor ! Subscribe




  var file : File = new File(Environment.getExternalStorageDirectory() + File.separator + "data_sirecv.bin")
  if(!file.exists()){
    file.createNewFile()
  }

  val fileostream = new FileOutputStream(file);
  val writer = new BufferedOutputStream(fileostream);

  override def receive: Receive = {
    case Subscribe => {
      ui = sender
    }
    case Received(data: Byte) => {
      val commander = sender

      val r = data & 0xFF
      Log.d("BYTE", s" ${data}");

//      if(buffer(0) == -34 && buffer(1) == -83){
//        if(buffer(2) > 0 && buffer(2) < 64)
//        {
//          if(buffer(3) != 3){
//            Log.d("BAD_MESSAGE", s"TYPE: ${buffer(3)}")
//          }
//          buffer = new Array[Byte](buffer(2) - 1)
//          var read = instream read buffer
//
//
//          //val buf : ByteBuffer = ByteBuffer.wrap buffer
//          subscriber ! Received(buffer)
//        }
//      }
       pi match {
         case 0 =>{
           if(r != 0xDE){
             pi = 0;
             return null;
           }
           else {
             payloadBuff(pi) = r
             pi += 1

           }
         }
         case 1 =>
         {
           if(r != 0xAD){
             pi = 0;
             return null;
           }
           else {
             payloadBuff(pi) = r
             pi += 1
           }
         }
         case 2 =>
         {
           payloadLen = r - 1;
           if(payloadLen < 0 || payloadLen > 0x40)
           {
             pi = 0;
             return null;
           }
           payloadBuff(pi) = payloadLen;
           pi += 1
         }
         case _ => {
           payloadBuff(pi) = r;
           pi += 1

           if(pi - 4 == payloadLen)
           {

             payloadBuff(3) match {
               case 3 => {
                 //var buffer = new Array[Int](payloadLen);
                 //Array.copy(payloadBuff, 4, buffer, 0, payloadLen)

                 //Buffer.BlockCopy(payloadBuff, 4, buffer, 0, payloadLen);
                 try
                 {

                   //Log.d("ASDASDF", payloadLen)
                   var lat : Int = Data.pos.getLatitude * 1000000 toInt
                   var lng : Int = Data.pos.getLongitude * 1000000 toInt

                   var time = Data.pos.getTime.toInt

                   val buffer = payloadBuff.slice(2, payloadLen + 4);

                   val bbuf = ByteBuffer.allocate(12 + buffer.length)
                     .putInt(time)
                     .putInt(lat)
                     .putInt(lng)
                     .put(buffer.map(x => x.toByte));

                   writer write bbuf.array
                   var trp = new Payload(buffer.slice(2, buffer.length));
                   ui ! ReceivedPayload(trp)
                 }catch {
                   case e: IllegalArgumentException => {
                     Log.e("ERROR!!!!", e.getMessage)

                   }
                 }
               }
               case _ => {
                 val buffer = payloadBuff.slice(2, payloadLen + 4);
                 var lat : Int = Data.pos.getLatitude * 1000000 toInt
                 var lng : Int = Data.pos.getLongitude * 1000000 toInt

                 var time = Data.pos.getTime.toInt

                 val bbuf = ByteBuffer.allocate(12 + buffer.length)
                   .putInt(time)
                   .putInt(lat)
                   .putInt(lng)
                   .put(buffer.map(x => x.toByte))

                   writer write bbuf.array


                   payloadBuff(3) match {
                     case 0 => ui ! Init(false)
                     case 1 => ui ! Init(true)
                     case 2 => ui ! CrcError()
                     case 4 => ui ! GetError(0)
                     case 5 => ui ! GetError(1)
                   }
                 }
               }

             pi = 0;
           }
        }
    }
  }
  }
}
