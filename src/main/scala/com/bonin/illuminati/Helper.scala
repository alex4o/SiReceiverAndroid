package com.bonin.illuminati

/**
  * Created by alex4o on 7/3/16.
  */
import java.nio.ByteBuffer

import android.view.View
import android.view.View.OnClickListener
import android.widget.AdapterView.OnItemClickListener
import android.widget.{AdapterView, Button, ListView}

object Helper {

  implicit def func2onclicklistener(f: View => Unit) : View.OnClickListener = {
    new OnClickListener {
      override def onClick(view: View): Unit = f(view)
    }
  }


  implicit def funcnoparam2onclicklistener(f: () => Unit) : View.OnClickListener = {
    new OnClickListener {
      override def onClick(view: View): Unit = f()
    }
  }

  implicit def funcnoparam2onitemclicklistener(f: (AdapterView[_], View, Int, Long) => Unit) : AdapterView.OnItemClickListener = {
    new OnItemClickListener {
      override def onItemClick(adapterView: AdapterView[_], view: View, i: Int, l: Long): Unit = {
        f(adapterView, view, i, l)
      }
    }
  }

  implicit def func2runnable(f: => Unit) : Runnable = {
    new Runnable {
      override def run(): Unit = {
        f
      }
    }
  }

  implicit def unit2onclicklistener(f: => Unit) : View.OnClickListener = {
    new OnClickListener {
      override def onClick(view: View): Unit = () => f
    }
  }

  implicit class ButtonEvents(val button : Button)  {
    def onClick(f: => Unit): Unit = {
      button.setOnClickListener(() => {
        f
      })
    }
  }

  implicit def Int2ByteArray(value : Int) : Array[Byte] = {
    ByteBuffer allocate(4) putInt(value) array
  }
  implicit def ByteArray2Int(bytes: Array[Byte]) : Int = {
    ByteBuffer wrap(bytes) getInt
  }


  implicit class ListViewEvents(val listview : ListView)  {
    def onClick(f: => Unit): Unit = {
      listview.setOnClickListener(() => {
        f
      })
    }

    def onItemClick(f: => Unit): Unit = {
      listview.setOnItemClickListener((adaptorView: AdapterView[_], view: View, item: Int, long: Long) => {
        f
      })
    }
  }
}



