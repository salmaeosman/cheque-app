package bp.projetbanque.GestionCheque.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class DownloadController {

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadApp(HttpServletResponse response) throws IOException {
        String filename = "downloads/cheque-app-1.0.jar"; // chemin relatif dans static/
        ClassPathResource file = new ClassPathResource("static/" + filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getFilename())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.contentLength())
                .body(new InputStreamResource(file.getInputStream()));
    }
}