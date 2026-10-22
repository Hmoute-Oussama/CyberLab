package cyberlab;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PasswordAnalyzer {

    private static final List<String> COMMON_PASSWORDS = List.of(
            "password", "password123", "123456", "12345678", "qwerty", "qwerty123",
            "admin", "welcome", "letmein", "iloveyou", "monkey", "dragon");

    public AnalysisResult analyze(String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null.");
        }

        int length = password.codePointCount(0, password.length());
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecialCharacter = false;
        boolean hasWhitespace = false;
        int distinctCharacters = 0;
        Set<Integer> seenCharacters = new HashSet<>();

        for (int offset = 0; offset < password.length();) {
            int codePoint = password.codePointAt(offset);
            if (Character.isUpperCase(codePoint)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(codePoint)) {
                hasLowercase = true;
            } else if (Character.isDigit(codePoint)) {
                hasDigit = true;
            } else if (!Character.isWhitespace(codePoint)) {
                hasSpecialCharacter = true;
            }
            hasWhitespace |= Character.isWhitespace(codePoint);

            if (seenCharacters.add(codePoint)) {
                distinctCharacters++;
            }
            offset += Character.charCount(codePoint);
        }

        boolean commonPassword = containsCommonPassword(password);
        boolean hasRepeatedCharacters = hasRepeatedCharacters(password);
        boolean hasSequence = hasSequence(password);
        List<String> warnings = buildWarnings(
                commonPassword, hasRepeatedCharacters, hasSequence, hasWhitespace, distinctCharacters, length);
        List<String> recommendations = buildRecommendations(
                length, hasUppercase, hasLowercase, hasDigit, hasSpecialCharacter,
                commonPassword, hasRepeatedCharacters, hasSequence, hasWhitespace);
        int score = calculateScore(
                length, hasUppercase, hasLowercase, hasDigit, hasSpecialCharacter,
                commonPassword, hasRepeatedCharacters, hasSequence, hasWhitespace, distinctCharacters);

        return new AnalysisResult(
                length, hasUppercase, hasLowercase, hasDigit, hasSpecialCharacter,
                distinctCharacters, score, determineStrength(score), recommendations, warnings);
    }

    private boolean containsCommonPassword(String password) {
        String normalized = normalize(password);
        for (String commonPassword : COMMON_PASSWORDS) {
            if (normalized.contains(commonPassword)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasRepeatedCharacters(String password) {
        int previousCodePoint = -1;
        int repeatedCount = 1;

        for (int offset = 0; offset < password.length();) {
            int codePoint = password.codePointAt(offset);
            if (codePoint == previousCodePoint) {
                repeatedCount++;
                if (repeatedCount >= 3) {
                    return true;
                }
            } else {
                previousCodePoint = codePoint;
                repeatedCount = 1;
            }
            offset += Character.charCount(codePoint);
        }
        return false;
    }

    private boolean hasSequence(String password) {
        String normalized = stripToAlphaNumeric(password);
        if (normalized.length() < 4) {
            return false;
        }

        int previousDifference = 0;
        int sequenceLength = 1;
        for (int i = 1; i < normalized.length(); i++) {
            int difference = normalized.charAt(i) - normalized.charAt(i - 1);
            if ((difference == 1 || difference == -1) && difference == previousDifference) {
                sequenceLength++;
                if (sequenceLength >= 4) {
                    return true;
                }
            } else {
                sequenceLength = difference == 1 || difference == -1 ? 2 : 1;
            }
            previousDifference = difference;
        }
        return false;
    }

    private String normalize(String password) {
        return password.toLowerCase(Locale.ROOT)
                .replace('@', 'a')
                .replace('0', 'o')
                .replace('1', 'i')
                .replace('3', 'e')
                .replace('4', 'a')
                .replace('5', 's')
                .replace('7', 't')
                .replaceAll("[^a-z0-9]", "");
    }

    private String stripToAlphaNumeric(String value) {
        return value.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    private List<String> buildWarnings(
            boolean commonPassword,
            boolean repeatedCharacters,
            boolean sequence,
            boolean whitespace,
            int distinctCharacters,
            int length) {
        List<String> warnings = new ArrayList<>();
        if (commonPassword) {
            warnings.add("Contains a common password or predictable password word.");
        }
        if (repeatedCharacters) {
            warnings.add("Contains the same character repeated three or more times in a row.");
        }
        if (sequence) {
            warnings.add("Contains a predictable sequence of four or more letters or digits.");
        }
        if (whitespace) {
            warnings.add("Contains whitespace, which some services may handle inconsistently.");
        }
        if (length >= 8 && distinctCharacters <= 3) {
            warnings.add("Uses very few distinct characters.");
        }
        return warnings;
    }

    private List<String> buildRecommendations(
            int length,
            boolean uppercase,
            boolean lowercase,
            boolean digit,
            boolean specialCharacter,
            boolean commonPassword,
            boolean repeatedCharacters,
            boolean sequence,
            boolean whitespace) {
        List<String> recommendations = new ArrayList<>();
        if (length < 12) {
            recommendations.add("Use at least 12 characters; longer is generally better.");
        }
        if (!uppercase) {
            recommendations.add("Consider adding uppercase letters.");
        }
        if (!lowercase) {
            recommendations.add("Consider adding lowercase letters.");
        }
        if (!digit) {
            recommendations.add("Consider adding digits.");
        }
        if (!specialCharacter) {
            recommendations.add("Consider adding special characters.");
        }
        if (commonPassword) {
            recommendations.add("Avoid common password words and predictable variations.");
        }
        if (repeatedCharacters) {
            recommendations.add("Avoid long runs of the same character.");
        }
        if (sequence) {
            recommendations.add("Avoid sequential letters or digits.");
        }
        if (whitespace) {
            recommendations.add("Avoid whitespace for compatibility across services.");
        }
        if (recommendations.isEmpty()) {
            recommendations.add("No obvious patterns were detected; use a unique password for every service.");
        }
        return recommendations;
    }

    private int calculateScore(
            int length,
            boolean uppercase,
            boolean lowercase,
            boolean digit,
            boolean specialCharacter,
            boolean commonPassword,
            boolean repeatedCharacters,
            boolean sequence,
            boolean whitespace,
            int distinctCharacters) {
        int score = Math.min(length, 20) * 2;
        score += countTrue(uppercase, lowercase, digit, specialCharacter) * 5;
        if (length >= 12) {
            score += 10;
        }
        if (length >= 16) {
            score += 10;
        }
        if (length >= 20) {
            score += 10;
        }
        if (commonPassword) {
            score -= 40;
        }
        if (repeatedCharacters) {
            score -= 15;
        }
        if (sequence) {
            score -= 15;
        }
        if (whitespace) {
            score -= 5;
        }
        if (length >= 8 && distinctCharacters <= 3) {
            score -= 15;
        }
        return Math.max(0, Math.min(100, score));
    }

    private int countTrue(boolean... values) {
        int count = 0;
        for (boolean value : values) {
            if (value) {
                count++;
            }
        }
        return count;
    }

    private String determineStrength(int score) {
        if (score < 20) {
            return "Very weak";
        }
        if (score < 40) {
            return "Weak";
        }
        if (score < 60) {
            return "Moderate";
        }
        if (score < 80) {
            return "Good";
        }
        return "Strong";
    }

    public static class AnalysisResult {
        private final int length;
        private final boolean uppercase;
        private final boolean lowercase;
        private final boolean digit;
        private final boolean specialCharacter;
        private final int distinctCharacters;
        private final int score;
        private final String strength;
        private final List<String> recommendations;
        private final List<String> warnings;

        private AnalysisResult(
                int length,
                boolean uppercase,
                boolean lowercase,
                boolean digit,
                boolean specialCharacter,
                int distinctCharacters,
                int score,
                String strength,
                List<String> recommendations,
                List<String> warnings) {
            this.length = length;
            this.uppercase = uppercase;
            this.lowercase = lowercase;
            this.digit = digit;
            this.specialCharacter = specialCharacter;
            this.distinctCharacters = distinctCharacters;
            this.score = score;
            this.strength = strength;
            this.recommendations = List.copyOf(recommendations);
            this.warnings = List.copyOf(warnings);
        }

        public int getLength() {
            return length;
        }

        public boolean hasUppercase() {
            return uppercase;
        }

        public boolean hasLowercase() {
            return lowercase;
        }

        public boolean hasDigit() {
            return digit;
        }

        public boolean hasSpecialCharacter() {
            return specialCharacter;
        }

        public int getDistinctCharacters() {
            return distinctCharacters;
        }

        public int getScore() {
            return score;
        }

        public String getStrength() {
            return strength;
        }

        public List<String> getRecommendations() {
            return recommendations;
        }

        public List<String> getWarnings() {
            return warnings;
        }

        public String getRecommendation() {
            return String.join("; ", recommendations);
        }
    }
}
