package com.ddingcham.event;

import com.ddingcham.event.boundary.ShopItems;
import com.ddingcham.event.domain.commands.Command;
import com.ddingcham.event.domain.commands.MarkPaymentTimeout;
import com.ddingcham.event.domain.commands.Order;
import com.ddingcham.event.domain.commands.Pay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableScheduling
@EnableBinding(Processor.class)
@Slf4j
public class Sample {

    @Autowired
    ShopItems shopItems;

    public static void main(String[] args) {
        new SpringApplication(Sample.class).run(args);
    }

    @StreamListener(Sink.INPUT)
    public void commandStream(Command command) {

        log.info("Received command {}", command);

        if (command instanceof MarkPaymentTimeout) {
            shopItems.markPaymentTimeout((MarkPaymentTimeout) command);
        } else if (command instanceof Order) {
            shopItems.order((Order) command);
        } else if (command instanceof Pay) {
            shopItems.pay((Pay) command);
        }
    }
}
