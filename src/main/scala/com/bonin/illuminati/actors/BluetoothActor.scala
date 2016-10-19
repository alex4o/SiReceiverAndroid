package com.bonin.illuminati

import java.io.{InputStream, OutputStream}
import java.util.UUID

import akka.actor.Actor
import akka.actor.Actor.Receive
import android.bluetooth.{BluetoothAdapter, BluetoothDevice, BluetoothSocket}
import android.util.Log
import com.bonin.illuminati.BluetoothActor._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.JavaConversions._
import scala.concurrent.Future

import java.nio.ByteBuffer


import Helper._

/**
  * Created by alex4o on 7/5/16.
  */
object BluetoothActor {
  case object Accept
  case object GetDevices
  object Subscribe
  object Disconect


  case class Connect(bluetoothDevice: BluetoothDevice)
  case class Connected(bluetoothSocket: BluetoothSocket)

  case class Send(data: Any)
  case class Received(data: Array[Byte])
  case class Disconected(msg: String)

  case class Devices(devices: Set[BluetoothDevice])
}

class BluetoothActor(uuidString: String, name: String) extends Actor {

  lazy val btAdapter = BluetoothAdapter.getDefaultAdapter
  var btSocket : BluetoothSocket = null
  val uuid = UUID.fromString(uuidString)

  var instream : InputStream = null
  var outstream : OutputStream = null

  override def preStart(): Unit ={

  }

  override def receive: Receive = disconnected

  def disconnected : Receive = {
    case Connect(device: BluetoothDevice) => {
      val commander = sender

      Future {
        val uuids = device.getUuids
        val socket = device.createInsecureRfcommSocketToServiceRecord(uuids(0).getUuid)

        Log.d("CON","Connecting !!!")
        socket.connect()
        Log.d("CON","Connected !!!")

        instream = socket.getInputStream
        outstream = socket.getOutputStream
        Log.d("CON","Streams !!!")

        socket
      } onSuccess {
        case sock => {
          btSocket = sock
          context.become(connected)
          commander ! Connected(sock)
        }
      }

    }
    case Accept => {
      val commander = sender

      Future {
        val acceptor = btAdapter.listenUsingRfcommWithServiceRecord(name, uuid)
        var socket = acceptor.accept()
        instream = socket.getInputStream
        outstream = socket.getOutputStream
        socket
      } onSuccess {
        case sock => {
          btSocket = sock
          context.become(connected)
          commander ! Connected(sock)
        }
      }
    }
    case GetDevices => {
      if (btAdapter.isEnabled()) {
        sender() ! Devices(btAdapter.getBondedDevices().toSet)
      } else {
        throw new RuntimeException("Your Bluetooth is not enabled!")
      }
    }
  }

  def connected : Receive = {
    case Send(string: String) => {
      outstream.write(string.length)
      outstream write string.getBytes

      outstream flush
    }
    case Send(bytes: Array[Byte]) => {
      outstream.write(bytes.length)
      outstream write(bytes)

      outstream flush
    }
    case Disconect => {
      btSocket.close()
      context.become(disconnected)
    }
    case Subscribe => {
      val subscriber = sender
      val thread = new Thread {
        while(btSocket isConnected) try{

          //og.d("READ", "BEGIN: " + btSocket.getRemoteDevice.getName )

          //var buffer : Array[Byte] = new Array[Byte](1)
          //var read = instream read buffer
          //Log.d("BRES", (buffer(0) & 0xFF) toString )

          


          var buffer : Array[Byte] = new Array[Byte](4)
          var read = instream read buffer

          if(buffer(0) == -34 && buffer(1) == -83){
            if(buffer(2) > 0 && buffer(2) < 64)
            {
              if(buffer(3) != 3){
                Log.d("BAD_MESSAGE", s"TYPE: ${buffer(3)}")
              }
              buffer = new Array[Byte](buffer(2) - 1)
              var read = instream read buffer


              //val buf : ByteBuffer = ByteBuffer.wrap buffer
              subscriber ! Received(buffer)
            }
          }


          //val length : Int = buffer
          //Log.d("LENGTH", length.toString)


          //buffer = new Array[Byte](length)

          //read = instream read buffer

          //Log.d("READ", read.toString)
          //Log.d("BUFFER", new String(buffer))

          //subscriber ! Received(buffer(0))

        }catch{
          case e : java.io.IOException => {
            Log.e("FUCK", e.getMessage, e)
            self ! Disconect
            subscriber ! Disconected(e.getMessage)
          }
        }
      }
    }
  }
}
