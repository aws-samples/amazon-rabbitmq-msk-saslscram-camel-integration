package org.amazon.connectors;

import java.time.Instant;
import java.util.Date;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PubSubRouter extends RouteBuilder {
    @Value("${amazon.rabbitmq.connection}")
    private String rabbitmqConnection;
    
    @Value("${amazon.msk.connection}")
    private String mskConnection;
    //${in.headers.CamelTimerCounter}
    @Override
    public void configure() throws Exception {
        // One route to push to MSK Sasl Scram
        from(rabbitmqConnection)
        .routeId("RabbitMQ Subscriber Route")
          .log(LoggingLevel.INFO, "${body}")
          .to(mskConnection);
        
    }
}
