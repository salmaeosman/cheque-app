// ChequeMvcController.java
package bp.projetbanque.GestionCheque.controllers;

import bp.projetbanque.GestionCheque.entities.Cheque;
import bp.projetbanque.GestionCheque.repositories.ChequeRepository;
import bp.projetbanque.GestionCheque.services.MontantEnLettresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/cheque")
public class ChequeMvcController {

    @Autowired
    private ChequeRepository chequeRepository;

    @Autowired
    private MontantEnLettresService montantService;

    @GetMapping("/formulaire")
    public String afficherFormulaire(Model model) {
        model.addAttribute("cheque", new Cheque());
        return "formulaire";
    }

    @PostMapping("/enregistrer")
    public String enregistrerCheque(
            @RequestParam String beneficiaire,
            @RequestParam double montant,
            @RequestParam String ville,
            @RequestParam String langue,
            @RequestParam String nomCheque,
            @RequestParam String nomSerie,
            @RequestParam Long numeroSerie,
            @RequestParam("date") String dateStr,
            Model model
    ) {
        String nomChequeUpper = nomCheque.toUpperCase();
        String nomSerieUpper = nomSerie.toUpperCase();

        if (chequeRepository.findByNomChequeAndNomSerieAndNumeroSerie(nomChequeUpper, nomSerieUpper, numeroSerie).isPresent()) {
            return "redirect:/cheque/formulaire?erreur=existedeja";
        }

        if (beneficiaire == null || beneficiaire.isBlank()) {
            return "redirect:/cheque/formulaire?erreur=beneficiaire";
        }

        Cheque cheque = new Cheque();
        cheque.setMontant(montant);
        cheque.setVille(ville);
        cheque.setDate(LocalDate.parse(dateStr));
        cheque.setNomCheque(nomChequeUpper);
        cheque.setNomSerie(nomSerieUpper);
        cheque.setNumeroSerie(numeroSerie);
        cheque.setBeneficiaire(beneficiaire.toUpperCase());

        chequeRepository.save(cheque);

        // Redirection avec paramètre de langue pour réaffichage correct
        return "redirect:/cheque/afficher/" + cheque.getId() + "?langue=" + langue;
    }
    @PostMapping("/modifier")
    public String modifierCheque(@ModelAttribute Cheque cheque) {
        chequeRepository.save(cheque);
        return "redirect:/cheque/afficher/" + cheque.getId() + "?langue=fr";
    }

    @GetMapping("/afficher/{id}")
    public String afficherCheque(@PathVariable Long id,
                                  @RequestParam(defaultValue = "fr") String langue,
                                  Model model) {
        Cheque cheque = chequeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chèque introuvable"));

        // Vérification de complétude
        if (cheque.getNomCheque() == null ||
            cheque.getNomSerie() == null ||
            cheque.getNumeroSerie() == null ||
            cheque.getVille() == null ||
            cheque.getDate() == null ||
            cheque.getBeneficiaire() == null) {

            return "redirect:/cheque/formulaire?erreur=incomplet";
        }

        model.addAttribute("cheque", cheque);
        model.addAttribute("montant", cheque.getMontant());
        model.addAttribute("langue", langue); // 🔥 Important !
        model.addAttribute("montantLettre", montantService.convertirMontant(cheque.getMontant(), langue));

        return "cheque-impression";
    }
    @GetMapping("/imprimer/{id}")
    public String imprimerCheque(@PathVariable Long id,
                                 @RequestParam(defaultValue = "fr") String langue,
                                 Model model) {
        Cheque cheque = chequeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chèque introuvable"));

        model.addAttribute("cheque", cheque);
        model.addAttribute("montant", cheque.getMontant());
        model.addAttribute("langue", langue);
        model.addAttribute("montantLettre", montantService.convertirMontant(cheque.getMontant(), langue));

        return "cheque-modification";
    }
    @GetMapping("/afficher-par-numero/{numeroSerie}")
    public String afficherParNumero(@PathVariable Long numeroSerie,
                                    @RequestParam(defaultValue = "fr") String langue,
                                    Model model) {
        Cheque cheque = chequeRepository.findByNumeroSerie(numeroSerie)
                .orElseThrow(() -> new RuntimeException("Chèque introuvable"));

        model.addAttribute("cheque", cheque);
        model.addAttribute("montant", cheque.getMontant());
        model.addAttribute("langue", langue);
        model.addAttribute("montantLettre", montantService.convertirMontant(cheque.getMontant(), langue));

        return "cheque-impression";
    }
    @GetMapping("/cheque2/{id}")
    public String afficherCheque2(@PathVariable Long id,
                                   @RequestParam(defaultValue = "fr") String langue,
                                   Model model) {
        Cheque cheque = chequeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chèque introuvable"));

        model.addAttribute("cheque", cheque);
        model.addAttribute("montant", cheque.getMontant());
        model.addAttribute("langue", langue);
        model.addAttribute("montantLettre", montantService.convertirMontant(cheque.getMontant(), langue));

        return "cheque2";
    }


    
}