package de.finn.groupsystem.commands;

import de.finn.groupsystem.language.LanguageManager;
import de.finn.groupsystem.signs.SignManager;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SignCommand implements CommandExecutor {
    private final SignManager signManager;
    private final LanguageManager languageManager;

    public SignCommand(SignManager signManager, LanguageManager languageManager) {
        this.signManager = signManager;
        this.languageManager = languageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length != 0) {
            languageManager.sendMessage(player, "add_info_sign_command_usage");
            return true;
        }

        Block targetBlock = player.getTargetBlockExact(5);
        if (targetBlock == null) {
            languageManager.sendMessage(player, "add_info_sign_command_not_looking_at_a_block");
            return true;
        }

        if (!(targetBlock.getState() instanceof Sign sign)) {
            languageManager.sendMessage(player, "add_info_sign_command_not_looking_at_sign");
            return true;
        }

        if (signManager.doesSignExist(targetBlock.getLocation())) {
            languageManager.sendMessage(player, "add_info_sign_command_sign_already_exists");
            return true;
        }

        signManager.addSign(sign);
        languageManager.sendMessage(player, "add_info_sign_command_success");
        return true;
    }
}