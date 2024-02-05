package ru.madwey.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.madwey.entity.AppDocument;
import ru.madwey.entity.AppPhoto;
import ru.madwey.service.enums.LinkType;

public interface ProcessFileService {
    AppDocument processDoc(Message externalMessage);
    AppPhoto processPhoto(Message externalMessage);
    String generateLink(Long docId, LinkType linkType);
}
