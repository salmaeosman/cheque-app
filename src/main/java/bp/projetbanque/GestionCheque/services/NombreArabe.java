package bp.projetbanque.GestionCheque.services;

public class NombreArabe {

    private static final String[] units = {
        "", "واحد", "اثنان", "ثلاثة", "أربعة", "خمسة",
        "ستة", "سبعة", "ثمانية", "تسعة", "عشرة", "أحد عشر", "اثنا عشر",
        "ثلاثة عشر", "أربعة عشر", "خمسة عشر", "ستة عشر", "سبعة عشر",
        "ثمانية عشر", "تسعة عشر"
    };

    private static final String[] tens = {
        "", "", "عشرون", "ثلاثون", "أربعون", "خمسون",
        "ستون", "سبعون", "ثمانون", "تسعون"
    };

    public static String convert(int number) {
        if (number == 0) return "صفر";

        if (number < 20) return units[number];

        if (number < 100) {
            int unit = number % 10;
            int ten = number / 10;
            return (unit > 0 ? units[unit] + " و " : "") + tens[ten];
        }

        if (number < 1000) {
            int rem = number % 100;
            int hundred = number / 100;
            String hundredStr = switch (hundred) {
                case 1 -> "مائة";
                case 2 -> "مئتان";
                case 3, 4, 5, 6, 7, 8, 9 -> units[hundred] + " مائة";
                default -> "";
            };
            return hundredStr + (rem > 0 ? " و " + convert(rem) : "");
        }

        return "قيمة كبيرة جداً";
    }
}