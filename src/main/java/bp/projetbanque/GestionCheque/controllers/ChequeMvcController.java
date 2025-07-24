package bp.projetbanque.GestionCheque.controllers;

import bp.projetbanque.GestionCheque.entities.Cheque;
import bp.projetbanque.GestionCheque.repositories.ChequeRepository;
import bp.projetbanque.GestionCheque.services.MontantEnLettresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

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
        cheque.setLangue(langue);

        chequeRepository.save(cheque);

        return "redirect:/cheque/afficher/" + cheque.getId() + "?langue=" + cheque.getLangue() + "&success=true";
    }

 // ✅ Affiche formulaire de modification
    @GetMapping("/modifier/{id}")
    public String afficherFormulaireModification(@PathVariable Long id,
                                                 @RequestParam(defaultValue = "fr") String langue,
                                                 Model model) {
        Cheque cheque = chequeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chèque introuvable"));

        model.addAttribute("cheque", cheque);
        model.addAttribute("langue", langue);
        model.addAttribute("montantLettre", montantService.convertirMontant(cheque.getMontant(), langue));
        return "cheque-modification";
    }

    // ✅ Enregistre les modifications d’un chèque existant
    @PostMapping("/modifier/{id}")
    public String modifierCheque(@PathVariable Long id,
                                  @ModelAttribute("cheque") Cheque chequeForm,
                                  @RequestParam String langue,
                                  Model model) {

        Cheque chequeExistant = chequeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chèque introuvable"));

        // Mise à jour des champs modifiables seulement
        chequeExistant.setMontant(chequeForm.getMontant());
        chequeExistant.setVille(chequeForm.getVille());
        chequeExistant.setDate(chequeForm.getDate());
        chequeExistant.setBeneficiaire(chequeForm.getBeneficiaire());
        chequeExistant.setNomCheque(chequeForm.getNomCheque());
        chequeExistant.setNomSerie(chequeForm.getNomSerie());
        chequeExistant.setNumeroSerie(chequeForm.getNumeroSerie());

        chequeRepository.save(chequeExistant);

        return "redirect:/cheque/cheque2/" + id + "?langue=" + langue;
    }

@GetMapping("/afficher/{id}")
    public String afficherCheque(@PathVariable Long id,
                                 @RequestParam(defaultValue = "fr") String langue,
                                 @RequestParam(required = false) Boolean success,
                                 Model model) {
        Cheque cheque = chequeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chèque introuvable"));

        model.addAttribute("cheque", cheque);
        model.addAttribute("montant", cheque.getMontant());
        model.addAttribute("langue", langue);
        model.addAttribute("montantLettre", montantService.convertirMontant(cheque.getMontant(), langue));
        model.addAttribute("success", success != null && success);

        return "cheque-impression";
    }

    @GetMapping("/imprimer/{id}")
    public String imprimerCheque(@PathVariable Long id,
                                 @RequestParam(defaultValue = "fr") String langue,
                                 Model model) {
        Cheque cheque = chequeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chèque introuvable"));

        model.addAttribute("cheque", cheque); // ✅ ID inclus
        model.addAttribute("langue", langue);
        model.addAttribute("montantLettre", montantService.convertirMontant(cheque.getMontant(), langue));

        return "cheque-modification";
    }

    @GetMapping("/cheque2/{id}")
    public String afficherChequeDepuisResultats(@PathVariable Long id,
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
