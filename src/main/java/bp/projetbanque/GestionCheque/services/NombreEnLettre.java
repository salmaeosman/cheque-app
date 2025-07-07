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

if (montant > 2_000_000_000.0) {

throw new IllegalArgumentException("Le montant ne doit pas dépasser 2 milliards de dirhams.");

}



long entier = (long) montant;

int centimes = (int) Math.round((montant - entier) * 100);



StringBuilder texte = new StringBuilder();



if (entier > 0) {

texte.append(convertirNombre(entier)).append(" dirhams");

}



if (centimes > 0) {

if (texte.length() > 0) {

texte.append(" et ");

}

texte.append(convertirNombre(centimes)).append(" centimes");

}



if (texte.length() == 0) {

texte.append("zéro dirham");

}



return texte.toString();

}



private static String convertirNombre(long n) {

if (n == 0) return "zéro";



StringBuilder sb = new StringBuilder();



if (n >= 1_000_000_000) {

long milliards = n / 1_000_000_000;

sb.append(convertirNombre(milliards)).append(" milliard");

if (milliards > 1) sb.append("s");

n %= 1_000_000_000;

if (n > 0) sb.append(" ");

}



if (n >= 1_000_000) {

long millions = n / 1_000_000;

sb.append(convertirNombre(millions)).append(" million");

if (millions > 1) sb.append("s");

n %= 1_000_000;

if (n > 0) sb.append(" ");

}



if (n >= 1000) {

long milliers = n / 1000;

if (milliers > 1) sb.append(convertirNombre(milliers)).append(" ");

sb.append("mille");

n %= 1000;

if (n > 0) sb.append(" ");

}



if (n >= 100) {

long centaines = n / 100;

if (centaines > 1) sb.append(UNITS[(int)centaines]).append(" ");

sb.append("cent");

n %= 100;

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