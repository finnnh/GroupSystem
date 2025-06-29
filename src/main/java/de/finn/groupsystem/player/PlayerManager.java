package de.finn.groupsystem.player;

import de.finn.groupsystem.GroupSystem;
import de.finn.groupsystem.events.PlayerGroupChangeEvent;
import de.finn.groupsystem.group.Group;
import de.finn.groupsystem.group.GroupManager;
import de.finn.groupsystem.language.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerManager {
    private final GroupSystem plugin;
    private final PlayerRepository playerRepository;
    private final LanguageManager languageManager;
    private final GroupManager groupManager;
    private final Map<UUID, PermissionAttachment> permissionAttachmentMap;

    public PlayerManager(GroupSystem plugin, PlayerRepository playerRepository, LanguageManager languageManager, GroupManager groupManager) {
        this.plugin = plugin;
        this.playerRepository = playerRepository;
        this.languageManager = languageManager;
        this.groupManager = groupManager;
        this.permissionAttachmentMap = new HashMap<>();
    }

    public void connectPlayer(Player player) {
        insertPlayerIfNotExists(player);
        syncUsernameWithDatabase(player);
        setPlayerLanguageMetadata(player);

        if (isGroupOfPlayerExpired(player)) {
            setGroupOfPlayerPermanent(player.getName(), "default");
        }

        setPlayerGroupMetadata(player);
        loadPlayerPermissions(player);
        setPlayerScoreboard(player);
    }

    public void disconnectPlayer(Player player) {
        clearPlayerPermissions(player);
        removePlayerGroupMetadata(player);
        removePlayerLanguageMetadata(player);
    }

    private void syncUsernameWithDatabase(Player player) {
        if (!playerRepository.getUsername(player.getUniqueId()).equals(player.getName())) {
            playerRepository.updateUsernameByUUID(player.getUniqueId(), player.getName());
        }
    }

    private void insertPlayerIfNotExists(Player player) {
        if(!playerRepository.doesPlayerExist(player.getUniqueId())) {
            playerRepository.insertPlayer(player.getUniqueId(), player.getName(), "en");
        }
    }

    public boolean doesPlayerExist(String username) {
        return playerRepository.doesPlayerExist(username);
    }

    public void setDefaultGroupToPlayersWithGroup(String groupName) {
    playerRepository
        .getAllUsernamesWithGroup(groupName)
        .forEach(
            playerName -> {
                setGroupOfPlayerPermanent(playerName, "default");

              Player target = Bukkit.getPlayer(playerName);
              if (target != null) {
                PlayerGroupChangeEvent event = new PlayerGroupChangeEvent(target, groupManager.getGroups().get("default"));
                Bukkit.getPluginManager().callEvent(event);
              }
            });
    }

    public void setGroupOfPlayerWithExpiry(String playerName, String groupName, int days, int hours, int minutes, int seconds) {
        Instant now = Instant.now();
        Instant expiry = now
                .plus(days, ChronoUnit.DAYS)
                .plus(hours, ChronoUnit.HOURS)
                .plus(minutes, ChronoUnit.MINUTES)
                .plus(seconds, ChronoUnit.SECONDS);

        Long expiryLong = expiry.toEpochMilli();

        setGroupOfPlayerByName(playerName, groupName, expiryLong);
    }

    public void setGroupOfPlayerPermanent(String playerName, String groupName) {
        setGroupOfPlayerByName(playerName, groupName, -1L);
    }

    private void setGroupOfPlayerByName(String playerName, String groupName, long expiry) {
        playerRepository.setGroupOfPlayer(playerName, groupName, expiry);
    }

    public void setPlayerGroupMetadata(Player player) {
        if (player.hasMetadata("groupsystem_player_group")) {
            player.removeMetadata("groupsystem_player_group", plugin);
        }
        player.setMetadata("groupsystem_player_group", new FixedMetadataValue(plugin, getGroupOfPlayer(player).groupName()));
    }

    private void removePlayerGroupMetadata(Player player) {
        player.removeMetadata("groupsystem_player_group", plugin);
    }

    private boolean isGroupOfPlayerExpired(Player player) {
        Long expiry = playerRepository.getExpiryOfPlayerGroup(player.getUniqueId());
        if (expiry != -1) {
            long now = Instant.now().toEpochMilli();
            return expiry < now;
        }
        return false;
    }

    public Long getDurationOfPlayerGroup(String playerName) {
        return playerRepository.getDurationOfGroup(playerName);
    }

    public Group getGroupOfPlayer(String playerName) {
        String groupName = playerRepository.getGroupNameOfPlayer(playerName);
        return groupManager.getGroups().get(groupName);
    }

    public Group getGroupOfPlayer(Player player) {
        String groupName = player.hasMetadata("groupsystem_player_group")
                ? player.getMetadata("groupsystem_player_group").get(0).asString()
                : playerRepository.getGroupNameOfPlayer(player.getUniqueId());

        Group group = groupManager.getGroups().get(groupName);
        return group;
    }

    public void loadPlayerPermissions(Player player) {
        if (permissionAttachmentMap.containsKey(player.getUniqueId())) {
            clearPlayerPermissions(player);
        }
        Group group = getGroupOfPlayer(player);
        PermissionAttachment attachment = player.addAttachment(plugin);
        for (String permission : group.permissions()) {
            attachment.setPermission(permission, true);
        }
        permissionAttachmentMap.put(player.getUniqueId(), attachment);
    }

    public void clearPlayerPermissions(Player player) {
        player.removeAttachment(permissionAttachmentMap.get(player.getUniqueId()));
        permissionAttachmentMap.remove(player.getUniqueId());
    }

    public void setPlayerLanguage(Player player, String language) {
        playerRepository.updateLanguageByUUID(player.getUniqueId(), language);
        removePlayerLanguageMetadata(player);
        setPlayerLanguageMetadata(player);
    }

    private void setPlayerLanguageMetadata(Player player) {
        player.setMetadata("groupsystem_player_lang", new FixedMetadataValue(plugin, getPlayerLanguage(player.getUniqueId())));
    }

    private void removePlayerLanguageMetadata(Player player) {
        player.removeMetadata("groupsystem_player_lang", plugin);
    }

    private String getPlayerLanguage(UUID uuid) {
        return playerRepository.getLanguage(uuid);
    }

    private void setPlayerScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("group_scoreboard", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§5GroupSystem");
        objective.getScore(languageManager.getMessage(player, "scoreboard_your_rank")).setScore(1);
        objective.getScore("§7 » " + getGroupOfPlayer(player).groupName()).setScore(0);

        player.setScoreboard(scoreboard);
    }

    public Map<UUID, PermissionAttachment> getPermissionAttachmentMap() {
        return permissionAttachmentMap;
    }
}