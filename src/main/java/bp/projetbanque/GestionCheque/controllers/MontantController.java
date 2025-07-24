package bp.projetbanque.GestionCheque.controllers;

import bp.projetbanque.GestionCheque.services.MontantEnLettresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/montant")
public class MontantController {

    @Autowired
    private MontantEnLettresService montantService;

    @GetMapping("/lettres")
    public String convertirMontant(@RequestParam double montant, @RequestParam String langue) {
        return montantService.convertirMontant(montant, langue);
    }
}
