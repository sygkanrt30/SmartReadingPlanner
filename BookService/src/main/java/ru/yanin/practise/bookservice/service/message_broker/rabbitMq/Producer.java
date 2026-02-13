package ru.yanin.practise.bookservice.service.message_broker.rabbitMq;

public interface Producer<T> {

    void send(T message);
}
