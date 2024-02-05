package ru.madwey.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.madwey.controllers.UpdateProcessor;
import ru.madwey.services.UpdateConsumerService;

@RequiredArgsConstructor
@Service
public class UpdateConsumerServiceImpl implements UpdateConsumerService {
    private final UpdateProcessor updateProcessor;

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.answer-message}")
    public void consume(SendMessage sendMessage) {
        updateProcessor.setView(sendMessage);
    }
}
