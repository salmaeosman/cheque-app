package bp.projetbanque.GestionCheque.controllers;

import bp.projetbanque.GestionCheque.entities.Cheque;
import bp.projetbanque.GestionCheque.entities.Scan;
import bp.projetbanque.GestionCheque.repositories.ChequeRepository;
import bp.projetbanque.GestionCheque.repositories.ScanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/scan")
public class ScanController {

    @Autowired
    private ScanRepository scanRepository;

    @Autowired
    private ChequeRepository chequeRepository;

    @GetMapping("/upload/{chequeId}")
    public String afficherFormulaireUpload(@PathVariable Long chequeId, Model model) {
        model.addAttribute("chequeId", chequeId);
        return "upload-scan";
    }

    @PostMapping("/upload")
    public String uploadScan(@RequestParam("chequeId") Long chequeId,
                             @RequestParam("file") MultipartFile file,
                             Model model) {
        try {
            Cheque cheque = chequeRepository.findById(chequeId)
                    .orElseThrow(() -> new RuntimeException("Chèque non trouvé"));

            Scan scan = new Scan();
            scan.setChequeId(chequeId);
            scan.setFileName(file.getOriginalFilename());
            scan.setFileType(file.getContentType());
            scan.setImage(file.getBytes());

            scanRepository.save(scan);

            return "redirect:/scan/afficher/" + chequeId;
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement du scan", e);
        }
    }
    @GetMapping("/afficher/{chequeId}")
    public String afficherScan(@PathVariable Long chequeId, Model model) {
        Scan scan = scanRepository.findByChequeId(chequeId)
                .orElseThrow(() -> new RuntimeException("Scan introuvable pour ce chèque"));

        model.addAttribute("scan", scan);
        return "afficher-scan";
    }
    @GetMapping("/image/{id}")
    @ResponseBody
    public byte[] afficherImage(@PathVariable Long id) {
        Scan scan = scanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Image introuvable"));
        return scan.getImage();
    }
    @GetMapping("/lancer-scan")
    @ResponseBody
    public ResponseEntity<String> lancerScanDepuisServeur(@RequestParam Long chequeId) {
        try {
            // Lancer l'application Java côté client (vous devez rendre cela possible via une app native)
            // Ici, vous pouvez écrire un fichier temporaire, une file d'attente ou autre
            return ResponseEntity.ok("Scan en cours");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur : " + e.getMessage());
        }
    }
}