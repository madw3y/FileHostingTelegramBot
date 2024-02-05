package ru.madwey.controllers;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Log4j
@Controller
public class TelegramBot extends TelegramWebhookBot {
    @Value("${bot.name}")
    private String botName;

    @Value("${bot.uri}")
    private String botUri;

    private final UpdateProcessor updateProcessor;

    public TelegramBot(@Value("${bot.token}") String token,
                       UpdateProcessor updateProcessor) {
        super(token);
        this.updateProcessor = updateProcessor;
        menu();
    }

    public void menu() {
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "Начать работу с ботом"));
        listOfCommands.add(new BotCommand("/help", "Получить список доступных команд"));
        listOfCommands.add(new BotCommand("/cancel", "Отменить выполнение текущей команды"));
        listOfCommands.add(new BotCommand("/registration", "Регистрация пользователя"));

        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e);
        }
    }

    @PostConstruct
    private void init() {
        updateProcessor.registerBot(this);

        var setWebhook = SetWebhook.builder()
                .url(botUri)
                .build();
        //передаем наш статичиский адрес на сервер телеги
        //устанавливаем постоянное соединение
        //приложение --http--> Proxy server --https--> Telegram server
        try {
            this.setWebhook(setWebhook);
        } catch (TelegramApiException e) {
            log.error(e);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    public void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return null;
    }

    @Override
    public String getBotPath() {
        return "/update";
    }
}
