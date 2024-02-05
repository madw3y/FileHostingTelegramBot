package ru.madwey.services;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.madwey.entities.AppDocument;
import ru.madwey.entities.AppPhoto;
import ru.madwey.services.enums.LinkType;

public interface ProcessFileService {
    AppDocument processDoc(Message externalMessage);
    AppPhoto processPhoto(Message externalMessage);
    String generateLink(Long docId, LinkType linkType);
}
