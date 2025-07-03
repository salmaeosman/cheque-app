package bp.projetbanque.GestionCheque.controllers;

import bp.projetbanque.GestionCheque.entities.Cheque;
import bp.projetbanque.GestionCheque.entities.Client;
import bp.projetbanque.GestionCheque.repositories.ChequeRepository;
import bp.projetbanque.GestionCheque.repositories.ClientRepository;
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
    private ClientRepository clientRepository;

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
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam String numeroCompte,
            @RequestParam double montant,
            @RequestParam String ville,
            @RequestParam String langue,
            @RequestParam String nomCheque,
            @RequestParam String nomSerie,
            @RequestParam Long numeroSerie, // ✅ Type Long (pas String)
            Model model
    ) {
        Optional<Client> clientOpt = clientRepository.findByNomAndPrenom(nom, prenom);
        if (clientOpt.isEmpty()) {
            model.addAttribute("erreur", "Client non trouvé.");
            return "erreur";
        }

        String nomChequeUpper = nomCheque.toUpperCase();
        String nomSerieUpper = nomSerie.toUpperCase();

        if (chequeRepository.findByNomChequeAndNomSerieAndNumeroSerie(
                nomChequeUpper, nomSerieUpper, numeroSerie).isPresent()) {
            model.addAttribute("erreur", "Une combinaison identique de nom de chèque, nom de série et numéro de série existe déjà.");
            return "erreur";
        }

        Client client = clientOpt.get();
        if (!client.getNumeroCompte().equals(numeroCompte)) {
            model.addAttribute("erreur", "Numéro de compte incorrect.");
            return "erreur";
        }

        Cheque cheque = new Cheque();
        cheque.setMontant(montant);
        cheque.setVille(ville);
        cheque.setDate(LocalDate.now());
        cheque.setNomCheque(nomChequeUpper);
        cheque.setNomSerie(nomSerieUpper);
        cheque.setNumeroSerie(numeroSerie); // ✅ Long
        cheque.setBeneficiaire(client);

        chequeRepository.save(cheque);

        model.addAttribute("cheque", cheque);
        model.addAttribute("client", client);
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
        Cheque original = chequeRepository.findById(cheque.getId())
                .orElseThrow(() -> new RuntimeException("Chèque introuvable"));

        if (cheque.getDate().isBefore(LocalDate.now())) {
            model.addAttribute("erreur", "Impossible de choisir une date passée.");
            model.addAttribute("cheque", original);
            return "modifierCheque";
        }

        // Mise à jour avec majuscules
        original.setNomCheque(cheque.getNomCheque().toUpperCase());
        original.setNomSerie(cheque.getNomSerie().toUpperCase());
        original.setNumeroSerie(cheque.getNumeroSerie());
        original.setMontant(cheque.getMontant());
        original.setVille(cheque.getVille());
        original.setDate(cheque.getDate());

        chequeRepository.save(original);
        return "redirect:/cheque/afficher/" + original.getId();
    }

    @GetMapping("/afficher/{id}")
    public String afficherCheque(@PathVariable Long id, Model model) {
        Cheque cheque = chequeRepository.findById(id).orElseThrow();
        model.addAttribute("cheque", cheque);
        model.addAttribute("client", cheque.getBeneficiaire());
        model.addAttribute("montant", cheque.getMontant());
        model.addAttribute("montantLettre", montantService.convertirMontant(cheque.getMontant(), "fr"));
        model.addAttribute("montantLettreAr", montantService.convertirMontant(cheque.getMontant(), "ar"));

        if (cheque.getNomCheque() == null || cheque.getNomSerie() == null || cheque.getNumeroSerie() == null || cheque.getVille() == null || cheque.getDate() == null) {
            model.addAttribute("erreur", "Le chèque est incomplet, impossible d'imprimer.");
            return "erreur";
        }

        return "cheque";
    }
}
