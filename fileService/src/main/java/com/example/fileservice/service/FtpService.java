package com.example.fileservice.service;


import com.example.fileservice.entity.ImageEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface FtpService {

        ImageEntity uploadFileToFtp(MultipartFile file) throws IOException;

        boolean deleteFile(String path) throws IOException;

        ByteArrayOutputStream getFile(ImageEntity imageEntity) throws IOException;
}
