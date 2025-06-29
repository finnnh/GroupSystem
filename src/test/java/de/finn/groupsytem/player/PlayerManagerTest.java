package de.finn.groupsytem.player;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import de.finn.groupsystem.GroupSystem;
import de.finn.groupsystem.group.Group;
import de.finn.groupsystem.group.GroupManager;
import de.finn.groupsystem.language.LanguageManager;
import de.finn.groupsystem.player.PlayerManager;
import de.finn.groupsystem.player.PlayerRepository;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.permissions.PermissionAttachment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerManagerTest {

    private GroupSystem plugin;
    private PlayerRepository playerRepository;
    private LanguageManager languageManager;
    private GroupManager groupManager;
    private PlayerManager playerManager;
    private Player player;

    @BeforeEach
    public void setup() {
        plugin = mock(GroupSystem.class);
        playerRepository = mock(PlayerRepository.class);
        languageManager = mock(LanguageManager.class);
        groupManager = mock(GroupManager.class);
        player = mock(Player.class);

        playerManager = new PlayerManager(plugin, playerRepository, languageManager, groupManager);

        when(player.getUniqueId()).thenReturn(UUID.randomUUID());
        when(player.getName()).thenReturn("TestPlayer");

        Group defaultGroup = new Group("default", "default", new ArrayList<>(List.of("", "")));
        when(groupManager.getGroups()).thenReturn(Map.of("default", defaultGroup));

        when(playerRepository.doesPlayerExist(any(UUID.class))).thenReturn(false);
        when(playerRepository.getUsername(any(UUID.class))).thenReturn("TestPlayer");
        when(playerRepository.getGroupNameOfPlayer(any(UUID.class))).thenReturn("default");
        when(playerRepository.getExpiryOfPlayerGroup(any(UUID.class))).thenReturn(-1L);
        when(playerRepository.getLanguage(any(UUID.class))).thenReturn("en");
    }


    @Test
    public void testLoadPlayerPermissionsAddsPermissionAttachment() {
        when(player.hasMetadata(anyString())).thenReturn(false);
        when(player.addAttachment(plugin)).thenReturn(mock(PermissionAttachment.class));

        playerManager.loadPlayerPermissions(player);
        Map<UUID, PermissionAttachment> map = playerManager.getPermissionAttachmentMap();
        assertTrue(map.containsKey(player.getUniqueId()));
    }

    @Test
    public void testSetGroupOfPlayerPermanent() {
        playerManager.setGroupOfPlayerPermanent("TestPlayer", "default");

        verify(playerRepository).setGroupOfPlayer("TestPlayer", "default", -1L);
    }

    @Test
    public void testDoesPlayerExistReturnsCorrectValue() {
        when(playerRepository.doesPlayerExist("TestPlayer")).thenReturn(true);

        assertTrue(playerManager.doesPlayerExist("TestPlayer"));

        verify(playerRepository).doesPlayerExist("TestPlayer");
    }

    @Test
    public void testGetGroupOfPlayerReturnsGroupFromMetadata() {
        when(player.hasMetadata("groupsystem_player_group")).thenReturn(true);
        when(player.getMetadata("groupsystem_player_group")).thenReturn(java.util.List.of(new FixedMetadataValue(plugin, "default")));
        Group mockGroup = mock(Group.class);
        when(groupManager.getGroups()).thenReturn(java.util.Map.of("default", mockGroup));

        Group group = playerManager.getGroupOfPlayer(player);

        assertEquals(mockGroup, group);
    }

    @Test
    public void testClearPlayerPermissionsRemovesAttachmentAndFromMap() {
        PermissionAttachment attachment = mock(PermissionAttachment.class);
        UUID playerUUID = player.getUniqueId();

        playerManager.getPermissionAttachmentMap().put(playerUUID, attachment);

        playerManager.clearPlayerPermissions(player);

        verify(player).removeAttachment(attachment);
        assertFalse(playerManager.getPermissionAttachmentMap().containsKey(playerUUID));
    }

    @Test
    public void testSetPlayerLanguageUpdatesRepoAndMetadata() {
        UUID uuid = player.getUniqueId();
        when(playerRepository.getLanguage(uuid)).thenReturn("en");

        playerManager.setPlayerLanguage(player, "de");

        verify(playerRepository).updateLanguageByUUID(uuid, "de");
        verify(player).removeMetadata("groupsystem_player_lang", plugin);
        verify(player).setMetadata(eq("groupsystem_player_lang"), any(FixedMetadataValue.class));
    }

}
