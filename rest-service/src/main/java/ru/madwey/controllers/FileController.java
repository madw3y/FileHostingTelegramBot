package ru.madwey.controllers;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.madwey.services.FileService;

import java.io.IOException;

@Log4j
@RequiredArgsConstructor
@RequestMapping("/file")
@RestController
public class FileController {

    private final FileService fileService;


    @GetMapping("/get-doc")
    public void getDoc(@RequestParam("id") String id, HttpServletResponse response) {
        //TODO для формирования BadRequest добавить ControllerAdvise
        var optionalAppDocument = fileService.getDocument(id);
        if (optionalAppDocument.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        //response.setContentType(MediaType.parseMediaType(doc.get().getMimeType()).toString());
        response.setHeader("Content-disposition", "attachment");
        response.setStatus(HttpServletResponse.SC_OK);

        var binaryContent = optionalAppDocument.get().getBinaryContent();

        try (ServletOutputStream outputStream = response.getOutputStream()){
            outputStream.write(binaryContent.getFileAsArrayOfBytes());
        } catch (IOException e) {
            log.error(e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get-photo")
    public void getPhoto(@RequestParam("id") String id, HttpServletResponse response) {
        //TODO для формирования BadRequest добавить ControllerAdvise
        var optionalAppPhoto = fileService.getPhoto(id);
        if (optionalAppPhoto.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        //response.setContentType(MediaType.IMAGE_JPEG.toString());
        response.setHeader("Content-disposition", "attachment");
        response.setStatus(HttpServletResponse.SC_OK);

        var binaryContent = optionalAppPhoto.get().getBinaryContent();

        try (ServletOutputStream outputStream = response.getOutputStream()){
            outputStream.write(binaryContent.getFileAsArrayOfBytes());
        } catch (IOException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }
}
