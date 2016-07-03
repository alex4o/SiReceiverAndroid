
import sbt.Keys._
import sbt.Resolver

androidBuild
//addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.0.0-RC1")

minSdkVersion.in(Android).:=("14")



javacOptions in Compile ++= "-source" :: "1.7" :: "-target" :: "1.7" :: Nil

resolvers += "jcenter" at "http://jcenter.bintray.com"
resolvers += Resolver.typesafeRepo("releases")



libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.11",
  aar("com.google.android" % "multidex" % "0.1")
)


//libraryDependencies ++= Seq(
//  "com.android.support" %% "multidex" % "1.0.1"
//)

dexMulti in Android := true


//proguardOptions in Android ++= io.Source.fromFile("proguard.cfg").getLines.toSeq

proguardScala in Android := false
useProguard in Android := false

/*
libraryDependencies ++= Seq(
  aar("org.macroid" %% "macroid" % "2.0.0-M3"),
  "com.android.support" % "support-v4" % "20.0.0"
)*/
