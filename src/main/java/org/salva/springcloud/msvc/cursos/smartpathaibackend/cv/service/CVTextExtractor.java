package org.salva.springcloud.msvc.cursos.smartpathaibackend.cv.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
public class CVTextExtractor {

    public String extractTextFromPDF(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            log.info("Texto extraído del PDF, longitud: {} caracteres", text.length());
            return text;
        }
    }

    public String extractTextFromDOCX(MultipartFile file) throws IOException {
        // Implementar con Apache POI si necesitas DOCX
        // Por ahora retornar placeholder
        return "DOCX parsing pendiente de implementar";
    }
}

