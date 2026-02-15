package ru.yanin.practise.bookservice.service.message_broker.rabbitMq;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yanin.practise.bookservice.model.dto.ReadingPlanCalculationEvent;
import ru.yanin.shared.message_broker.producer.Producer;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMqPlanEventProducer implements Producer<ReadingPlanCalculationEvent> {

    @Qualifier("rabbitTemplateForPlanEvent")
    private final RabbitTemplate rabbitTemplate;

    @Override
    public void send(ReadingPlanCalculationEvent event) {
        rabbitTemplate.convertAndSend(event);
        log.debug("Sent verification event: {}", event);
    }
}
