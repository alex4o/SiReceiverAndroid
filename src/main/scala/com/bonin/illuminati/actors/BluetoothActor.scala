package com.bonin.illuminati

import java.util.UUID

import akka.actor.Actor
import akka.actor.Actor.Receive
import android.bluetooth.{BluetoothAdapter, BluetoothDevice, BluetoothSocket}
import com.bonin.illuminati.BluetoothActor._

import scala.concurrent.ExecutionContext.Implicits.global

import scala.collection.JavaConversions._
import scala.concurrent.Future

/**
  * Created by alex4o on 7/5/16.
  */
object BluetoothActor {
  case object Accept
  case object GetDevices

  case class Connect(bluetoothDevice: BluetoothDevice)
  case class Connected(bluetoothSocket: BluetoothSocket)

  case class Send(data: Any)
  case class Received(data: Array[Byte])
  case class Devices(devices: Set[BluetoothDevice])
}

class BluetoothActor(uuidString: String, name: String) extends Actor {

  lazy val btAdapter = BluetoothAdapter.getDefaultAdapter
  var btSocket : BluetoothSocket = null
  val uuid = UUID.fromString(uuidString)

  override def preStart(): Unit ={

  }

  override def receive: Receive = {
    case Connect(device: BluetoothDevice) => {
      val commander = sender

      Future {
        val socket = device.createRfcommSocketToServiceRecord(uuid)
        socket.connect()
        socket
      } onSuccess {
        case sock => {
          btSocket = sock
          commander ! Connected(sock)
        }
      }

    }
    case Accept => {
      val commander = sender
      commander ! Data.This

      /*Future {
        val sock = btAdapter.listenUsingRfcommWithServiceRecord(name, uuid)
        sock.accept()
      } onSuccess {
        case sock => {
          btSocket = sock
          commander ! Connected(sock)
        }
      }*/
    }
    case GetDevices => {
      if(btAdapter.isEnabled()) {
        sender() ! Devices(btAdapter.getBondedDevices().toSet)
      }else{
        throw new RuntimeException("Your fucking bluetooth is not enabled!!!!")
      }
    }
    case Send(string: String) => {

    }
    case Send(bytes: Array[Byte]) => {

    }
  }
}
