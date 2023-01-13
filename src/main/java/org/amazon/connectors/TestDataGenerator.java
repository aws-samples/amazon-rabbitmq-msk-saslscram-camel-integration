package org.amazon.connectors;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spi.UuidGenerator;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TestDataGenerator extends RouteBuilder {
    final Random rd = new Random();
    
    @Value("${amazon.rabbitmq.connection}")
    private String rabbitmqConnection;
    
    @Value("${amazon.msk.consumers}")
    private String mskConsumers;
    
    @Override
    public void configure() throws Exception {
        
        // Generate data every 500ms and push to RabbitMQ
        from("timer://simple?period=500")
        .routeId("Data Generator Route")
        .process(new Processor() {
            @Override
            public void process(Exchange exch) throws Exception {
                JSONObject json = new JSONObject();
                json.put("event", "camera-down");
                json.put("timestamp", Instant.now().toString());
                json.put("id", UUID.randomUUID().toString());
                json.put("device", rd.nextInt(10));
                exch.getIn().setBody(json.toString());
            }
        })
        .to(rabbitmqConnection);


        // 5 consumers reading from MSK 
        from(mskConsumers).routeId("MSK Consumer Route").log(LoggingLevel.INFO, "${body}");


        
    }
}
