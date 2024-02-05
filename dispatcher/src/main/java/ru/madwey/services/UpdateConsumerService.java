package ru.madwey.services;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface UpdateConsumerService {
    void consume(SendMessage sendMessage);
}
