package ru.madwey.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import ru.madwey.CryptoTool;
import ru.madwey.entities.AppDocument;
import ru.madwey.entities.AppPhoto;
import ru.madwey.repositories.AppDocumentRepository;
import ru.madwey.repositories.AppPhotoRepository;
import ru.madwey.services.FileService;

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
        //дешифрование id
        var id = cryptoTool.idOf(docId);
        return appDocumentRepository.findById(id);
    }

    @Override
    public Optional<AppPhoto> getPhoto(String photoId) {
        //дешифрование id
        var id = cryptoTool.idOf(photoId);
        return appPhotoRepository.findById(id);
    }
}
