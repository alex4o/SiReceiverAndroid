package com.bonin.illuminati

import akka.actor.{Actor, ActorRef}
import akka.actor.Actor.Receive
import android.app.{Activity, Fragment, FragmentManager}
import com.bonin.illuminati.Data.This
import com.bonin.illuminati.FragmentManager.AttachTo
import Helper._
/**
  * Created by alex4o on 7/11/16.
  */
object FragmentManager {
  case object Create
  case class Create(actor: ActorRef)
  case class AttachTo(activity: Activity)

  case class SwitchTo(name: String)
}

class FragmentManagerActor extends Actor {
  var activity: Activity = null
  var fm : FragmentManager = null

  override def receive: Receive = {
    case FragmentManager.Create(actor: ActorRef) => {
      actor ! Data.GetThis
    }
    case This(fragment: Any) => {
      if(activity != null){
        //activity.runOnUiThread {
          val fragmentTransaction = fm.beginTransaction
          val fragmentTransactionNew = fragmentTransaction.replace(android.R.id.content, fragment.asInstanceOf[Fragment])
          val res = fragmentTransaction.commit()
        //}
      }
    }
    case AttachTo(activity: Activity) => {
      this.activity = activity
      fm = activity.getFragmentManager
    }
  }
}
