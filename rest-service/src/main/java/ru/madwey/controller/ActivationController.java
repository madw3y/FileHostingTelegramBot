package ru.madwey.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.madwey.exception.ActivationUserException;
import ru.madwey.service.UserActivationService;

@RequiredArgsConstructor
@RequestMapping("/user")
@RestController
public class ActivationController {
    private final UserActivationService userActivationService;

    @GetMapping("/activation")
    public ResponseEntity<?> activation(@RequestParam("id") String id) {
        var res = userActivationService.activation(id);
        if (res) {
            return ResponseEntity.ok().body("Регистрация успешно завершена!");
        }
        throw new ActivationUserException();
    }
}
