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

    private static final String[] scales = {
        "", "ألف", "مليون", "مليار"
    };

    public static String convert(long number) {
        if (number == 0) return "صفر درهم";

        String[] parts = new String[scales.length];
        int scaleIndex = 0;

        long tempNumber = number;

        while (tempNumber > 0 && scaleIndex < scales.length) {
            int segment = (int) (tempNumber % 1000);
            if (segment > 0) {
                String segmentText = convertLessThan1000(segment);

                if (segment == 1 && scaleIndex > 0) {
                    segmentText = ""; // éviter "واحد ألف"
                }

                if (scaleIndex > 0) {
                    if (segmentText.isEmpty()) {
                        segmentText = getScaleName(segment, scaleIndex);
                    } else {
                        segmentText += " " + getScaleName(segment, scaleIndex);
                    }
                }

                parts[scaleIndex] = segmentText;
            }
            tempNumber /= 1000;
            scaleIndex++;
        }

        // Assembler les segments
        StringBuilder result = new StringBuilder();
        for (int i = parts.length - 1; i >= 0; i--) {
            if (parts[i] != null && !parts[i].isEmpty()) {
                if (result.length() > 0) {
                    result.append(" و ");
                }
                result.append(parts[i]);
            }
        }

        // Ajouter le mot "درهم" avec la bonne terminaison
        result.append(" ").append(getCurrencyWord(number));

        return result.toString();
    }

    private static String convertLessThan1000(int number) {
        if (number == 0) return "";

        if (number < 20) return units[number];

        if (number < 100) {
            int unit = number % 10;
            int ten = number / 10;
            return (unit > 0 ? units[unit] + " و " : "") + tens[ten];
        }

        int rem = number % 100;
        int hundred = number / 100;

        String hundredStr = switch (hundred) {
            case 1 -> "مائة";
            case 2 -> "مئتان";
            case 3, 4, 5, 6, 7, 8, 9 -> units[hundred] + " مائة";
            default -> "";
        };

        if (rem > 0) {
            return hundredStr + " و " + convertLessThan1000(rem);
        } else {
            return hundredStr;
        }
    }

    private static String getScaleName(int number, int scaleIndex) {
        if (scaleIndex == 1) { // ألف
            if (number == 1) return "ألف";
            if (number == 2) return "ألفان";
            if (number >= 3 && number <= 10) return units[number] + " آلاف";
            return "ألف";
        }

        if (scaleIndex == 2) { // مليون
            if (number == 1) return "مليون";
            if (number == 2) return "مليونان";
            if (number >= 3 && number <= 10) return units[number] + " ملايين";
            return "مليون";
        }

        if (scaleIndex == 3) { // مليار
            if (number == 1) return "مليار";
            if (number == 2) return "ملياران";
            if (number >= 3 && number <= 10) return units[number] + " مليارات";
            return "مليار";
        }

        return "";
    }

    private static String getCurrencyWord(long number) {
        if (number == 1) return "درهم واحد";
        if (number == 2) return "درهمان";

        long lastTwoDigits = number % 100;
        if (lastTwoDigits >= 3 && lastTwoDigits <= 10) {
            return "دراهم";
        } else if (lastTwoDigits >= 11 && lastTwoDigits <= 99) {
            return "درهمًا";
        } else {
            return "درهم";
        }
    }
}