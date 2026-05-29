package com.puc.mensageria.config;

public final class RabbitMQConstants {

    public static final String EXCHANGE = "email.exchange";
    public static final String QUEUE = "email.send.queue";
    public static final String ROUTING_KEY = "email.send";

    private RabbitMQConstants() {
    }
}
