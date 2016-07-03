androidBuild


minSdkVersion.in(Android).:=("14")



javacOptions in Compile ++= "-source" :: "1.7" :: "-target" :: "1.7" :: Nil

resolvers += "jcenter" at "http://jcenter.bintray.com"

/*
libraryDependencies ++= Seq(
  aar("org.macroid" %% "macroid" % "2.0.0-M3"),
  "com.android.support" % "support-v4" % "20.0.0"
)*/
