package utils;

public class TextDecorationUtils {
public static final String UNDERLINE = "\u001B[4m";

public static final String BOLD = "\u001B[1m";

public static final String ITALIC = "\u001B[3m";

public static final String RESET_CODE = "\u001B[0m";

public static String underlineText(String text) {
return UNDERLINE + text + RESET_CODE;
}

public static String boldText(String text) {
return BOLD + text + RESET_CODE;
}

public static String italicText(String text) {
return ITALIC + text + RESET_CODE;
}
}