package ru.madwey.services;

import org.springframework.core.io.FileSystemResource;
import ru.madwey.entities.AppDocument;
import ru.madwey.entities.AppPhoto;
import ru.madwey.entities.BinaryContent;

import java.util.Optional;

public interface FileService {
    Optional<AppDocument> getDocument(String id);
    Optional<AppPhoto> getPhoto(String id);
}
