package com.example.fileservice.controller;

import com.example.fileservice.entity.ImageResponse;
import com.example.fileservice.mediator.ImageMediator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageMediator imageMediator;
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> saveFile(@RequestBody MultipartFile file){
        return imageMediator.saveImage(file);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<ImageResponse> deleteFile(@RequestParam String uuid) throws IOException {
        return imageMediator.delete(uuid);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getFile(@RequestParam String uuid) throws IOException {
        return imageMediator.getImage(uuid);
    }
    @RequestMapping(method = RequestMethod.PATCH)
    public ResponseEntity<ImageResponse> activateImage(@RequestParam String uuid) {
        return imageMediator.activateImage(uuid);
    }
}
