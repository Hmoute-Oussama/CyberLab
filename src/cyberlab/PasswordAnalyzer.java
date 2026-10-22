package cyberlab;

public class PasswordAnalyzer {

    public AnalysisResult analyze(String password) {
        int length = password.codePointCount(0, password.length());
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecialCharacter = false;

        for (int offset = 0; offset < password.length();) {
            int codePoint = password.codePointAt(offset);
            if (Character.isUpperCase(codePoint)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(codePoint)) {
                hasLowercase = true;
            } else if (Character.isDigit(codePoint)) {
                hasDigit = true;
            } else {
                hasSpecialCharacter = true;
            }
            offset += Character.charCount(codePoint);
        }

        int characterTypes = countTrue(hasUppercase, hasLowercase, hasDigit, hasSpecialCharacter);
        String strength = determineStrength(length, characterTypes);
        String recommendation = buildRecommendation(
                length, hasUppercase, hasLowercase, hasDigit, hasSpecialCharacter);

        return new AnalysisResult(
                length, hasUppercase, hasLowercase, hasDigit, hasSpecialCharacter,
                strength, recommendation);
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

    private String determineStrength(int length, int characterTypes) {
        if (length < 8) {
            return "Very weak";
        }
        if (length < 12 || characterTypes <= 2) {
            return "Weak";
        }
        if (length < 14 || characterTypes == 3) {
            return "Moderate";
        }
        return "Strong";
    }

    private String buildRecommendation(
            int length, boolean uppercase, boolean lowercase, boolean digit, boolean specialCharacter) {
        StringBuilder recommendation = new StringBuilder();

        if (length < 12) {
            recommendation.append("Use at least 12 characters");
        }
        if (!uppercase) {
            appendRecommendation(recommendation, "add uppercase letters");
        }
        if (!lowercase) {
            appendRecommendation(recommendation, "add lowercase letters");
        }
        if (!digit) {
            appendRecommendation(recommendation, "add digits");
        }
        if (!specialCharacter) {
            appendRecommendation(recommendation, "add special characters");
        }

        return recommendation.length() == 0
                ? "Your password meets the basic length and character variety criteria."
                : recommendation.toString();
    }

    private void appendRecommendation(StringBuilder recommendation, String item) {
        if (recommendation.length() > 0) {
            recommendation.append("; ");
        }
        recommendation.append(item);
    }

    public static class AnalysisResult {
        private final int length;
        private final boolean uppercase;
        private final boolean lowercase;
        private final boolean digit;
        private final boolean specialCharacter;
        private final String strength;
        private final String recommendation;

        private AnalysisResult(
                int length,
                boolean uppercase,
                boolean lowercase,
                boolean digit,
                boolean specialCharacter,
                String strength,
                String recommendation) {
            this.length = length;
            this.uppercase = uppercase;
            this.lowercase = lowercase;
            this.digit = digit;
            this.specialCharacter = specialCharacter;
            this.strength = strength;
            this.recommendation = recommendation;
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

        public String getStrength() {
            return strength;
        }

        public String getRecommendation() {
            return recommendation;
        }
    }
}
