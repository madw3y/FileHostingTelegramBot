package ru.madwey.service;

import ru.madwey.dto.MailParams;

public interface MailSenderService {
    void send(MailParams mailParams);
}
