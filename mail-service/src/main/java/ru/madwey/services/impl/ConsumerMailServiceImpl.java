package ru.madwey.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.madwey.dto.MailParams;
import ru.madwey.services.ConsumerMailService;
import ru.madwey.services.MailSenderService;

@RequiredArgsConstructor
@Service
public class ConsumerMailServiceImpl implements ConsumerMailService {

    private final MailSenderService mailSenderService;

    @Override
    @RabbitListener(queues = "${spring.rabbitmq.queues.registration-mail}")
    public void consumerRegistrationMail(MailParams mailParams) {
        mailSenderService.send(mailParams);
    }
}
