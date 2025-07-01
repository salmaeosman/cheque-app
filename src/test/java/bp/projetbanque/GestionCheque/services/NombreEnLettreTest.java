package bp.projetbanque.GestionCheque.services;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NombreEnLettreTest {

    @Test
    public void testConversionEntierSimple() {
        String resultat = NombreEnLettre.convertir(25);
        assertEquals("vingt-cinq dirhams", resultat);
    }

    @Test
    public void testConversionAvecCentimes() {
        String resultat = NombreEnLettre.convertir(120.75);
        assertEquals("cent vingt dirhams et soixante-quinze centimes", resultat);
    }

    @Test
    public void testConversionZero() {
        String resultat = NombreEnLettre.convertir(0);
        assertEquals("zéro dirhams", resultat);
    }
}
