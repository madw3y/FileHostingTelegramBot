package ru.madwey.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.madwey.entity.AppUser;
import ru.madwey.entity.RawData;
import ru.madwey.exception.UploadFileException;
import ru.madwey.repository.AppUserRepository;
import ru.madwey.repository.RawDataRepository;
import ru.madwey.service.AppUserService;
import ru.madwey.service.ProcessFileService;
import ru.madwey.service.MainService;
import ru.madwey.service.ProducerService;
import ru.madwey.service.enums.LinkType;
import ru.madwey.service.enums.ServiceCommand;


import static ru.madwey.entity.enums.UserState.*;
import static ru.madwey.service.enums.ServiceCommand.*;

@Log4j
@RequiredArgsConstructor
@Service
public class MainServiceImpl implements MainService {
    private final RawDataRepository rawDataRepository;

    private final AppUserRepository appUserRepository;

    private final ProducerService producerService;

    private final ProcessFileService processFileService;

    private final AppUserService appUserService;


    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);

        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getState();
        var text = update.getMessage().getText();
        var output = "";

        var serviceCommand = ServiceCommand.fromValue(text);
        if (CANCEL.equals(serviceCommand)) {
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)){
            output = appUserService.setEmail(appUser, text);
        } else {
            log.error("Unknown user state: " + userState);
            output = "Неизвестная ошибка! Введите /cancel и попробуйте снова.";
        }

        var chatId = update.getMessage().getChatId();
        sendAnswer(chatId, output);
    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);

        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();

        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        try{
            var doc = processFileService.processDoc(update.getMessage());
            var link = processFileService.generateLink(doc.getId(), LinkType.GET_DOC);
            var answer = "Документ успешно съеден. Вот тебе ссылка для скачивания: " +
                    "(я сам не знаю откуда она взялась...)\n " + link;

            sendAnswer(chatId, answer);
        } catch (UploadFileException e) {
            log.error(e);
            var error = "Загрузка файла не удалась. Повтори попытку позже.";

            sendAnswer(chatId, error);
        }

    }


    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);

        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();

        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        try {
            var photo = processFileService.processPhoto(update.getMessage());
            var link = processFileService.generateLink(photo.getId(), LinkType.GET_PHOTO);
            var answer = "Фото успешно съедено. Вот тебе ссылка для скачивания: " +
                    "(я сам не знаю откуда она взялась...) \n" + link;

            sendAnswer(chatId, answer);
        } catch (UploadFileException e) {
            log.error(e);
            var error = "Загрузка фото не удалась. Повтори попытку позже.";

            sendAnswer(chatId, error);
        }


    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getState();
        if (!appUser.getIsActive()) {
            var error = "Для загрузки контента сначала необходимо зарегистрироваться! " +
                    "Введите /registration.";
            sendAnswer(chatId, error);
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
            var error = "Вам необходимо ввести свою электронную почту, чтобы прододжить. " +
                    "Введите /cancel для отмены команды.";
            sendAnswer(chatId, error);
            return true;
        }
        return false;
    }

    private void sendAnswer(Long chatId, String text) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);

        producerService.produceAnswer(sendMessage);
    }

    private String processServiceCommand(AppUser appUser, String text) {
        var serviceCommand = ServiceCommand.fromValue(text);
        if (REGISTRATION.equals(serviceCommand)) {
            return appUserService.registerUser(appUser);
        } else if (HELP.equals(serviceCommand)) {
            return help();
        } else if (START.equals(serviceCommand)) {
            return "Привет! Я типа бот и мой рацион питания состоит из фотографий и документов. " +
                    "Сюда ты сможешь кидать все свои гигабайты фоток с котиками. " +
                    "Я все это с удовольствием съем, a тебе верну ссылку для скачивания. " +
                    "Чтобы посмотреть список доступных команд введи /help.";
        } else {
            return "Неизвестная команда! Чтобы посмотреть список доступных команд введи /help";
        }
    }

    private String help() {
        return """
                Список доступных команд:
                /cancel - отмена выполнения текущей команды;
                /registration - регистрация пользователя""";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserRepository.save(appUser);
        return "Команда отменена!";
    }

    private AppUser findOrSaveAppUser(Update update) {
        var telegramUser = update.getMessage().getFrom();
        var optionalPersistentAppUser = appUserRepository.findByTelegramUserId(telegramUser.getId());
        if (optionalPersistentAppUser.isEmpty()) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .username(telegramUser.getUserName())
                    .state(BASIC_STATE)
                    .isActive(false)
                    .build();
            return appUserRepository.save(transientAppUser);
        }
        return optionalPersistentAppUser.get();
    }

    private void saveRawData(Update update) {
        var rawData = RawData.builder()
                .event(update)
                .build();
        rawDataRepository.save(rawData);
    }
}
