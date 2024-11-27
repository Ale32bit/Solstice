package me.alexdevs.solstice.config.locale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class LocaleManager {
    private static final Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
            .create();

    private final Path path;
    private Locale locale;

    public LocaleManager(Path path) {
        this.path = path;
    }

    public Locale locale() {
        return locale;
    }

    public Locale load() throws IOException {
        if(!path.toFile().exists()) {
            locale = new Locale();
            return locale;
        }
        var bf = new BufferedReader(new FileReader(path.toFile(), StandardCharsets.UTF_8));
        locale = gson.fromJson(bf, Locale.class);
        bf.close();
        return locale;
    }

    public void save() throws IOException {
        var fw = new FileWriter(path.toFile(), StandardCharsets.UTF_8);
        gson.toJson(locale, fw);
        fw.close();
    }
}
