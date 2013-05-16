import AssemblyKeys._

seq(assemblySettings: _*)

name := "photomailwizard"

version := "1.1.0"

scalaVersion := "2.10.1"

scalacOptions ++= Seq("-unchecked", "-deprecation" )

mainClass in assembly := Some("com.photomail.Main")

jarName in assembly := "photomailwizard.jar"

libraryDependencies ++= Seq(
   "jgoodies" % "forms" % "1.0.5"
  ,"jgoodies" % "looks" % "1.2.2"
  ,"javax.mail" % "mail" % "1.4"
  ,"javax.activation" % "activation" % "1.1.1"
//  ,"com.drewnoakes" % "metadata-extractor" % "2.6.2"
//  ,"com.drewnoakes" % "metadata-extractor" % "2.2.2"
)

libraryDependencies ++= Seq(
   "com.novocode" % "junit-interface" % "0.10-M4" % "test"
  ,"junit" % "junit" % "3.8.2" % "test"
)

initialCommands in console := """import com.exproxy._"""

sourceGenerators in Compile <+= 
 (sourceManaged in Compile, version, name, jarName in assembly) map {
  (dir, version, projectname, jarexe) =>
  val file = dir / "com" / "photomail" / "MetaInfo.scala"
  IO.write(file,
  """package com.photomail
    |object MetaInfo { 
    |  val version="%s"
    |  val project="%s"
    |  val jarbasename="%s"
    |}
    |""".stripMargin.format(version, projectname, jarexe.split("[.]").head) )
  Seq(file)
}

