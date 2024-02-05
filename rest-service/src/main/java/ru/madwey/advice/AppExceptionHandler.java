package ru.madwey.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.madwey.exception.ActivationUserException;
import ru.madwey.exception.IncorrectFileIdException;

@RestControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IncorrectFileIdException.class)
    public ResponseEntity<?> incorrectFileIsExceptionHandler(IncorrectFileIdException ex) {
        return ResponseEntity.badRequest().body("Опа, к сожалению файл не был найден :(");
    }

    @ExceptionHandler(ActivationUserException.class)
    public ResponseEntity<?> activationUserExceptionHandler(ActivationUserException ex) {
        return ResponseEntity.internalServerError().body("Опа, к сожалению регистрация не удалась. " +
                "Попробуйте еще раз позже.");
    }
}
