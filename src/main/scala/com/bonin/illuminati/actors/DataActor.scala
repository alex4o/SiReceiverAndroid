package com.bonin.illuminati.actors

import akka.actor.{Actor, ActorRef}
import akka.actor.Actor.Receive
import android.bluetooth.{BluetoothDevice, BluetoothSocket}
import android.util.Log
import com.bonin.illuminati.BluetoothActor.{Received, Subscribe}
import com.bonin.illuminati.actors.DataActor.{CrcError, GetError, Init, ReceivedPayload}

/**
  * Created by alex4o on 10/18/16.
  */
object DataActor {
  case class Data()
  case class Init(status: Boolean)
  case class CrcError()
  case class ReceivedPayload(payload: Payload)
  case class GetError(t: Int)// 0 => fifo, 1 => int
}

class Payload(payloadBuff: Array[Byte])
{

  var Rssi: Byte = 0x00
  var PwrLevel : Byte = 0x00
  var Longtitude : Int = -0
  var Latitude : Int = -0
  var PacketId : Int = -0

  if (payloadBuff.length != 12) throw new IllegalArgumentException("Buffer is not correct size");


  Rssi = payloadBuff(0);
  PwrLevel = payloadBuff(1);
  PacketId = payloadBuff(2) | (payloadBuff(3) << 8);
  Latitude = (payloadBuff(4)) | (payloadBuff(5) << 8) | (payloadBuff(6) << 16) | (payloadBuff(7) << 24);
  Longtitude = (payloadBuff(8)) | (payloadBuff(9) << 8) | (payloadBuff(10) << 16) | (payloadBuff(11) << 24);

  override def toString: String = s"Id: ${PacketId}, RSSI: ${Rssi}, Pwr: ${PwrLevel}, Lon: ${Longtitude}, Lat: ${Latitude}"
}

class DataActor(actor: ActorRef) extends Actor {
  var pi = 0 // Payload index
  var payloadBuff = new Array[Byte](128);
  var payloadLen : Byte = 0x00
  var ui : ActorRef = null
  actor ! Subscribe

  override def receive: Receive = {
    case Subscribe => {
      ui = sender
    }
    case Received(data: Byte) => {
      val commander = sender

      pi match {
        case 0 =>{
          if(data != 0xDE){
            pi = 0;
            return null;
          }
          else {
            pi += 1
            payloadBuff(pi) = data
          }
        }
        case 1 =>
        {
          if(data != 0xAD){
            pi = 0;
            return null;
          }
          else {
            pi += 1
            payloadBuff(pi) = data
          }
        }
        case 2 =>
        {
          payloadLen = (data - 1).asInstanceOf[Byte];
          if(payloadLen > 0x40)
          {
            pi = 0;
            return null;
          }
          pi += 1
          payloadBuff(pi) = payloadLen;
        }
        case _ => {
          pi += 1

          payloadBuff(pi) = data;
          if(pi - 4 == payloadLen)
          {

            payloadBuff(3) match {
              case 0 => ui ! Init(false)
              case 1 => ui ! Init(true)
              case 2 => ui ! CrcError()
              case 3 => {
                var buffer = new Array[Byte](payloadLen);
                Array.copy(payloadBuff, 4, buffer, 0, payloadLen))
                //Buffer.BlockCopy(payloadBuff, 4, buffer, 0, payloadLen);
                try
                {
                  var trp = new Payload(buffer);
                  ui ! ReceivedPayload(trp)
                }catch {
                  case e: IllegalArgumentException => {
                    Log.e("ERROR!!!!", e.getMessage)
                  }
                }
              }
              case 4 => ui ! GetError(0)
              case 5 => ui ! GetError(1)

            }
            pi = 0;
          }
        }
    }
  }
  }
}
