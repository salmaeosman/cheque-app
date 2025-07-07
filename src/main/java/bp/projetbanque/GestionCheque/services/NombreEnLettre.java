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
        if (montant < 0) {
            throw new IllegalArgumentException("Le montant doit être positif.");
        }
        if (montant > 2_000_000_000.0) {
            throw new IllegalArgumentException("Le montant ne doit pas dépasser 2 milliards de dirhams.");
        }

        long entier = (long) montant;
        int centimes = (int) Math.round((montant - entier) * 100);

        StringBuilder texte = new StringBuilder();

        // Dirhams
        if (entier > 0) {
            texte.append(convertirNombre(entier))
                 .append(entier > 1 ? " dirhams" : " dirham");
        }

        // Centimes — attention à l’ordre !
        if (centimes > 0) {
            if (texte.length() > 0) {
                texte.append(" et ");
            }
            String centTxt = convertirNombre(centimes)
                           + (centimes > 1 ? " centimes" : " centime");
            texte.append(centTxt);
        }

        if (texte.length() == 0) {
            texte.append("zéro dirham");
        }

        return texte.toString();
    }

    private static String convertirNombre(long n) {
        if (n == 0) return "zéro";

        StringBuilder sb = new StringBuilder();

        if (n >= 1_000_000_000L) {
            long milliards = n / 1_000_000_000L;
            sb.append(convertirNombre(milliards))
              .append(milliards > 1 ? " milliards" : " milliard");
            n %= 1_000_000_000L;
            if (n > 0) sb.append(" ");
        }

        if (n >= 1_000_000L) {
            long millions = n / 1_000_000L;
            sb.append(convertirNombre(millions))
              .append(millions > 1 ? " millions" : " million");
            n %= 1_000_000L;
            if (n > 0) sb.append(" ");
        }

        if (n >= 1000L) {
            long milliers = n / 1000L;
            if (milliers > 1) {
                sb.append(convertirNombre(milliers)).append(" ");
            }
            sb.append("mille");
            n %= 1000L;
            if (n > 0) sb.append(" ");
        }

        if (n >= 100L) {
            long centaines = n / 100L;
            if (centaines > 1) {
                sb.append(UNITS[(int) centaines]).append(" ");
            }
            sb.append("cent");
            n %= 100L;
            if (n > 0) sb.append(" ");
        }

        if (n >= 20) {
            int d = (int) (n / 10);
            int u = (int) (n % 10);
            sb.append(TENS[d]);
            if (d == 7 || d == 9) {
                sb.append("-").append(UNITS[10 + u]);
            } else if (u == 1 && d != 8) {
                sb.append(" et un");
            } else if (u > 0) {
                sb.append("-").append(UNITS[u]);
            }
        } else if (n > 0) {
            sb.append(UNITS[(int) n]);
        }

        return sb.toString().trim();
    }
}
