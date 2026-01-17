package ru.yanin.practice.user_service.service.rabbitMq;

public interface Producer<T> {

    void send(T message);
}
