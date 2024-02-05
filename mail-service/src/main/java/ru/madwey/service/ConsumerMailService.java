package ru.madwey.service;

import ru.madwey.dto.MailParams;

public interface ConsumerMailService {
    void consumerRegistrationMail(MailParams mailParams);
}
