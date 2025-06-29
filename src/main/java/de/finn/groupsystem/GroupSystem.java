package de.finn.groupsystem;

import de.finn.groupsystem.commands.DoIHavePermissionCommand;
import de.finn.groupsystem.commands.GroupCommand;
import de.finn.groupsystem.commands.LanguageCommand;
import de.finn.groupsystem.commands.SignCommand;
import de.finn.groupsystem.database.PostgreSQLClient;
import de.finn.groupsystem.group.GroupManager;
import de.finn.groupsystem.group.GroupRepository;
import de.finn.groupsystem.language.LanguageManager;
import de.finn.groupsystem.listener.ChangeGroupListener;
import de.finn.groupsystem.listener.ChangeLanguageListener;
import de.finn.groupsystem.listener.ChatListener;
import de.finn.groupsystem.listener.PlayerConnectionListener;
import de.finn.groupsystem.player.PlayerManager;
import de.finn.groupsystem.player.PlayerRepository;
import de.finn.groupsystem.signs.SignManager;
import de.finn.groupsystem.signs.SignRepository;
import org.bukkit.plugin.java.JavaPlugin;

public class GroupSystem extends JavaPlugin {
    private PostgreSQLClient postgreSQLClient;

    @Override
    public void onEnable() {
        this.postgreSQLClient = new PostgreSQLClient();
        GroupRepository groupRepository = new GroupRepository(postgreSQLClient);
        PlayerRepository playerRepository = new PlayerRepository(postgreSQLClient);
        SignRepository signRepository = new SignRepository(postgreSQLClient);

        LanguageManager languageManager = new LanguageManager();
        GroupManager groupManager = new GroupManager(this, groupRepository);
        PlayerManager playerManager = new PlayerManager(this, playerRepository, languageManager, groupManager);
        SignManager signManager = new SignManager(signRepository, playerManager);

        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this, playerManager, languageManager, signManager), this);
        getServer().getPluginManager().registerEvents(new ChatListener(playerManager), this);
        getServer().getPluginManager().registerEvents(new ChangeLanguageListener(languageManager), this);
        getServer().getPluginManager().registerEvents(new ChangeGroupListener(signManager, playerManager), this);

        getCommand("group").setExecutor(new GroupCommand(languageManager, groupManager, playerManager, signManager));
        getCommand("doIHavePermission").setExecutor(new DoIHavePermissionCommand(languageManager));
        getCommand("addInfoSign").setExecutor(new SignCommand(signManager, languageManager));
        getCommand("language").setExecutor(new LanguageCommand(languageManager, playerManager));
    }

    @Override
    public void onDisable() {
        postgreSQLClient.close();
    }
}
