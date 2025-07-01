package bp.projetbanque.GestionCheque.services;
import org.springframework.stereotype.Service;

@Service
public class MontantEnLettresService {

    public String convertirMontant(double montant, String langue) {
        if ("ar".equalsIgnoreCase(langue)) {
            return NombreArabe.convert(montant);
        } else if ("fr".equalsIgnoreCase(langue)) {
            return NombreEnLettre.convertir(montant);
        } else {
            return "Langue non supportée.";
        }
    }
}