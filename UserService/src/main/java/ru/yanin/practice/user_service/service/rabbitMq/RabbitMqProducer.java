package ru.yanin.practice.user_service.service.rabbitMq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yanin.practice.user_service.model.dto.rabbit.VerificationEvent;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMqProducer implements Producer<VerificationEvent> {

    @Qualifier("rabbitTemplateForVerification")
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void send(VerificationEvent event) {
        rabbitTemplate.convertAndSend(event);
        log.debug("Sent verification event: {}", event);
    }
}
