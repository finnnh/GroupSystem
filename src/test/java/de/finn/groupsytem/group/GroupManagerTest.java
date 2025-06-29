package de.finn.groupsytem.group;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import de.finn.groupsystem.GroupSystem;
import de.finn.groupsystem.group.Group;
import de.finn.groupsystem.group.GroupManager;
import de.finn.groupsystem.group.GroupRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupManagerTest {

    private GroupSystem plugin;
    private GroupRepository groupRepository;
    private GroupManager groupManager;

    @BeforeEach
    public void setup() {
        plugin = mock(GroupSystem.class);
        groupRepository = mock(GroupRepository.class);

        when(groupRepository.doesGroupExist("default")).thenReturn(false);

        when(groupRepository.loadAllGroups()).thenReturn(new ArrayList<>());

        groupManager = new GroupManager(plugin, groupRepository);
    }

    @Test
    public void testCreateDefaultGroupIfMissingCallsRepositoryCreate() {
        verify(groupRepository).createGroup(eq("default"), eq("Default"), any());
    }

    @Test
    public void testCreateGroupAddsToMapAndCallsRepository() {
        ArrayList<String> perms = new ArrayList<>(List.of("perm.test"));
        groupManager.createGroup("admin", "Admin", perms);

        Map<String, Group> groups = groupManager.getGroups();
        assertTrue(groups.containsKey("admin"));
        assertEquals("admin", groups.get("admin").groupName());

        verify(groupRepository).createGroup("admin", "Admin", perms);
    }

    @Test
    public void testDeleteGroupRemovesFromMapAndCallsRepository() {
        groupManager.createGroup("testgroup", "Test", new ArrayList<>());
        assertTrue(groupManager.getGroups().containsKey("testgroup"));

        groupManager.deleteGroup("testgroup");
        assertFalse(groupManager.getGroups().containsKey("testgroup"));

        verify(groupRepository).deleteGroup("testgroup");
    }

    @Test
    public void testAddPermissionToGroupUpdatesGroupAndCallsRepository() {
        groupManager.createGroup("group1", "Group 1", new ArrayList<>());
        groupManager.addPermissionToGroup("group1", "perm.new");

        List<String> perms = groupManager.getGroups().get("group1").permissions();
        assertTrue(perms.contains("perm.new"));

        verify(groupRepository).addPermissionToGroup("group1", "perm.new");
    }

    @Test
    public void testRemovePermissionFromGroupUpdatesGroupAndCallsRepository() {
        ArrayList<String> perms = new ArrayList<>(List.of("perm.remove"));
        groupManager.createGroup("group2", "Group 2", perms);

        groupManager.removePermissionFromGroup("group2", "perm.remove");
        List<String> currentPerms = groupManager.getGroups().get("group2").permissions();
        assertFalse(currentPerms.contains("perm.remove"));

        verify(groupRepository).removePermissionFromGroup("group2", "perm.remove");
    }

}

