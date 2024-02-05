package ru.madwey.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.madwey.services.UpdateProducerService;

@Log4j
@RequiredArgsConstructor
@Service
public class UpdateProducerServiceImpl implements UpdateProducerService {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void produce(String rabbitQueue, Update update) {
        //TODO для формирования InternalServerError добавить ControllerAdvise
        log.debug(update.getMessage().getText());
        rabbitTemplate.convertAndSend(rabbitQueue, update);
    }
}
