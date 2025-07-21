package bp.projetbanque.GestionCheque.controllers;

import bp.projetbanque.GestionCheque.entities.Cheque;
import bp.projetbanque.GestionCheque.entities.Scan;
import bp.projetbanque.GestionCheque.repositories.ChequeRepository;
import bp.projetbanque.GestionCheque.services.ScanService;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/scan")
public class ScanController {

    @Autowired
    private ScanService scanService;

    @Autowired
    private ChequeRepository chequeRepository;

    // Page de scan d’un chèque spécifique
    @GetMapping("/upload/{chequeId}")
    public String scanPage(@PathVariable Long chequeId, Model model) {
        Cheque cheque = chequeRepository.findById(chequeId)
                .orElseThrow(() -> new RuntimeException("Chèque non trouvé avec ID: " + chequeId));
        model.addAttribute("cheque", cheque);
        return "scan_form";
    }

    // ✅ Rediriger proprement les accès GET vers l'URL correcte
    @GetMapping("/real/{chequeId}")
    public String redirectToUploadPage(@PathVariable Long chequeId) {
        return "redirect:/scan/upload/" + chequeId;
    }

    // Traitement du scan (appel NAPS2)
    @PostMapping("/real/{chequeId}")
    public String triggerRealScan(@PathVariable Long chequeId,
                                  @RequestParam(defaultValue = "HP300") String profile,
                                  Model model) {
        try {
            Scan scan = scanService.launchRealScan(chequeId, profile);
            model.addAttribute("scan", scan);

            // Convertir l'image en base64 ici
            String base64Image = Base64.getEncoder().encodeToString(scan.getImage());
            model.addAttribute("scanImageBase64", base64Image);

            model.addAttribute("message", "Scan réussi !");
        } catch (Exception e) {
            model.addAttribute("error", "Erreur : " + e.getMessage());
        }

        Cheque cheque = chequeRepository.findById(chequeId)
                .orElseThrow(() -> new RuntimeException("Chèque non trouvé"));
        model.addAttribute("cheque", cheque);
        return "scan_form";
    }
}
