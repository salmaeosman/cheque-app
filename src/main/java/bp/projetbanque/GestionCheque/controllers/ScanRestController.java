package bp.projetbanque.GestionCheque.controllers;

import bp.projetbanque.GestionCheque.entities.Cheque;
import bp.projetbanque.GestionCheque.entities.Scan;
import bp.projetbanque.GestionCheque.repositories.ChequeRepository;
import bp.projetbanque.GestionCheque.repositories.ScanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/scan")
public class ScanRestController {

    @Autowired
    private ScanRepository scanRepository;

    @Autowired
    private ChequeRepository chequeRepository;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadScan(@RequestParam("chequeId") Long chequeId,
                                             @RequestParam("file") MultipartFile file) {
        try {
            Cheque cheque = chequeRepository.findById(chequeId)
                    .orElseThrow(() -> new RuntimeException("Chèque non trouvé"));

            Scan scan = new Scan();
            scan.setCheque(cheque); // ← ici aussi
            scan.setFileName(file.getOriginalFilename());
            scan.setFileType(file.getContentType());
            scan.setImage(file.getBytes());

            scanRepository.save(scan);

            return ResponseEntity.ok("Scan enregistré avec succès");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Erreur de traitement du fichier");
        }
    }
}
