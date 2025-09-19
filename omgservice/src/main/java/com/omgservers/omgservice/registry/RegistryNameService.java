package com.omgservers.omgservice.registry;

import com.omgservers.omgservice.errors.InvalidRegistryName;
import jakarta.enterprise.context.ApplicationScoped;

import java.text.Normalizer;
import java.util.regex.Pattern;

@ApplicationScoped
public class RegistryNameService {

    private static final Pattern NOT_ALLOWED = Pattern.compile("[^a-z0-9-]");
    private static final Pattern DUPLICATES = Pattern.compile("[._-]{2,}");
    private static final Pattern BEGINNING = Pattern.compile("^[._-]+");
    private static final Pattern ENDING = Pattern.compile("[._-]+$");

    public String prepare(final String name, final String defaultName) {
        try {
            return prepare(name);
        } catch (Exception e) {
            return defaultName;
        }
    }

    public String prepare(final String name) {
        String s = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase();

        s = NOT_ALLOWED.matcher(s).replaceAll("-");
        s = DUPLICATES.matcher(s).replaceAll("-");
        s = BEGINNING.matcher(s).replaceAll("");
        s = ENDING.matcher(s).replaceAll("");

        if (s.isBlank()) {
            throw new InvalidRegistryName(name);
        }

        return s;
    }
}
