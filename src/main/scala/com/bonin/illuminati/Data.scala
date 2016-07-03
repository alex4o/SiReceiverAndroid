package com.bonin.illuminati

import android.bluetooth.{BluetoothDevice, BluetoothSocket}

/**
  * Created by alex4o on 7/3/16.
  */
object Data {
  var socket : BluetoothSocket = null
  var device : BluetoothDevice = null
  var server : Boolean = false
}
