package de.finn.groupsystem.commands;

import de.finn.groupsystem.events.PlayerLanguageChangeEvent;
import de.finn.groupsystem.language.LanguageManager;
import de.finn.groupsystem.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LanguageCommand implements CommandExecutor {

    private final LanguageManager languageManager;
    private final PlayerManager playerManager;

    public LanguageCommand(LanguageManager languageManager, PlayerManager playerManager) {
        this.languageManager = languageManager;
        this.playerManager = playerManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length != 1) {
            languageManager.sendMessage(player, "language_command_usage");
            return true;
        }

        String input = args[0];

        if (input.equalsIgnoreCase("list")) {
            languageManager.sendMessage(player, "language_command_list");
            languageManager.getLanguages().keySet().forEach(lang ->
                    player.sendMessage("§7- " + lang)
            );
            return true;
        }

        if (!(languageManager.doesLanguageExist(input))) {
            languageManager.sendMessage(player, "language_command_language_not_found");
            return true;
        }

        playerManager.setPlayerLanguage(player, input);
        languageManager.sendMessage(player, "language_command_language_update_success", input);

        Bukkit.getPluginManager().callEvent(new PlayerLanguageChangeEvent(player, input));
        return true;
    }
}