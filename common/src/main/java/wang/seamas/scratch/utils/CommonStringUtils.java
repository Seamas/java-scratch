package wang.seamas.scratch.utils;

import java.util.regex.Pattern;

public final class CommonStringUtils {

    private static final Pattern SNAKE_CASE_PATTERN = Pattern.compile("^[a-z][a-z0-9]*(_[a-z0-9]+)*$");
    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("^[a-z][a-zA-Z0-9]*$");
    private static final Pattern PASCAL_CASE_PATTERN = Pattern.compile("^[A-Z][a-zA-Z0-9]*$");

    public static String toCamelCase(String source) {
        if (source == null || source.isEmpty()) {
            return source;
        }

        // If already camelCase, return as is
        if (isCamelCase(source)) {
            return source;
        }

        // If PascalCase, just lowercase the first character
        if (isPascalCase(source)) {
            return Character.toLowerCase(source.charAt(0)) + source.substring(1);
        }

        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = false;

        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);

            if (c == '_' || c == '-' || c == ' ') {
                nextUpperCase = true;
            } else if (nextUpperCase) {
                result.append(Character.toUpperCase(c));
                nextUpperCase = false;
            } else {
                if (i == 0) {
                    result.append(Character.toLowerCase(c));
                } else {
                    result.append(Character.toLowerCase(c));
                }
            }
        }

        return result.toString();
    }

    public static String toPascalCase(String source) {
        if (source == null || source.isEmpty()) {
            return source;
        }

        // If already PascalCase, return as is
        if (isPascalCase(source)) {
            return source;
        }

        // If camelCase, just uppercase the first character
        if (isCamelCase(source)) {
            return Character.toUpperCase(source.charAt(0)) + source.substring(1);
        }

        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = true;

        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);

            if (c == '_' || c == '-' || c == ' ') {
                nextUpperCase = true;
            } else if (nextUpperCase) {
                result.append(Character.toUpperCase(c));
                nextUpperCase = false;
            } else {
                result.append(Character.toLowerCase(c));
            }
        }

        return result.toString();
    }

    public static String toSnakeCase(String source) {
        if (source == null || source.isEmpty()) {
            return source;
        }

        // If already snake_case, return as is
        if (isSnakeCase(source)) {
            return source;
        }

        StringBuilder result = new StringBuilder();
        boolean lastWasSeparator = false;

        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);

            if (c == '-' || c == ' ' || c == '_') {
                if (!lastWasSeparator && result.length() > 0) {
                    result.append('_');
                }
                lastWasSeparator = true;
            } else if (Character.isUpperCase(c)) {
                if (i > 0 && !lastWasSeparator && result.length() > 0) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(c));
                lastWasSeparator = false;
            } else {
                result.append(c);
                lastWasSeparator = false;
            }
        }

        return result.toString();
    }

    public static boolean isCamelCase(String source) {
        if (source == null || source.isEmpty()) {
            return false;
        }

        return CAMEL_CASE_PATTERN.matcher(source).matches();
    }

    public static boolean isPascalCase(String source) {
        if (source == null || source.isEmpty()) {
            return false;
        }

        return PASCAL_CASE_PATTERN.matcher(source).matches();
    }

    public static boolean isSnakeCase(String source) {
        if (source == null || source.isEmpty()) {
            return false;
        }

        return SNAKE_CASE_PATTERN.matcher(source).matches();
    }
}
