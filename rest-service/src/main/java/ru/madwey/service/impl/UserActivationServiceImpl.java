package ru.madwey.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.madwey.CryptoTool;
import ru.madwey.repository.AppUserRepository;
import ru.madwey.service.UserActivationService;

@RequiredArgsConstructor
@Service
public class UserActivationServiceImpl implements UserActivationService {
    private final AppUserRepository appUserRepository;

    private final CryptoTool cryptoTool;

    @Override
    public boolean activation(String cryptoUserId) {
        var userId = cryptoTool.idOf(cryptoUserId);
        var optional = appUserRepository.findById(userId);
        if (optional.isPresent()) {
            var user = optional.get();
            user.setIsActive(true);
            appUserRepository.save(user);
            return true;
        }
        return false;
    }
}
