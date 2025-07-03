package bp.projetbanque.GestionCheque.controllers;

import bp.projetbanque.GestionCheque.entities.Client;
import bp.projetbanque.GestionCheque.entities.Cheque;
import bp.projetbanque.GestionCheque.repositories.ClientRepository;
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
    private ClientRepository clientRepository;

    @Autowired
    private ChequeRepository chequeRepository;

    @Autowired
    private MontantEnLettresService montantService;

    // ✅ Affiche le formulaire de saisie du chèque
    @GetMapping("/formulaire")
    public String afficherFormulaire() {
        return "formulaire"; // → src/main/resources/templates/formulaire.html
    }

    // ✅ Affiche la vue du chèque avec les données fournies (prévisualisation)
    @GetMapping("/afficher")
    public String afficherCheque(
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam String numeroCompte,
            @RequestParam double montant,
            @RequestParam String langue,
            Model model
    ) {
        Optional<Client> clientOpt = clientRepository.findByNomAndPrenom(nom, prenom);

        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            if (!client.getNumeroCompte().equals(numeroCompte)) {
                model.addAttribute("erreur", "Numéro de compte incorrect.");
                return "erreur";
            }

            model.addAttribute("client", client);
            model.addAttribute("montant", montant);
            model.addAttribute("montantLettre", montantService.convertirMontant(montant, langue));
            return "cheque"; // → src/main/resources/templates/cheque.html
        } else {
            model.addAttribute("erreur", "Client non trouvé.");
            return "erreur";
        }
    }

    // ✅ Enregistre le chèque dans la base de données
    @PostMapping("/enregistrer")
    public String enregistrerCheque(
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam String numeroCompte,
            @RequestParam String nomCheque,
            @RequestParam double montant,
            @RequestParam String ville,
            Model model
    ) {
        Optional<Client> clientOpt = clientRepository.findByNomAndPrenom(nom, prenom);

        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            if (!client.getNumeroCompte().equals(numeroCompte)) {
                model.addAttribute("erreur", "Numéro de compte incorrect.");
                return "erreur";
            }

            Cheque cheque = new Cheque();
            cheque.setNomCheque(nomCheque);
            cheque.setMontant(montant);
            cheque.setVille(ville);
            cheque.setDate(LocalDate.now());
            cheque.setSerieNo(generateSerieNo());
            cheque.setBeneficiaire(client);

            chequeRepository.save(cheque);

            model.addAttribute("cheque", cheque);
            model.addAttribute("client", client);
            return "cheque"; // → src/main/resources/templates/cheque.html
        } else {
            model.addAttribute("erreur", "Client non trouvé.");
            return "erreur";
        }
    }

    // ✅ Génération d’un numéro de série unique
    private String generateSerieNo() {
        return "A" + System.currentTimeMillis(); // Ex : A162738291
    }
}
