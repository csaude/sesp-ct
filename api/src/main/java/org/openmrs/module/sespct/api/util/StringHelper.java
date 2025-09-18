package org.openmrs.module.sespct.api.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class StringHelper {

    public static String removeAcentos(String input) {
        if (input == null) {
            return null;
        }

        // Normaliza a string (NFD separa os acentos das letras base)
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);

        // Regex que remove caracteres diacríticos
        Pattern pattern = Pattern.compile("\\p{M}");

        return pattern.matcher(normalized).replaceAll("");
    }
}
