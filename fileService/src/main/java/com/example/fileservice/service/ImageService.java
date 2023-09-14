package com.example.fileservice.service;

import com.example.fileservice.entity.ImageEntity;

public interface ImageService {

     ImageEntity save(ImageEntity imageEntity);

     ImageEntity findByUuid(String uuid);
}
