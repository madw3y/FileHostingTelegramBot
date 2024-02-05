package ru.madwey.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.madwey.services.UpdateProducerService;
import ru.madwey.utils.MessageUtils;


@Log4j
@RequiredArgsConstructor
@Controller
public class UpdateProcessor {
    private TelegramBot telegramBot;

    private final MessageUtils messageUtils;

    private final UpdateProducerService updateProducerService;


    @Value("${spring.rabbitmq.queues.text-message-update}")
    private String textMessageUpdate;

    @Value("${spring.rabbitmq.queues.doc-message-update}")
    private String docMessageUpdate;

    @Value("${spring.rabbitmq.queues.photo-message-update}")
    private String photoMessageUpdate;


    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void processUpdate(Update update) {
        if (update == null) {
            log.error("Received update is null");
            return;
        }

        if (update.hasMessage()) {
            distributeMessagesByType(update);
        } else {
            log.error("Unsupported message type is received: " + update);
        }
    }

    private void distributeMessagesByType(Update update) {
        var message = update.getMessage();
        if (message.hasText()) {
            processTextMessage(update);
        } else if (message.hasDocument()) {
            processDocMessage(update);
        } else if (message.hasPhoto()) {
            processPhotoMessage(update);
        } else {
            setUnsupportedMessageTypeView(update);
        }
    }

    private void setUnsupportedMessageTypeView(Update update) {
        var sendMessage = messageUtils.generateSendMessage(update,
                "Неподдерживаемый тип сообщения!");
        setView(sendMessage);
    }

    public void setView(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    private void setFileIsReceivedView(Update update) {
        var sendMessage = messageUtils.generateSendMessage(update,
                "Файл обрабатывается...");
        setView(sendMessage);
    }

    private void processPhotoMessage(Update update) {
        setFileIsReceivedView(update);
        updateProducerService.produce(photoMessageUpdate, update);
    }

    private void processDocMessage(Update update) {
        setFileIsReceivedView(update);
        updateProducerService.produce(docMessageUpdate, update);
    }

    private void processTextMessage(Update update) {
        updateProducerService.produce(textMessageUpdate, update);
    }
}
