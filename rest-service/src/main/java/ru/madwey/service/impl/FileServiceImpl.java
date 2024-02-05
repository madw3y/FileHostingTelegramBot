package ru.madwey.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import ru.madwey.CryptoTool;
import ru.madwey.entity.AppDocument;
import ru.madwey.entity.AppPhoto;
import ru.madwey.exception.IncorrectFileIdException;
import ru.madwey.repository.AppDocumentRepository;
import ru.madwey.repository.AppPhotoRepository;
import ru.madwey.service.FileService;

import java.util.Optional;

@Log4j
@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {

    private final AppDocumentRepository appDocumentRepository;

    private final AppPhotoRepository appPhotoRepository;

    private final CryptoTool cryptoTool;

    @Override
    public Optional<AppDocument> getDocument(String docId) {
        var id = decodeFileId(docId);
        return appDocumentRepository.findById(id);
    }

    @Override
    public Optional<AppPhoto> getPhoto(String photoId) {
        var id = decodeFileId(photoId);
        return appPhotoRepository.findById(id);
    }

    private Long decodeFileId(String fileId) {
        var id = cryptoTool.idOf(fileId);
        if (id == null) {
            log.error("Неверный id переданного файла");
            throw new IncorrectFileIdException();
        }
        return id;
    }
}
