package ru.madwey.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.madwey.CryptoTool;
import ru.madwey.dto.MailParams;
import ru.madwey.entity.AppUser;
import ru.madwey.entity.enums.UserState;
import ru.madwey.repository.AppUserRepository;
import ru.madwey.service.AppUserService;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

@Log4j
@RequiredArgsConstructor
@Service
public class AppUserServiceImpl implements AppUserService {
    private final AppUserRepository appUserRepository;

    private final CryptoTool cryptoTool;

    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.queues.registration-mail}")
    private String registrationMailQueue;


    @Override
    public String registerUser(AppUser appUser) {
        if (appUser.getIsActive()) {
            return "Вы уже зарегистрированы";
        } else if (appUser.getEmail() != null) {
            return "Вам на почту уже отправлено письмо. " +
                    "Перейдите по ссылке в письме для подтверждения регистрации.";
        }
        appUser.setState(UserState.WAIT_FOR_EMAIL_STATE);
        appUserRepository.save(appUser);
        return "Для начала введите ваш email.";
    }

    @Override
    public String setEmail(AppUser appUser, String email) {
        try {
            var emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException e) {
            return "Введите корректный email. Для отмены команды введите /cancel";
        }
        var optionalAppUser = appUserRepository.findByEmail(email);
        if (optionalAppUser.isEmpty()) {
            appUser.setEmail(email);
            appUser.setState(UserState.BASIC_STATE);
            appUser = appUserRepository.save(appUser);

            var cryptoUserId = cryptoTool.hashOf(appUser.getId());
            sendRequestToMailService(cryptoUserId, email);

            return "Вам на почту отправлено письмо. " +
                    "Перейдите по ссылке в письме для подтверждения регистрации.";
        } else {
            return "Данный email уже используется. Введите корректный email. " +
                    "Для отмены команды введите /cancel";
        }
    }

    private void sendRequestToMailService(String cryptoUserId, String email) {
        var mailParams = MailParams.builder()
                .id(cryptoUserId)
                .emailTo(email)
                .build();

        rabbitTemplate.convertAndSend(registrationMailQueue, mailParams);

    }
}
