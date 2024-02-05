package ru.madwey.service;

import ru.madwey.entity.AppDocument;
import ru.madwey.entity.AppPhoto;

import java.util.Optional;

public interface FileService {
    Optional<AppDocument> getDocument(String id);
    Optional<AppPhoto> getPhoto(String id);
}
