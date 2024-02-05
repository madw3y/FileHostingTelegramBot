package ru.madwey.services;

import ru.madwey.dto.MailParams;

public interface ConsumerMailService {
    void consumerRegistrationMail(MailParams mailParams);
}
