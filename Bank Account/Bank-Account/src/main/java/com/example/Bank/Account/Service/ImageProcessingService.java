package com.example.Bank.Account.Service;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

@Service
public class ImageProcessingService {
    public String extractTextFromImage(MultipartFile image) throws IOException, TesseractException {
        // Convert MultipartFile to BufferedImage
        BufferedImage bufferedImage = ImageIO.read(image.getInputStream());

        // Handle temp file creation
        File tempFile = File.createTempFile("image", "." + getFileExtension(image.getOriginalFilename()));
        ImageIO.write(bufferedImage, getFileExtension(image.getOriginalFilename()), tempFile);

        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("C:\\Program Files (x86)\\Tesseract-OCR\\tessdata"); // Set the path to tessdata

        String extractedText = tesseract.doOCR(tempFile);

        tempFile.delete();
        return extractedText;
    }
    private String getFileExtension(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }
}
