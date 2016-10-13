package com.bonin.illuminati

import akka.actor.{ActorSystem, Props}
import android.app.Fragment
import android.bluetooth.{BluetoothDevice, BluetoothSocket}
import com.bonin.illuminati.BluetoothActor

/**
  * Created by alex4o on 7/3/16.
  */
object Data {
  var socket : BluetoothSocket = null
  var device : BluetoothDevice = null
  var server : Boolean = false
  val system = ActorSystem("AndroidSystem")
  lazy val btActor = system.actorOf(Props(new BluetoothActor("4ec5de5d-7be7-442b-b7f1-37207d2aa4ff","BONIN")))
  lazy val fma = Data.system.actorOf(Props(new FragmentManagerActor()), "FragmentManagerActor")

  case object GetThis
  case class This(t: Any)
  case class SwitchTo(fragment: Class[_])
}
