package com.ddingcham.event;

import com.ddingcham.event.domain.commands.Command;
import lombok.extern.slf4j.Slf4j;
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

    public static void main(String[] args) {
        new SpringApplication(Sample.class).run(args);
    }

    @StreamListener(Sink.INPUT)
    public void commandStream(Command command) {
        log.info("Received command {}", command);
    }
}
