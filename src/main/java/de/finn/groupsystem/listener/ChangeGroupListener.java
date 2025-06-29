package de.finn.groupsystem.listener;

import de.finn.groupsystem.events.PlayerGroupChangeEvent;
import de.finn.groupsystem.player.PlayerManager;
import de.finn.groupsystem.signs.SignManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class ChangeGroupListener implements Listener {
    private final SignManager signManager;
    private final PlayerManager playerManager;

    public ChangeGroupListener(SignManager signManager, PlayerManager playerManager) {
        this.signManager = signManager;
        this.playerManager = playerManager;
    }

    @EventHandler
    public void onGroupChange(PlayerGroupChangeEvent event) {
        Player player = event.getPlayer();

        playerManager.setPlayerGroupMetadata(player);
        playerManager.loadPlayerPermissions(player);
        signManager.updateAllSignsPerPlayer(player);

        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        if (objective != null) {
            scoreboard.getEntries().forEach(entry -> {
                Score score = objective.getScore(entry);
                if (score != null && score.getScore() == 0) {
                    scoreboard.resetScores(entry);
                }
            });
        }

        String localizedGroupLine = "§7 » " + event.getNewGroup().groupName();
        objective.getScore(localizedGroupLine).setScore(0);
    }

}