package com.bonin.illuminati

import akka.actor.Actor
import akka.actor.Actor.Receive
import android.app.Fragment
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{AdapterView, ArrayAdapter, Toast}
import com.bonin.illuminati.BluetoothActor._
import Helper._

/**
  * Created by alex4o on 7/8/16.
  */
class ConnectionFragment extends Fragment with Actor with TypedFindView{
  var view: View = null

  lazy val button = findView(TR.button)
  lazy val list = findView(TR.listView)
  lazy val ctx = this.getActivity.getApplicationContext

  lazy val text = findView(TR.text)
  lazy val adapter = new ArrayAdapter[String](ctx, android.R.layout.simple_list_item_1)

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    view = inflater.inflate(R.layout.connected, container, false)

    Data.btActor ! Subscribe

    button onClick {

      adapter.insert(text.getText.toString, 0)
      val bytes = text.getText().toString().getBytes()

      Data.btActor ! Send(bytes)

      text setText ""

    }

    return view
  }

  override protected def findViewById(id: Int): View = {
    view.findViewById(id)
  }

  override def receive: Receive = {
    case Data.GetThis => {
      sender ! Data.This(this)
    }
    case Received(data : Array[Byte]) => getActivity.runOnUiThread {
      adapter.add(new String(data))
    }
    case Disconected(msg: String) => getActivity.runOnUiThread {
      this.getActivity.getActionBar.setTitle("Disconnected")
    }
  }
}
