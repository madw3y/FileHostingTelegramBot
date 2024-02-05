package ru.madwey.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MessageUtils {
    public SendMessage generateSendMessage(Update update, String text) {
        var message = new SendMessage();

        message.setChatId(update.getMessage().getChatId());
        message.setText(text);

        return message;
    }
}

