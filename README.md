# Akka HTTP REST API with MondoDB

This is a repo I created to provide a simple setup for a basic REST API
using Scala, Akka HTTP, and MongoDB.

It also includes a setup for route testing.
(Testing the API from input to output without using HTTP connections.)

## Use Notes
Mongo is setup to read from a local database.
You'll have to point it to the right spot for your project.

## Acknowledgments
- I got started with Akka HTTP thanks to Miguel Lopez and his Akka HTTP Quickstart course.  
His Medium article about [the first few steps](https://medium.freecodecamp.org/how-you-can-build-a-hello-world-api-with-scala-and-akka-http-55e2ff67d70d) is what got me started.
- Gabriel Francisco wrote an informative post about [microservices using MongoDB](http://www.thedevpiece.com/building-microservices-using-akka-http-and-mongodb/). 
It helped me get that up and running in my project.
