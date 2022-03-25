# ClearScore Tech Test

This is my submission for the ClearScore tech challenge homework exercise that was set. This was 
initially tested using cURL and [Postman](https://www.postman.com) and later tested using retrospective unit tests, where
any test failures would have resulted in further implementation to address these issues. 

The project was written in Scala (version 2.13) and is built using Maven (which I am aware that ClearScore currently uses). 
Maven was used as opposed to SBT simply because of the [MoJo Codehaus](https://www.mojohaus.org) appassembler plugin that 
was used that allows the Scala application binaries to be automatically packaged to a shell script, which as far as I 
am aware is only available for use with Maven.  

### Getting Started
To run the application simply use the start wrapper script (as requested this is a bash script). You
can just simply run script as:

>__./start.sh__

All defaults for defining the port and both endpoints are provided by the application.conf file. If
you wish to specify these manually you may do so in the following manner, for example: 

>__./start.sh 1234 http://cscards.com http://scoredcards.com__

The service should then run and you may test it using cURL, for example:

>__curl -X POST\ <br /> 
> -H "Content-type: application/json"\ <br /> 
> -d {"name": "John Smith", "creditScore": 500, "salary": 25000}\ <br />
>localhost:8080/creditcards__

The service will then output all available credit card offers from both providers.

To build the application source, simply do the following steps:
>__mvn install__ <br />
> __mvn package appassembler:assemble__

You can also run the build script which does this. Please ensure to run the build script before running the start script.

The resulting shell script (start.sh) is available in the target directory: <br />
${ProjectDirectory}/target/appassembler/assemble/bin

## What libraries were used? 
The project uses the following key libraries:
* [Scala Language Library](https://mvnrepository.com/artifact/org.scala-lang/scala-library)
* [Typesafe Config](https://mvnrepository.com/artifact/com.typesafe/config) 
* [SCLAP](https://mvnrepository.com/artifact/io.jobial/sclap)
* [Akka HTTP](https://mvnrepository.com/artifact/com.typesafe.akka/akka-http)
* [Akka Actor (Typed)](https://mvnrepository.com/artifact/com.typesafe.akka/akka-actor-typed)
* [Akka Stream (Typed)](https://mvnrepository.com/artifact/com.typesafe.akka/akka-stream-typed)
* [Akka HTTP Spray JSON](https://mvnrepository.com/artifact/com.typesafe.akka/akka-http-spray-json)
* [Akka HTTP CORS Directives](https://mvnrepository.com/artifact/ch.megard/akka-http-cors)
* [Swagger for Akka HTTP](https://mvnrepository.com/artifact/com.github.swagger-akka-http/swagger-akka-http)
* [SLF4J Logger](https://mvnrepository.com/artifact/org.slf4j/slf4j-api) 
* [ScalaTest](https://mvnrepository.com/artifact/org.scalatest/scalatest)

Documentation: <br />
* [SCLAP](https://github.com/jobial-io/sclap)  
* [Akka](https://akka.io) 
* [Swagger](https://swagger.io)

## How this was done

This project itself follows a very simple process, which was designed as follows: 

1. Start the service with a specified port and endpoints - if these are not provided, default values can simply be 
defined in a configuration file which is preferable to use as opposed to hardcoded values,
2. Receive an HTTP POST request from the end user using "/creditcards" path and parse the incoming JSON to an object
3. Use the data in the object to create 2 seperate HTTP POST requests (one for each credit card provider) and handle the
response in a fault tolerant manner (i.e. fail fast) where the received data is parsed to a list of objects
4. From the resulting data, process this to build the custom response and return to the user

Akka HTTP makes use of the Actor System to define services and routes, which are done through CORS directives. Spray is 
used for the serialisation of JSON strings to objects and deserialisation of objects to JSON strings, which are done 
implicitly. Swagger is used to document the API. 

The implementation followed a modular approach to avoid writing long Scala scripts. It is simply divided into the following 
key directories: 
* config - for storing all files realting to the configuartion setup 
* models - holds the API models - i.e. case classes - of all entities, the service response and expected responses
* service - holds code specific to the server logic 
* swagger - holds code relating to Swagger documentation

The remaining ClearScoreApp is the main application itself that accepts input parameters from the user (or from config)
and uses the values to call the main method from the service. 

This ensures that code stored in each directory is highly specific and allows other developers to quickly and easily navigate 
the code. Ensuring a minimalist implementation approach for each source file helps to promote readability and thereby keeping the
code in a state that is easy to maintain. 

## How I'd deploy this
In a real developer environment this would be deployed as a follows: 
1. Submit pull request for review
2. Deploy to a CI/CD system (such as Jenkins or GitLabs)
3. Containerize the application using Docker or Kubernetes
4. Publish the container image on a server (for example, an EC2 instance from AWS)
5. Spin up the docker image and allow the service to run as a daemon process and ensure that a log file is being written 
in real time by the service
6. Test that the service is working as expected by forming a POST request using cURL. This is also an opportunity for integration testing 
as an actual web application would be making these requests, so would ensure that any web app pointing the service can receive the data. 
 

