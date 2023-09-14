package com.example.fileservice.service.implementations;

import com.example.fileservice.entity.ImageEntity;
import com.example.fileservice.repository.ImageRepository;
import com.example.fileservice.service.FtpService;
import com.example.fileservice.service.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@EnableScheduling
@Slf4j
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final FtpService ftpService;


    @Override
    public ImageEntity save(ImageEntity imageEntity) {
        return imageRepository.saveAndFlush(imageEntity);
    }

    @Override
    public ImageEntity findByUuid(String uuid) {
        return imageRepository.findByUuid(uuid).orElse(null);
    }

    @Scheduled(cron = "0 0 1 * * ?")
    public void cleanImages(){
        imageRepository.findNotUsedImages().forEach( value -> {
            try {
                ftpService.deleteFile(value.getPath());
                imageRepository.delete(value);
            } catch(IOException e) {
                log.warn("Cannot delete" + value.getUuid());
            }
        });
    }
}
