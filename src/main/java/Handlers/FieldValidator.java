package Handlers;
import java.util.regex.Pattern;


public class FieldValidator {
    private static final Pattern WORD_PATTERN =
            Pattern.compile("^[a-z0-9]([-a-z0-9.]*[a-z0-9])?$");

    private static final Pattern IMAGE_PATTERN =
            Pattern.compile("^[a-z0-9]+([._/-][a-z0-9]+)*(?::[A-Za-z0-9._-]+)?$");

    private static final Pattern CONTAINER_NAME_PATTERN =
            Pattern.compile("^[a-z0-9]([-a-z0-9]*[a-z0-9])?$");
    public static boolean validatePort(String text) {
        try {
            int port = Integer.parseInt(text);
            return port >= 0 && port <= 65535;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean validateName(String text) {
        return WORD_PATTERN.matcher(text).matches();
    }

    public static boolean validateImage(String text) {
        return IMAGE_PATTERN.matcher(text).matches();
    }

    public static boolean validateContainerName(String text) {
        return CONTAINER_NAME_PATTERN.matcher(text).matches();
    }
}
