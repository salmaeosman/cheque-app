package bp.projetbanque.GestionCheque.services;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

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

    public static final String[] scales = {
        "", "ألف", "مليون", "مليار", "ترليون"
    };

    public static String convertDecimal(double number) {
        if (number > 9999999999999.99) {
            return "المبلغ يتجاوز الحد الأقصى المسموح به";
        }

        long integerPart = (long) number;
        int fractionalPart = (int) Math.round((number - integerPart) * 100);

        String result = convert(integerPart);

        if (fractionalPart > 0) {
            result += " و " + convertFractional(fractionalPart);
        }

        return result;
    }

    public static String convert(double montant) {
        if (montant == 0) return "صفر درهم";

        String[] parts = new String[scales.length];
        int scaleIndex = 0;
        long tempNumber = (long) montant;

        while (tempNumber > 0 && scaleIndex < scales.length) {
            int segment = (int) (tempNumber % 1000);
            if (segment > 0) {
                String segmentText = convertLessThan1000(segment);
                parts[scaleIndex] = formatSegment(segmentText, segment, scaleIndex);
            }
            tempNumber /= 1000;
            scaleIndex++;
        }

        StringBuilder result = new StringBuilder();
        boolean firstSegmentAdded = false;

        for (int i = parts.length - 1; i >= 0; i--) {
            if (parts[i] != null && !parts[i].isEmpty()) {
                if (firstSegmentAdded) {
                    result.append(" و ");
                }
                result.append(parts[i]);
                firstSegmentAdded = true;
            }
        }

        int unitsSegment = (int) ((long) montant % 1000);
        result.append(" ").append(getCurrencyWord((long) montant, unitsSegment));

        return result.toString();
    }

    private static String convertFractional(int centimes) {
        if (centimes == 1) return "سنتيم واحد";
        if (centimes == 2) return "سنتيمان";

        if (centimes <= 19) {
            return units[centimes] + " سنتيمًا";
        } else {
            int unit = centimes % 10;
            int ten = centimes / 10;
            return (unit > 0 ? units[unit] + " و " : "") + tens[ten] + " سنتيمًا";
        }
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

        String hundredStr;
        if (hundred == 1) hundredStr = "مائة";
        else if (hundred == 2) hundredStr = "مائتا";
        else hundredStr = units[hundred] + " مائة";

        if (rem > 0) {
            return hundredStr + " و " + convertLessThan1000(rem);
        } else {
            return hundredStr;
        }
    }

    private static String formatSegment(String numberText, int number, int scaleIndex) {
        if (scaleIndex == 0) return numberText;

        switch (scaleIndex) {
            case 1: return formatScale(numberText, number, "ألف", "ألفا", "ألفان", "آلاف");
            case 2: return formatScale(numberText, number, "مليون", "مليونًا", "مليونان", "ملايين");
            case 3: return formatScale(numberText, number, "مليار", "مليارًا", "ملياران", "مليارات");
            case 4: return formatScale(numberText, number, "ترليون", "ترليونًا", "ترليونان", "ترليونات");
            default: return numberText;
        }
    }

    private static String formatScale(String numberText, int number, String singular, String accusative, String dual, String plural) {
        if (number == 1) return singular;
        if (number == 2) return dual;
        if (number >= 3 && number <= 10) return numberText + " " + plural;
        if (numberText.equals("مائتا")) return "مائتا " + singular;

        return numberText + " " + singular;
    }

    private static String getCurrencyWord(long number, int unitsSegment) {
        if (number == 1) return "درهم واحد";
        if (number == 2) return "درهمان";

        long lastTwo = number % 100;

        if (unitsSegment == 0) {
            if (number >= 1000) {
                return "درهم";
            }
            return "دراهم";
        }

        if (lastTwo >= 3 && lastTwo <= 10) {
            return "دراهم";
        }

        return "درهمًا";
    }

    public static String formatNumberWithDecimals(double number) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.FRANCE);
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator(',');
        DecimalFormat formatter = new DecimalFormat("#,##0.00", symbols);
        return formatter.format(number);
    }

    // ✅ Alias utilisé par Thymeleaf
    public static String formatNumber(Double number) {
        return formatNumberWithDecimals(number);
    }
}
