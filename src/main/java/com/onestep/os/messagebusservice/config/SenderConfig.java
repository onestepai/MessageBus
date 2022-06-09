package com.onestep.os.messagebusservice.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SenderConfig {
    @Autowired
    private TerminalConfig terminalConfig;
    @Bean
    public DirectExchange direct() {
        return new DirectExchange(terminalConfig.getExchangeName());
    }

}
