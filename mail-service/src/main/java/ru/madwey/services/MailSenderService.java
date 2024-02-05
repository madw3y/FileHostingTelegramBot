package ru.madwey.services;

import ru.madwey.dto.MailParams;

public interface MailSenderService {
    void send(MailParams mailParams);
}
