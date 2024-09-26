package com.example.Bank.Account.controller;
import com.example.Bank.Account.Service.ImageProcessingService;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/image")
public class ImageController {
    @Autowired
    private ImageProcessingService imageProcessingService;

    @PostMapping("/extract")
    public ResponseEntity<String> extractTextFromImage(@RequestParam("file") MultipartFile file) {
        try {
            String extractedText = imageProcessingService.extractTextFromImage(file);
            return new ResponseEntity<>(extractedText, HttpStatus.OK);
        } catch (IOException | TesseractException e) {
            return new ResponseEntity<>("Error processing image", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
