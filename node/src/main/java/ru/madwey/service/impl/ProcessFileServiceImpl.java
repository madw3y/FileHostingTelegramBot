package ru.madwey.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.madwey.CryptoTool;
import ru.madwey.entity.AppDocument;
import ru.madwey.entity.AppPhoto;
import ru.madwey.entity.BinaryContent;
import ru.madwey.exception.UploadFileException;
import ru.madwey.repository.AppDocumentRepository;
import ru.madwey.repository.AppPhotoRepository;
import ru.madwey.repository.BinaryContentRepository;
import ru.madwey.service.ProcessFileService;
import ru.madwey.service.enums.LinkType;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Log4j
@RequiredArgsConstructor
@Service
public class ProcessFileServiceImpl implements ProcessFileService {
    @Value("${token}")
    private String token;

    @Value("${service.file_info.uri}")
    private String fileInfoUri;

    @Value("${service.file_storage.uri}")
    private String fileStorageUri;

    @Value("${link.address}")
    private String linkAddress;

    private final AppDocumentRepository appDocumentRepository;

    private final BinaryContentRepository binaryContentRepository;

    private final AppPhotoRepository appPhotoRepository;

    private final CryptoTool cryptoTool;


    @Override
    public AppDocument processDoc(Message externalMessage) {
        var fileId = externalMessage.getDocument().getFileId();
        var response = getFileInfo(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            var persistentBinaryContent = getPersistentBinaryContent(response);
            var telegramDoc = externalMessage.getDocument();
            var transientAppDoc = AppDocument.builder()
                    .telegramFileId(telegramDoc.getFileId())
                    .docName(telegramDoc.getFileName())
                    .fileSize(telegramDoc.getFileSize())
                    .mimeType(telegramDoc.getMimeType())
                    .binaryContent(persistentBinaryContent)
                    .build();
            return appDocumentRepository.save(transientAppDoc);
        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }


    @Override
    public AppPhoto processPhoto(Message externalMessage) {
        var photoSizeCount = externalMessage.getPhoto().size();
        var photoIndex = photoSizeCount > 1 ? externalMessage.getPhoto().size() - 1 : 0;
        var telegramPhoto = externalMessage.getPhoto().get(photoIndex);
        var fileId = telegramPhoto.getFileId();
        var response = getFileInfo(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            var persistentBinaryContent = getPersistentBinaryContent(response);
            var transientAppPhoto = AppPhoto.builder()
                    .binaryContent(persistentBinaryContent)
                    .fileSize(telegramPhoto.getFileSize())
                    .telegramFileId(fileId)
                    .build();
            return appPhotoRepository.save(transientAppPhoto);
        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
    }

    @Override
    public String generateLink(Long docId, LinkType linkType) {
        var hash = cryptoTool.hashOf(docId);
        return "http://" + linkAddress + "/" + linkType + "?id=" + hash;
    }

    private BinaryContent getPersistentBinaryContent(ResponseEntity<String> response) {
        var jsonObject = new JSONObject(response.getBody());
        var filePath = String.valueOf(jsonObject
                .getJSONObject("result")
                .getString("file_path"));;
        byte[] fileInByte = downloadFile(filePath);
        var transientBinaryContent = BinaryContent.builder()
                .fileAsArrayOfBytes(fileInByte)
                .build();
        return binaryContentRepository.save(transientBinaryContent);
    }

    private byte[] downloadFile(String filePath) {
        var fullUri = fileStorageUri.replace("{token}", token)
                .replace("{filePath}", filePath);
        URL url;
        try {
            url = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new UploadFileException(e);
        }

        //TODO оптимизировать скачивание файлов
        try (InputStream is = url.openStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            throw new UploadFileException(url.toExternalForm(), e);
        }
    }

    private ResponseEntity<String> getFileInfo(String fileId) {
        var restTemplate = new RestTemplate();
        var httpHeaders = new HttpHeaders();
        var request = new HttpEntity<>(httpHeaders);

        return restTemplate.exchange(
                fileInfoUri,
                HttpMethod.GET,
                request,
                String.class,
                token, fileId
        );
    }
}
