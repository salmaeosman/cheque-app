package bp.projetbanque.GestionCheque.controllers;

import bp.projetbanque.GestionCheque.entities.Cheque;
import bp.projetbanque.GestionCheque.repositories.ChequeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class FiltreController {

    @Autowired
    private ChequeRepository chequeRepository;

    @GetMapping("/filtre")
    public String afficherPageFiltre() {
        return "filtre"; // charge la page filtre.html
    }

    @GetMapping("/filtre-resultats")
    public String filtrerCheques(
            @RequestParam(required = false) String nomCheque,
            @RequestParam(required = false) String nomSerie,
            @RequestParam(required = false) Long numeroSerie,
            @RequestParam(required = false) Double montant,
            Model model
    ) {
        List<Cheque> cheques = chequeRepository.findAll();

        List<Cheque> resultats = cheques.stream()
                .filter(c -> nomCheque == null || nomCheque.isBlank() || c.getNomCheque().equalsIgnoreCase(nomCheque))
                .filter(c -> nomSerie == null || nomSerie.isBlank() || c.getNomSerie().equalsIgnoreCase(nomSerie))
                .filter(c -> numeroSerie == null || c.getNumeroSerie().equals(numeroSerie))
                .filter(c -> montant == null || Math.abs(c.getMontant() - montant) < 0.01)
                .toList();

        model.addAttribute("resultats", resultats);
        return "filtre"; // même page avec les résultats affichés
    }
}
