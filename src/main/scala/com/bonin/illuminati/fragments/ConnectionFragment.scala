package com.bonin.illuminati

import akka.actor.Actor
import akka.actor.Actor.Receive
import android.app.Fragment
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.AdapterView

import com.bonin.illuminati.BluetoothActor.Connect

/**
  * Created by alex4o on 7/8/16.
  */
class ConnectionFragment extends Fragment with Actor with TypedFindView{
  var view: View = null

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    view = inflater.inflate(R.layout.connected, container, false)

    return view
  }

  override protected def findViewById(id: Int): View = {
    getView.findViewById(id)
  }

  override def receive: Receive = {
    case _ => {}
  }
}
