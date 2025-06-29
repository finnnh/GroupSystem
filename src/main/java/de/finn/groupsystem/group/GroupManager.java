package de.finn.groupsystem.group;

import de.finn.groupsystem.GroupSystem;

import java.util.*;

public class GroupManager {
    private final GroupSystem plugin;
    private final GroupRepository groupRepository;

    private final Map<String, Group> groups;

    public GroupManager(GroupSystem plugin, GroupRepository groupRepository) {
        this.plugin = plugin;
        this.groupRepository = groupRepository;
        this.createDefaultGroupIfMissing();
        this.groups = loadGroupsIntoMap();
    }

    private void createDefaultGroupIfMissing() {
        if (!(groupRepository.doesGroupExist("default"))) {
            groupRepository.createGroup("default", "Default", new ArrayList<>());
        }
    }

    private Map<String, Group> loadGroupsIntoMap() {
        HashMap<String, Group> groups = new HashMap<>();
        for (Group group : groupRepository.loadAllGroups()) {
            groups.put(group.groupName(), group);
        }

        return groups;
    }

    public boolean doesGroupExist(String groupName) {
        return groupRepository.doesGroupExist(groupName);
    }

    public void createGroup(String groupName, String groupPrefix, ArrayList<String> permissions) {
        groups.put(groupName, new Group(groupName, groupPrefix, permissions));
        groupRepository.createGroup(groupName, groupPrefix, permissions);
    }

    public void deleteGroup(String groupName) {
        groups.remove(groupName);
        groupRepository.deleteGroup(groupName);
    }

    public void addPermissionToGroup(String groupName, String permission) {
        groups.get(groupName).permissions().add(permission);
        groupRepository.addPermissionToGroup(groupName, permission);
    }

    public void removePermissionFromGroup(String groupName, String permission) {
        groups.get(groupName).permissions().remove(permission);
        groupRepository.removePermissionFromGroup(groupName, permission);
    }

    public Map<String, Group> getGroups() {
        return groups;
    }
}