package ru.madwey.controller;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.madwey.entity.BinaryContent;
import ru.madwey.service.FileService;

import java.io.IOException;

@Log4j
@RequiredArgsConstructor
@RequestMapping("/file")
@RestController
public class FileController {

    private final FileService fileService;

    @GetMapping("/get-doc")
    public void getDoc(@RequestParam("id") String id, HttpServletResponse response) {
        var optionalAppDocument = fileService.getDocument(id);

        var binaryContent = optionalAppDocument.get().getBinaryContent();

        downloadFile(binaryContent, response);
    }

    @GetMapping("/get-photo")
    public void getPhoto(@RequestParam("id") String id, HttpServletResponse response) {
        var optionalAppPhoto = fileService.getPhoto(id);

        var binaryContent = optionalAppPhoto.get().getBinaryContent();

        downloadFile(binaryContent, response);
    }

    private void downloadFile(BinaryContent binaryContent, HttpServletResponse response) {
        response.setHeader("Content-disposition", "attachment");
        response.setStatus(HttpServletResponse.SC_OK);

        try (ServletOutputStream outputStream = response.getOutputStream()){
            outputStream.write(binaryContent.getFileAsArrayOfBytes());
        } catch (IOException e) {
            log.error(e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
