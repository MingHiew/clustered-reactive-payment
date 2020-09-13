name := "clustered-reactive-payment"

version := "0.1"

scalaVersion := "2.13.3"
resolvers ++= Seq(
  "maven" at "https://repo1.maven.org/maven2/"
)
lazy val akkaVersion = "2.6.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor"                  % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-typed"            % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-sharding-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-persistence-typed"      % akkaVersion,
  "com.typesafe.akka" %% "akka-serialization-jackson"  % akkaVersion,
  "org.fusesource.leveldbjni" % "leveldbjni-all"       % "1.8",
  "ch.qos.logback"     % "logback-classic"             % "1.2.3",
  "com.typesafe.akka" %% "akka-actor-testkit-typed"    % akkaVersion  % "test",
  "org.typelevel"     %% "squants"                     % "1.7.0" from "https://repo1.maven.org/maven2/org/typelevel/squants_2.13/1.7.0/squants_2.13-1.7.0.jar",
  "org.scalatest"     %% "scalatest"                   % "3.2.2"      % "test" from "https://repo1.maven.org/maven2/org/scalatest/scalatest_2.13/3.2.2/scalatest_2.13-3.2.2.jar"

)

fork in Test := true