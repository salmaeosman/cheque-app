package bp.projetbanque.GestionCheque.controllers;
import bp.projetbanque.GestionCheque.entities.Client;
import bp.projetbanque.GestionCheque.repositories.ClientRepository;
import bp.projetbanque.GestionCheque.services.MontantEnLettresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/cheque")
public class ChequeController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private MontantEnLettresService montantService;

    @GetMapping("/rechercher")
    public Map<String, Object> rechercherClientEtConvertirMontant(
        @RequestParam String nom,
        @RequestParam String prenom,
        @RequestParam int montant,
        @RequestParam String langue
    ) {
        Map<String, Object> response = new HashMap<>();

        Optional<Client> clientOpt = clientRepository.findByNomAndPrenom(nom, prenom);

        if (clientOpt.isPresent()) {
            Client client = clientOpt.get();
            response.put("client", client);
            response.put("montantChiffre", montant);
            response.put("montantLettre", montantService.convertirMontant(montant, langue));
        } else {
            response.put("erreur", "Client non trouvé");
        }

        return response;
    }
}