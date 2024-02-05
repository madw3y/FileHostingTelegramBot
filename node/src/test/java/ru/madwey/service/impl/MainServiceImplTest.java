package ru.madwey.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.madwey.entity.RawData;
import ru.madwey.repository.RawDataRepository;

import java.util.HashSet;
import java.util.Set;

@SpringBootTest
public class MainServiceImplTest {
    private final RawDataRepository rawDataRepository;

    public MainServiceImplTest(RawDataRepository rawDataRepository) {
        this.rawDataRepository = rawDataRepository;
    }

    @Test
    public void testSaveRawData() {
        Update update = new Update();
        Message message = new Message();
        message.setText("hello");
        update.setMessage(message);

        RawData rawData = RawData.builder()
                .event(update)
                .build();
        Set<RawData> testData = new HashSet<>();
        rawDataRepository.save(rawData);

        testData.add(rawData);

        Assert.isTrue(testData.contains(rawData), "Entity not found in the set");
    }
}
