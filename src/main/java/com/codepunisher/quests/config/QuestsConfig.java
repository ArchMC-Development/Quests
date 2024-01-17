package com.codepunisher.quests.config;

import dev.dejvokep.boostedyaml.YamlDocument;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Getter
public class QuestsConfig {
    private static final String LANG_FOLDER_NAME = "lang/";
    private String helloMessage;

    public void reload(JavaPlugin plugin) {
        try {
            YamlDocument config = YamlDocument.create(new File(plugin.getDataFolder(), "config.yml"),
                    Objects.requireNonNull(plugin.getResource("config.yml")));

            // Handling language based on config setting
            config.getOptionalString("lang").ifPresent(language -> {
                reloadLanguage(plugin, language);
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void reloadLanguage(JavaPlugin plugin, String language) {
        try {
            String langFilePath = LANG_FOLDER_NAME + language;
            File languageFile = new File(plugin.getDataFolder(), langFilePath);

            // Loading folder/values if first time
            boolean isPluginLoadingForFirstTime = !new File(plugin.getDataFolder(), LANG_FOLDER_NAME).exists();
            if (isPluginLoadingForFirstTime) {
                YamlDocument newConfig = YamlDocument.create(languageFile,
                        Objects.requireNonNull(plugin.getResource(langFilePath)));
                cacheLanguageConfigSettings(newConfig);
                return;
            }

            if (!languageFile.exists()) {
                plugin.getLogger().severe("THAT LANGUAGE FILE DOES NOT EXIST");
                return;
            }

            // Instantiating from existing config
            cacheLanguageConfigSettings(YamlDocument.create(languageFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void cacheLanguageConfigSettings(YamlDocument config) {
        config.getOptionalString("messages.hello").ifPresent(s -> {
            this.helloMessage = s;
        });
    }
}
