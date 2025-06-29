package de.finn.groupsystem.commands;

import de.finn.groupsystem.events.PlayerGroupChangeEvent;
import de.finn.groupsystem.group.GroupManager;
import de.finn.groupsystem.language.LanguageManager;
import de.finn.groupsystem.player.PlayerManager;
import de.finn.groupsystem.signs.SignManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GroupCommand implements CommandExecutor {
    private final LanguageManager languageManager;
    private final GroupManager groupManager;
    private final PlayerManager playerManager;

    public GroupCommand(LanguageManager languageManager, GroupManager groupManager, PlayerManager playerManager, SignManager signManager) {
        this.languageManager = languageManager;
        this.groupManager = groupManager;
        this.playerManager = playerManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        if (args.length == 0) {
            languageManager.sendMessage(player, "group_command_usage");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "list" -> {
                return handleList(player, args);
            }
            case "create" -> {
                return handleCreate(player, args);
            }
            case "delete" -> {
                return handleDelete(player, args);
            }
            case "addperm" -> {
                return handleAddPerm(player, args);
            }
            case "removeperm" -> {
                return handleRemovePerm(player, args);
            }
            case "set" -> {
                return handleSet(player, args);
            }
            case "info" -> {
                return handleInfo(player, args);
            }
            default -> {
                languageManager.sendMessage(player, "group_command_usage");
                return true;
            }
        }
    }

    private boolean handleList(Player player, String[] args) {
        if (args.length != 1) {
            languageManager.sendMessage(player, "group_command_usage");
            return true;
        }
        languageManager.sendMessage(player, "group_command_list");
        groupManager.getGroups().keySet().forEach(group -> player.sendMessage("§7- " + group));
        return true;
    }

    private boolean handleCreate(Player player, String[] args) {
        if (args.length != 3) {
            languageManager.sendMessage(player, "group_command_usage");
            return false;
        }
        String groupName = args[1];
        String groupPrefix = args[2];

        if (groupManager.doesGroupExist(groupName)) {
            languageManager.sendMessage(player, "group_command_group_already_exist", groupName);
            return true;
        }

        groupManager.createGroup(groupName, groupPrefix, new ArrayList<>());
        languageManager.sendMessage(player, "group_command_group_created", groupName, groupPrefix);
        return true;
    }

    private boolean handleDelete(Player player, String[] args) {
        if (args.length != 2) {
            languageManager.sendMessage(player, "group_command_usage");
            return true;
        }

        String groupName = args[1];
        if (!groupManager.doesGroupExist(groupName)) {
            languageManager.sendMessage(player, "group_command_group_does_not_exist", groupName);
            return true;
        }

        playerManager.setDefaultGroupToPlayersWithGroup(groupName);
        groupManager.deleteGroup(groupName);
        languageManager.sendMessage(player, "group_command_group_deleted", groupName);
        return true;
    }

    private boolean handleAddPerm(Player player, String[] args) {
        if (args.length != 3) {
            languageManager.sendMessage(player, "group_command_usage");
            return true;
        }

        String groupName = args[1];
        String permission = args[2];

        if (!groupManager.doesGroupExist(groupName)) {
            languageManager.sendMessage(player, "group_command_group_does_not_exist", groupName);
            return false;
        }

        groupManager.addPermissionToGroup(groupName, permission);
        languageManager.sendMessage(player, "group_command_group_add_permission", permission, groupName);
        return true;
    }

    private boolean handleRemovePerm(Player player, String[] args) {
        if (args.length != 3) {
            languageManager.sendMessage(player, "group_command_usage");
            return true;
        }

        String groupName = args[1];
        String permission = args[2];

        if (!groupManager.doesGroupExist(groupName)) {
            languageManager.sendMessage(player, "group_command_group_does_not_exist", groupName);
            return false;
        }

        groupManager.removePermissionFromGroup(groupName, permission);
        languageManager.sendMessage(player, "group_command_group_remove_permission", permission, groupName);
        return true;
    }

    private boolean handleSet(Player player, String[] args) {
        if (args.length != 3 && args.length != 7) {
            languageManager.sendMessage(player, "group_command_usage");
            return false;
        }

        String playerName = args[1];
        String groupName = args[2];

        if (!groupManager.doesGroupExist(groupName)) {
            languageManager.sendMessage(player, "group_command_group_does_not_exist", groupName);
            return false;
        }

        if (!playerManager.doesPlayerExist(playerName)) {
            languageManager.sendMessage(player, "group_command_player_does_not_exist", playerName);
            return true;
        }

        if (args.length == 3) {
            playerManager.setGroupOfPlayerPermanent(playerName, groupName);
            callGroupChangeEvent(playerName, groupName);
            languageManager.sendMessage(player, "group_command_set_player_group_permanent", groupName, playerName);
            return true;
        }

        try {
            int days = Integer.parseInt(args[3]);
            int hours = Integer.parseInt(args[4]);
            int minutes = Integer.parseInt(args[5]);
            int seconds = Integer.parseInt(args[6]);

            playerManager.setGroupOfPlayerWithExpiry(playerName, groupName, days, hours, minutes, seconds);
            languageManager.sendMessage(player, "group_command_set_player_group_with_expiry", groupName, playerName,
                    String.valueOf(days), String.valueOf(hours), String.valueOf(minutes), String.valueOf(seconds));
            return true;
        } catch (NumberFormatException e) {
            languageManager.sendMessage(player, "group_command_usage");
            return false;
        }
    }

    private boolean handleInfo(Player player, String[] args) {
        if (args.length > 2) {
            languageManager.sendMessage(player, "group_command_usage");
            return false;
        }

        if (args.length == 1) {
            String groupName = playerManager.getGroupOfPlayer(player).groupName();
            Long length = playerManager.getDurationOfPlayerGroup(player.getName());
            String expiryTime = (length == -1L) ? languageManager.getMessage(player, "group_command_group_length_permanent") : formatRemainingTime(length);

            languageManager.sendMessage(player, "group_command_player_info", groupName, expiryTime);
            return true;
        }

        String targetName = args[1];
        if (!playerManager.doesPlayerExist(targetName)) {
            languageManager.sendMessage(player, "group_command_player_does_not_exist", targetName);
            return true;
        }

        String groupName = playerManager.getGroupOfPlayer(targetName).groupName();
        Long length = playerManager.getDurationOfPlayerGroup(targetName);
        String expiryTime = (length == -1L) ? languageManager.getMessage(player, "group_command_group_length_permanent") : formatRemainingTime(length);

        languageManager.sendMessage(player, "group_command_target_info", targetName, groupName, expiryTime);
        return true;
    }

    private void callGroupChangeEvent(String playerName, String groupName) {
        Player target = Bukkit.getPlayer(playerName);
        if (target != null) {
            PlayerGroupChangeEvent event = new PlayerGroupChangeEvent(target, groupManager.getGroups().get(groupName));
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    public String formatRemainingTime(long futureTimestamp) {
        long currentTime = System.currentTimeMillis();
        long remainingMillis = futureTimestamp - currentTime;

        if (remainingMillis <= 0) {
            return "0 seconds";
        }

        long seconds = (remainingMillis / 1000) % 60;
        long minutes = (remainingMillis / (1000 * 60)) % 60;
        long hours = (remainingMillis / (1000 * 60 * 60)) % 24;
        long days = remainingMillis / (1000 * 60 * 60 * 24);

        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append(" days ");
        if (hours > 0) sb.append(hours).append(" hours ");
        if (minutes > 0) sb.append(minutes).append(" minutes ");
        if (seconds > 0) sb.append(seconds).append(" seconds");

        return sb.toString().trim();
    }
}