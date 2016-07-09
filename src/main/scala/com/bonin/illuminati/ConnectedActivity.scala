package com.bonin.illuminati

import android.app.Activity
import android.os.{AsyncTask, Bundle}
import android.widget.{ArrayAdapter, Toast}
import Helper._
import android.content.DialogInterface
import android.view.View.OnClickListener
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.view.View.OnClickListener

/**
  * Created by alex4o on 7/3/16.
  */
class ConnectedActivity extends Activity with TypedFindView {
  lazy val button = findView(TR.button)
  lazy val list = findView(TR.listView)


  lazy val text = findView(TR.text)

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)

    setContentView(R.layout.connected)


    val adapter = new ArrayAdapter[String](this, android.R.layout.simple_list_item_1)
    list.setAdapter(adapter)

    if(!Data.server){
      Data.socket.connect()
    }else{

    }

    if(Data.socket.isConnected()){
      val instream = Data.socket getInputStream
      val outstream = Data.socket getOutputStream

      this.setTitle("Conncted: " + Data.device.getName)


      button onClick {

        adapter.insert(text.getText.toString, 0)
        val bytes = text.getText().toString().getBytes()

        Toast makeText(getApplicationContext, bytes.toString, Toast.LENGTH_LONG) show

        outstream write Int2ByteArray(bytes.length)
        outstream write bytes

        outstream flush

        text setText ""

      }
      val thread = new Thread({

        while(Data.socket.isConnected()){
          try{

            Log.d("READ", "BEGIN: " + Data.device.getName )


            var buffer : Array[Byte] = new Array[Byte](4)
            var read = instream read buffer


            val length : Int = ByteArray2Int(buffer)
            Log.d("LENGTH", length.toString)


            buffer = new Array[Byte](length)

            read = instream read buffer

            Log.d("READ", read.toString)
            Log.d("BUFFER", new String(buffer))

            runOnUiThread({
              adapter.insert(new String(buffer), 0)
            })

          }catch{
            case e : java.io.IOException => {
              Log.e("FUCK", e.getMessage, e)
              Data.socket.close()
              runOnUiThread {
                this.setTitle("Disconnected!")
              }
            }
          }
        }

      }).start()



//      val task = new AsyncTask[Unit, Array[Byte], String] {
//
//        override def doInBackground(parmas: Unit*): String = {
//
//          return "Hello!"
//        }
//
//
//        override def onPostExecute(res: String): Unit = {
//
//        }
//
//        override def onProgressUpdate(values: Array[Byte]*) {
//        }
//      }
//
//      task.execute()
    }



  }
}
