package com.bonin.illuminati

import java.util

import android.content.Context
import android.widget.{ArrayAdapter, BaseAdapter, TextView}
import android.bluetooth.BluetoothDevice
import android.util.Size
import android.view.{LayoutInflater, View, ViewGroup}




class DeviceArrayAdapter(val context: Context, val id: Int) extends GenericArrayAdapter[BluetoothDevice](context) {

    override def drawText(textView: TextView, o : BluetoothDevice) {
        textView.setText(o.getName + "\n" + o.getAddress)
    }
}
