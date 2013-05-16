
The source code for a pretty old personal project (2005).
More information can be find there : http://reuse.pagesperso-orange.fr/photomailwizard/

Quickly converted to use SBT (http://www.scala-sbt.org/) instead of ANT

Can be quickly launched like this :
sbt run

or you can create an executable jar using this command : 
sbt assembly

and then starts it with :

java -jar target/scala-2.10/photomailwizard.jar


TAKE CARE : project quickly converted to SBT, properties variable replacement is not yet done, so in sent message you may see variable name like this @product.name@.


