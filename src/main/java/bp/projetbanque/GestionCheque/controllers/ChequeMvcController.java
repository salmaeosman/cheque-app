package bp.projetbanque.GestionCheque.controllers;

import bp.projetbanque.GestionCheque.entities.Client;
import bp.projetbanque.GestionCheque.repositories.ClientRepository;
import bp.projetbanque.GestionCheque.services.MontantEnLettresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
@RequestMapping("/cheque")
public class ChequeMvcController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private MontantEnLettresService montantService;

    // ✅ Affiche la vue du formulaire
    @GetMapping("/formulaire")
    public String afficherFormulaire() {
        return "formulaire"; // → src/main/resources/templates/formulaire.html
    }

    // ✅ Affiche la vue du chèque avec les données
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
            return "erreur"; // → src/main/resources/templates/erreur.html
        }
    }
}
