package bp.projetbanque.GestionCheque.services;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Convertit un montant (≤ 2 000 000 000) en texte (dirhams + centimes),
     * avec "et" uniquement entre dirhams et centimes.
     */
    public static String convertir(double montant) {
        if (montant < 0) {
            throw new IllegalArgumentException("Le montant doit être positif.");
        }
        if (montant > 2_000_000_000.0) {
            throw new IllegalArgumentException("Le montant ne doit pas dépasser 2 000 000 000.");
        }

        long entier = (long) montant;
        int centimes = (int) Math.round((montant - entier) * 100);
        StringBuilder texte = new StringBuilder();

        // Partie dirhams
        if (entier > 0) {
            texte.append(convertirNombre(entier))
                 .append(entier > 1 ? " dirhams" : " dirham");
        }

        // Partie centimes : "et" uniquement ici
        if (centimes > 0) {
            if (texte.length() > 0) {
                texte.append(" et ");
            }
            texte.append(convertirNombre(centimes))
                 .append(centimes > 1 ? " centimes" : " centime");
        }

        if (texte.length() == 0) {
            texte.append("zéro dirham");
        }

        return texte.toString()
                    .replaceAll("\\s+", " ")
                    .trim();
    }

    /**
     * Convertit un entier [0..2 000 000 000] en toutes lettres,
     * supporte milliards / millions / milliers / centaines / dizaines.
     */
    private static String convertirNombre(long n) {
        if (n == 0) {
            return "zéro";
        }
        List<String> parts = new ArrayList<>();

        // Milliards
        if (n >= 1_000_000_000L) {
            long milliards = n / 1_000_000_000L;
            parts.add(convertirNombre(milliards) + (milliards > 1 ? " milliards" : " milliard"));
            n %= 1_000_000_000L;
        }

        // Millions
        if (n >= 1_000_000L) {
            long millions = n / 1_000_000L;
            parts.add(convertirNombre(millions) + (millions > 1 ? " millions" : " million"));
            n %= 1_000_000L;
        }

        // Milliers
        if (n >= 1000L) {
            long milliers = n / 1000L;
            if (milliers > 1) {
                parts.add(convertirNombre(milliers) + " mille");
            } else {
                parts.add("mille");
            }
            n %= 1000L;
        }

        // Centaines, dizaines et unités
        if (n > 0) {
            int rem = (int) n;
            int centaines = rem / 100;
            int reste = rem % 100;
            if (centaines > 0) {
                parts.add((centaines > 1 ? UNITS[centaines] + " cent" : "cent"));
            }
            if (reste > 0) {
                parts.add(convertTwoDigits(reste));
            }
        }

        return String.join(" ", parts);
    }

    /**
     * Gère les nombres de 1 à 99, sans "et" interne, toujours avec hyphens.
     */
    private static String convertTwoDigits(int n) {
        if (n < 20) {
            return UNITS[n];
        }
        int tens = n / 10;
        int unit = n % 10;
        String tensWord = TENS[tens];
        if (tens == 7 || tens == 9) {
            // 70-79, 90-99
            return tensWord + "-" + UNITS[10 + unit];
        } else if (unit > 0) {
            return tensWord + "-" + UNITS[unit];
        } else {
            return tensWord;
        }
    }

    /**
     * Petit test rapide en console
     */
    public static void main(String[] args) {
        double[] tests = {109099.89, 1999999999.98};
        for (double t : tests) {
            System.out.println(t + " → " + convertir(t));
        }
    }
}
