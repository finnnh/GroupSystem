package de.finn.groupsystem.group;

import java.util.ArrayList;

public record Group(String groupName, String groupPrefix, ArrayList<String> permissions) {}