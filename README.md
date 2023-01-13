# amazon-rabbitmq-msk-saslscram-camel-integration

This project demonstrates how data could be streamed from Amazon RabbitMQ to a SASL/SCRAM enabled Amazon MSK, through Apache Camel. 

### Workflow
![CamelConnector](https://user-images.githubusercontent.com/25897220/212338755-aca97740-b9dc-433f-b34e-e89b76a4ca52.png)


* [RabbitMQ](https://www.rabbitmq.com/) is a popular open source message broker with a vast range of functionality such as flexible routing/dead letter exchanges/multi-protocol support.
* [Apache Kafka](https://kafka.apache.org/) is an open source highly scalable real-time messaging system used in event driven communications / distributed event processing .
* [Apache Camel](https://camel.apache.org/) is an opensource integration framework supporting various enterprise integration patterns


### Pre-requisites:
1. Amazon Rabbit MQ is setup to have public access (associated with a public subnet) and is listening on port 5671. 
2. MSK is setup with SASL/SCRAM authentication (https://docs.aws.amazon.com/msk/latest/developerguide/msk-password.html#msk-password-tutorial) in a private subnet.
3. Apache Camel runs on an EC2 in a public subnet in the same VPC as Amazon MQ and Amazon MSK.
4. Maven to build the project.
    ```
      mvn clean install
    ```
5. jre >=8 to run the project.
    ``` 
      java -jar camel-kafka-sasl-scram-connector-1.0-SNAPSHOT.jar 
    ```

<br>
<br>

### Apache Camel Component Details
* Amazon RabbitMQ Pub/Sub Configuration:
  When publishing or subscribing to Amazon RabbitMQ queue below are some common important parameters to set for publishers and subscribers:
    ```
    sslProtocol: TLS
    portNumber: 5671
    userName: ***
    password: ******
    ```
  -  Checkout the `amazon.rabbitmq.connection` property in [application.properties](src/main/resources/application.properties#amazon.rabbitmq.connection) file.

* Amazon MSK Pub/Sub Configuration:
   For a SASL/SCRAM authentication based MSK below are the common important parameters to set for producers and consumers:
   ```
   saslMechanism: SCRAM-SHA-512
   securityProtocol=SASL_SSL
   sslEndpointAlgorithm=HTTPS
   saslJaasConfig=org.apache.kafka.common.security.scram.ScramLoginModule required username=fraser password=fraser-secret;
  ```
  #### MSK supports SCRAM-SHA-512 sasl mechanism
  ##### (http://www.iana.org/assignments/sasl-mechanisms/sasl-mechanisms.xhtml)
    - Checkout the `amazon.msk.connection` property in [application.properties](src/main/resources/application.properties#amazon.msk.connection) file.


--------------------------------------------------------------

## Apache Camel Routes:
1.  ``Data Generator Route``: This route generates json events every 500ms and pushes it to a RabbitMQ queue (queue-name=logs)
```
 Class Name: TestDataGenerator.java
 ```
2. ``RabbitMQ Subscriber Route``: This route polls the Rabbit MQ queue and pushes data to an MSK topic call iot-downstream.
```
 Class Name: TestDataGenerator.java
 ```
3. ``MSK Consumer Route``: This route subscribes to the iot-downstream topic on MSK and logs the data.
 ```
 Class Name: PubSubRouter.java
 ```

#### This project comes bundled with Hawtio. Hawtio is used to
 monitor/manage camel routes. Its available as a springboot actuator and is accessible at
* http://{your-ip-address}:8080/actuator/hawtio


## Steps to build and deploy

1. mvn clean install
2. cd target
3. java -jar camel-kafka-sasl-scram-connector-1.0-SNAPSHOT.jar 
