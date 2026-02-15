package ru.yanin.shared.message_broker.producer;

public interface Producer<T> {

    void send(T message);
}
