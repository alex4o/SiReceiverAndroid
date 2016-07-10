package com.bonin.illuminati

import akka.actor.{Actor, ActorRef, Props}
import akka.actor.Actor.Receive
import android.app.Fragment
import android.bluetooth.{BluetoothDevice, BluetoothSocket}
import android.os.Bundle
import android.view.{LayoutInflater, View, ViewGroup}
import android.widget.{AdapterView, Toast}
import com.bonin.illuminati.Data._
import com.bonin.illuminati.BluetoothActor._
import Helper._
import android.content.Intent
import android.util.Log

import scala.collection.JavaConversions._

/**
  * Created by alex4o on 7/6/16.
  */
class DeviceListFragment extends Fragment with Actor with TypedFindView{
  lazy val server = findView(TR.button)

  lazy val list = findView(TR.listView)
  lazy val ctx = this.getActivity.getApplicationContext
  lazy val la = new DeviceArrayAdapter(ctx, android.R.layout.simple_list_item_1)
  var view: View = null
  var main: ActorRef = null

  override  def onStart(): Unit = {
    super.onStart()
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    view = inflater.inflate(R.layout.main, container, false)
    Log.d("WTF", server.getText.toString)

    list.setAdapter(la)

    list.setOnItemClickListener((adaptorView: AdapterView[_], view: View, item: Int, long: Long) => {
      val device = adaptorView.getItemAtPosition(item).asInstanceOf[BluetoothDevice]

      Data.btActor ! Connect(device)
    })

    server onClick {
      server setText "Connecting..."
      Log.d("WTF", Data.btActor.toString())

      Data.btActor ! Accept
      //Toast makeText(getApplicationContext, "Hello", Toast.LENGTH_LONG) show
    }

    Data.btActor ! GetDevices
    return view
  }

  override def receive: Receive = {

    case Devices(devices : Set[BluetoothDevice]) => {
      Log.d("WTF", "Device List")

      la.addAll(devices)
    }

    case Connected(bluetoothSocket: Any) => {
      //val myIntent : Intent = new Intent(this, classOf[ConnectedActivity])
      //startActivity(myIntent)
      Log.d("WTF", "Connected")

      server setText "Connected!"

      val actor = Data.system.actorOf(Props(new ConnectionFragment), "ConnectionActor")
      /* when the ActorRef isn't saved in a variable the Cell is of type UncreatedActorCell
       which does not heave an referance to the original actor */
      this.getActivity.asInstanceOf[MainActivity].switchFragment(actor)

    }
  }

  override protected def findViewById(id: Int): View = {
    view.findViewById(id)
  }
}
