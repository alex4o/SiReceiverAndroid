package com.bonin.illuminati


import java.util.UUID

import android.app.Activity
import android.bluetooth.{BluetoothAdapter, BluetoothDevice}
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget._

import scala.collection.JavaConversions._
import Helper._

class MainActivity extends Activity with TypedFindView {
    lazy val server = findView(TR.button)

    lazy val list = findView(TR.listView)
    lazy val btAdapter = BluetoothAdapter.getDefaultAdapter

    var btDevices : Set[BluetoothDevice] = null
    val uuid = UUID.fromString("4ec5de5d-7be7-442b-b7f1-37207d2aa4ff")
    /** Called when the activity is first created. */
    override def onCreate(savedInstanceState: Bundle): Unit = {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        val la = new DeviceArrayAdapter(this, android.R.layout.simple_list_item_1)
        list.setAdapter(la)


        if(btAdapter.isEnabled()){
           val btDevices = btAdapter.getBondedDevices().toSet

          for(device <- btDevices){
            la.add(device)
          }
        }

      list.setOnItemClickListener((adaptorView: AdapterView[_], view: View, item: Int, long: Long) => {
        val device = adaptorView.getItemAtPosition(item).asInstanceOf[BluetoothDevice]

        Toast makeText(getApplicationContext, device.toString, Toast.LENGTH_LONG) show

        val socket = device.createRfcommSocketToServiceRecord(uuid)

        Data.socket = socket
        Data.device = device

        val myIntent : Intent = new Intent(this, classOf[ConnectedActivity])
        startActivity(myIntent)

      })

      server onClick {
        val thread = new Thread({
          runOnUiThread {
            server setText "Connecting..."
          }
          val sock = btAdapter.listenUsingRfcommWithServiceRecord("BONIN", uuid)
          val accepted = sock.accept()
          Data.socket = accepted
          Data.server = true
          Data.device = accepted.getRemoteDevice

          runOnUiThread {
            val myIntent : Intent = new Intent(this, classOf[ConnectedActivity])
            startActivity(myIntent)
          }
        })
        thread.start()

      }
        // list.setAdapter()
    }
}