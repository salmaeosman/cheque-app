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
    public String afficherFormulaire() {
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
        Model model
    ) {
        Optional<Client> clientOpt = clientRepository.findByNomAndPrenom(nom, prenom);

        if (clientOpt.isEmpty()) {
            model.addAttribute("erreur", "Client non trouvé.");
            return "erreur";
        }

        Client client = clientOpt.get();

        if (!client.getNumeroCompte().equals(numeroCompte)) {
            model.addAttribute("erreur", "Numéro de compte incorrect.");
            return "erreur";
        }

        // Générer nom du chèque auto-incrémenté
        long count = chequeRepository.count() + 1;
        String nomCheque = String.format("CHEQUE-%06d", count);

        Cheque cheque = new Cheque();
        cheque.setMontant(montant);
        cheque.setVille(ville);
        cheque.setDate(LocalDate.now());
        cheque.setSerieNo(generateSerieNo());
        cheque.setBeneficiaire(client);
        cheque.setNomCheque(nomCheque);

        chequeRepository.save(cheque);

        model.addAttribute("cheque", cheque);
        model.addAttribute("client", client);
        model.addAttribute("montant", montant);
        model.addAttribute("montantLettre", montantService.convertirMontant(montant, langue));
        model.addAttribute("montantLettreAr", montantService.convertirMontant(montant, "ar"));

        return "cheque";
    }

    private String generateSerieNo() {
        return "A" + System.currentTimeMillis();
    }
}