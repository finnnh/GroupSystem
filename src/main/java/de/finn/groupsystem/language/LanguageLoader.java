package de.finn.groupsystem.language;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import de.finn.groupsystem.utils.ResourceUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class LanguageLoader {
    private final Gson gson;
    private final Path jsonPath = Path.of("./plugins/GroupSystem/languages.json");

    public LanguageLoader() {
        this.gson = new Gson();
    }

    private Map<String, Map<String, String>> parseMessages(String json) {
        Type type = new TypeToken<Map<String, Map<String, String>>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public CompletableFuture<Map<String, Map<String, String>>> loadLanguagesFromFileAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!(Files.exists(jsonPath))) {
                    ResourceUtils.copyResourceIfMissing("/languages.json", jsonPath);
                }
                String json = Files.readString(jsonPath);
                return parseMessages(json);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            return null;
        });
    }
}