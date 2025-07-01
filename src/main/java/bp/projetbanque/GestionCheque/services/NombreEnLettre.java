package bp.projetbanque.GestionCheque.services;

public class NombreEnLettre {

    private static final String[] UNITS = {
        "", "un", "deux", "trois", "quatre", "cinq", "six", "sept", "huit", "neuf",
        "dix", "onze", "douze", "treize", "quatorze", "quinze", "seize",
        "dix-sept", "dix-huit", "dix-neuf"
    };

    private static final String[] TENS = {
        "", "", "vingt", "trente", "quarante", "cinquante", "soixante",
        "soixante", "quatre-vingt", "quatre-vingt"
    };

    public static String convertir(double montant) {
        int entier = (int) montant;
        int centimes = (int) Math.round((montant - entier) * 100);

        String texteEntier = convertirNombre(entier);
        String texteCentimes = centimes > 0 ? convertirNombre(centimes) : "zéro";

        return texteEntier + " dirhams" +
               (centimes > 0 ? " et " + texteCentimes + " centimes" : "");
    }

    private static String convertirNombre(int n) {
        if (n == 0) return "zéro";

        StringBuilder sb = new StringBuilder();

        if (n >= 1_000_000) {
            int millions = n / 1_000_000;
            sb.append(convertirNombre(millions)).append(" million");
            if (millions > 1) sb.append("s");
            n %= 1_000_000;
            if (n > 0) sb.append(" ");
        }

        if (n >= 1000) {
            int milliers = n / 1000;
            if (milliers > 1) sb.append(convertirNombre(milliers)).append(" ");
            sb.append("mille");
            n %= 1000;
            if (n > 0) sb.append(" ");
        }

        if (n >= 100) {
            int centaines = n / 100;
            if (centaines > 1) sb.append(UNITS[centaines]).append(" ");
            sb.append("cent");
            n %= 100;
            if (n > 0) sb.append(" ");
        }

        if (n >= 20) {
            int d = n / 10;
            int u = n % 10;
            sb.append(TENS[d]);
            if (d == 7 || d == 9) {
                sb.append("-").append(UNITS[10 + u]);
            } else if (u == 1 && d != 8) {
                sb.append(" et un");
            } else if (u > 0) {
                sb.append("-").append(UNITS[u]);
            }
        } else if (n > 0) {
            sb.append(UNITS[n]);
        }

        return sb.toString().trim();
    }
}