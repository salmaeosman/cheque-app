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
            @RequestParam String typeBeneficiaire,
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String prenom,
            @RequestParam(required = false) String raisonSociale,
            @RequestParam double montant,
            @RequestParam String ville,
            @RequestParam String langue,
            @RequestParam String nomCheque,
            @RequestParam String nomSerie,
            @RequestParam Long numeroSerie,
            Model model
    ) {
        String nomChequeUpper = nomCheque.toUpperCase();
        String nomSerieUpper = nomSerie.toUpperCase();

        if (chequeRepository.findByNomChequeAndNomSerieAndNumeroSerie(
                nomChequeUpper, nomSerieUpper, numeroSerie).isPresent()) {
            model.addAttribute("cheque", new Cheque());
            model.addAttribute("erreur", "Une combinaison identique de nom de chèque, nom de série et numéro de série existe déjà.");
            return "formulaire";
        }

        String beneficiaire;
        if ("physique".equals(typeBeneficiaire)) {
            if (nom == null || nom.isBlank() || prenom == null || prenom.isBlank()) {
                model.addAttribute("cheque", new Cheque());
                model.addAttribute("erreur", "Nom et prénom requis pour une personne physique.");
                return "formulaire";
            }
            beneficiaire = nom.toUpperCase() + " " + prenom.toUpperCase();
        } else if ("morale".equals(typeBeneficiaire)) {
            if (raisonSociale == null || raisonSociale.isBlank()) {
                model.addAttribute("cheque", new Cheque());
                model.addAttribute("erreur", "Raison sociale requise pour une personne morale.");
                return "formulaire";
            }
            beneficiaire = raisonSociale.toUpperCase();
        } else {
            model.addAttribute("cheque", new Cheque());
            model.addAttribute("erreur", "Type de bénéficiaire invalide.");
            return "formulaire";
        }

        Cheque cheque = new Cheque();
        cheque.setMontant(montant);
        cheque.setVille(ville);
        cheque.setDate(LocalDate.now());
        cheque.setNomCheque(nomChequeUpper);
        cheque.setNomSerie(nomSerieUpper);
        cheque.setNumeroSerie(numeroSerie);
        cheque.setTypeBeneficiaire(typeBeneficiaire);
        cheque.setBeneficiaire(beneficiaire);

        chequeRepository.save(cheque);

        model.addAttribute("cheque", cheque);
        model.addAttribute("montant", montant);
        model.addAttribute("montantLettre", montantService.convertirMontant(montant, langue));
        model.addAttribute("montantLettreAr", montantService.convertirMontant(montant, "ar"));

        return "cheque";
    }

    @GetMapping("/modifier/{id}")
    public String modifierForm(@PathVariable Long id, Model model) {
        Cheque cheque = chequeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chèque introuvable"));
        model.addAttribute("cheque", cheque);
        return "modifierCheque";
    }

    @PostMapping("/modifier")
    public String modifierCheque(@ModelAttribute Cheque cheque, Model model) {
        Optional<Cheque> optionalOriginal = chequeRepository.findById(cheque.getId());
        if (optionalOriginal.isEmpty()) {
            model.addAttribute("erreur", "Chèque introuvable.");
            return "modifierCheque";
        }

        Cheque original = optionalOriginal.get();

        if (cheque.getDate() != null && cheque.getDate().isBefore(LocalDate.now())) {
            model.addAttribute("cheque", original);
            model.addAttribute("erreur", "Impossible de choisir une date passée.");
            return "modifierCheque";
        }

        String newNomCheque = cheque.getNomCheque() != null ? cheque.getNomCheque().toUpperCase() : original.getNomCheque();
        String newNomSerie = cheque.getNomSerie() != null ? cheque.getNomSerie().toUpperCase() : original.getNomSerie();
        Long newNumeroSerie = cheque.getNumeroSerie() != null ? cheque.getNumeroSerie() : original.getNumeroSerie();

        if (!newNomCheque.equals(original.getNomCheque()) ||
            !newNomSerie.equals(original.getNomSerie()) ||
            !newNumeroSerie.equals(original.getNumeroSerie())) {

            boolean exists = chequeRepository
                    .findByNomChequeAndNomSerieAndNumeroSerie(newNomCheque, newNomSerie, newNumeroSerie)
                    .isPresent();

            if (exists) {
                model.addAttribute("cheque", original);
                model.addAttribute("erreur", "Cette combinaison existe déjà.");
                return "modifierCheque";
            }
        }

        if (cheque.getMontant() > 0) {
            original.setMontant(cheque.getMontant());
        }
        if (cheque.getVille() != null && !cheque.getVille().isBlank()) {
            original.setVille(cheque.getVille());
        }
        if (cheque.getDate() != null) {
            original.setDate(cheque.getDate());
        }

        original.setNomCheque(newNomCheque);
        original.setNomSerie(newNomSerie);
        original.setNumeroSerie(newNumeroSerie);
        original.setTypeBeneficiaire(cheque.getTypeBeneficiaire());
        original.setBeneficiaire(cheque.getBeneficiaire());

        chequeRepository.save(original);
        return "redirect:/cheque/afficher/" + original.getId();
    }

    @GetMapping("/afficher/{id}")
    public String afficherCheque(@PathVariable Long id, Model model) {
        Cheque cheque = chequeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chèque introuvable"));

        model.addAttribute("cheque", cheque);
        model.addAttribute("montant", cheque.getMontant());
        model.addAttribute("montantLettre", montantService.convertirMontant(cheque.getMontant(), "fr"));
        model.addAttribute("montantLettreAr", montantService.convertirMontant(cheque.getMontant(), "ar"));

        if (cheque.getNomCheque() == null ||
            cheque.getNomSerie() == null ||
            cheque.getNumeroSerie() == null ||
            cheque.getVille() == null ||
            cheque.getDate() == null ||
            cheque.getBeneficiaire() == null) {

            model.addAttribute("cheque", cheque);
            model.addAttribute("erreur", "Le chèque est incomplet, impossible d'imprimer.");
            return "modifierCheque";
        }

        return "cheque";
    }
}