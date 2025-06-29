package de.finn.groupsystem.commands;

import de.finn.groupsystem.language.LanguageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DoIHavePermissionCommand implements CommandExecutor {
    private final LanguageManager languageManager;

    public DoIHavePermissionCommand(LanguageManager languageManager) {
        this.languageManager = languageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length != 1) {
            languageManager.sendMessage(player, "do_i_have_permission_command_usage");
            return true;
        }

        String permission = args[0];
        boolean hasPerm = player.hasPermission(permission);

        languageManager.sendMessage(player, "do_i_have_permission_command_response", permission, String.valueOf(hasPerm));
        return true;
    }
}