package bp.projetbanque.GestionCheque.controllers;

import bp.projetbanque.GestionCheque.entities.Cheque;
import bp.projetbanque.GestionCheque.repositories.ChequeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class FiltreController {

    @Autowired
    private ChequeRepository chequeRepository;

    @GetMapping("/filtre")
    public String afficherPageFiltre() {
        return "filtre";
    }

    @GetMapping("/filtre-resultats")
    public String filtrerCheques(
            @RequestParam(required = false) String cheque,
            @RequestParam(required = false) String beneficiaire,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Double montant,
            Model model
    ) {
        List<Cheque> cheques = chequeRepository.findAll();

        List<Cheque> resultats = cheques.stream()
                .filter(c -> cheque == null || cheque.isBlank() ||
                        ((c.getNomCheque() != null ? c.getNomCheque() : "") +
                         (c.getNomSerie() != null ? c.getNomSerie() : "") +
                         (c.getNumeroSerie() != null ? c.getNumeroSerie() : ""))
                         .equalsIgnoreCase(cheque))
                .filter(c -> beneficiaire == null || beneficiaire.isBlank() ||
                        (c.getBeneficiaire() != null && c.getBeneficiaire().equalsIgnoreCase(beneficiaire)))
                .filter(c -> date == null || (c.getDate() != null && c.getDate().equals(date)))
                .filter(c -> montant == null || Math.abs(c.getMontant() - montant) < 0.01)
                .toList();

        model.addAttribute("resultats", resultats);
        return "filtre";
    }
}
