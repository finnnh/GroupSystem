package de.finn.groupsystem.language;

import org.bukkit.entity.Player;

import java.util.Map;

public class LanguageManager {
    private final LanguageLoader languageLoader;
    private Map<String, Map<String, String>> languages;

    public LanguageManager() {
        this.languageLoader = new LanguageLoader();
        loadLanguagesAsync();
    }

    private void loadLanguagesAsync() {
        languageLoader.loadLanguagesFromFileAsync().thenAccept(loadedMessages -> {
            if (loadedMessages != null) {
                this.languages = loadedMessages;
            } else {
                throw new RuntimeException("Failed to load language messages");
            }
        });
    }

    private String getMessage(String langId, String messageId) {
        Map<String, String> messagesMap = languages.get(langId);
        if (messagesMap == null) return null;
        return messagesMap.get(messageId);
    }

    public void sendMessage(Player player, String id, String... args) {
        String message = getMessage(player, id, args);
        player.sendMessage(message);
    }

    public String getMessage(Player player, String id, String... args) {
        String language = player.getMetadata("groupsystem_player_lang").get(0).asString();
        String message = getMessage(language, id);

        if(message == null) {
            message = id;
        }

        for (int i = 0; i < args.length; i++) {
            message = message.replace("{" + i + "}", args[i]);
        }

        return message;
    }

    public boolean doesLanguageExist(String language) {
        return languages.containsKey(language);
    }

    public Map<String, Map<String, String>> getLanguages() {
        return languages;
    }
}