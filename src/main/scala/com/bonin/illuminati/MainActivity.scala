package com.bonin.illuminati


import java.util.UUID
import java.util.concurrent.TimeUnit

import akka.actor.{ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout
import android.app.{Activity, Fragment}
import android.os.Bundle

import scala.concurrent.ExecutionContext.Implicits.global
import Helper._
import android.util.Log

class MainActivity extends Activity with TypedFindView {
    lazy val fm = getFragmentManager
    implicit val max : Timeout = new Timeout(1, TimeUnit.HOURS)



    val uuid = UUID.fromString("4ec5de5d-7be7-442b-b7f1-37207d2aa4ff")

  def switchFragment(actor : ActorRef): Unit = runOnUiThread
  {

      Log.d("switching", "fragment to be switched")

      //    val fields = actor.getClass.getDeclaredFields().foreach(f => {
      //      Log.d("Field", f.getName)
      //    })


      println(actor.getClass toString)

      val underlying = actor.getClass.getDeclaredMethod("underlying")
      //cellField.setAccessible(true)
      val cell = underlying.invoke(actor)

      println(cell.getClass toString)


      val actorReal = cell.getClass.getDeclaredMethod("actor").invoke(cell)


      val fragment = actorReal.asInstanceOf[Fragment]
      val fragmentTransaction = fm.beginTransaction()
      val fragmentTransactionNew = fragmentTransaction.replace(android.R.id.content, fragment)
      val res = fragmentTransaction.commit()
  }


//    (actor ? Data.GetThis).onSuccess({
//      case Data.This(fragment) => {
//        //Toast makeText(getApplicationContext, "Fragment created", Toast.LENGTH_LONG) show
//        runOnUiThread(func2runnable({
//
//        }))
//      }
//    })

   override def onCreate(savedInstanceState: Bundle): Unit = {
     super.onCreate(savedInstanceState)
     //setContentView(R.layout.main)

    //Data.system.eventStream.


     val actor = Data.system.actorOf(Props(new DeviceListFragment()), "MainActor")
     switchFragment(actor)





     //      server onClick {
     //        val thread = new Thread({
     //          runOnUiThread {
     //            server setText "Connecting..."
     //          }
     //          val sock = btAdapter.listenUsingRfcommWithServiceRecord("BONIN", uuid)
     //          val accepted = sock.accept()
     //          Data.socket = accepted
     //          Data.server = true
     //          Data.device = accepted.getRemoteDevice
     //
     //          runOnUiThread {
     //            val myIntent : Intent = new Intent(this, classOf[ConnectedActivity])
     //            startActivity(myIntent)
     //          }
     //        })
     //        thread.start()
     //
     //      }
     // list.setAdapter()
   }
}