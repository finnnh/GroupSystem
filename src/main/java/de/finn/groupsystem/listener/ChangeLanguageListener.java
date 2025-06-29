package de.finn.groupsystem.listener;

import de.finn.groupsystem.events.PlayerLanguageChangeEvent;
import de.finn.groupsystem.language.LanguageManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class ChangeLanguageListener implements Listener {
    private final LanguageManager languageManager;

    public ChangeLanguageListener(LanguageManager languageManager) {
        this.languageManager = languageManager;
    }

    @EventHandler
    public void onLanguageChange(PlayerLanguageChangeEvent event) {
        Player player = event.getPlayer();

        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);
        if (objective != null) {
            scoreboard.getEntries().forEach(entry -> {
                Score score = objective.getScore(entry);
                if (score != null && score.getScore() == 1) {
                    scoreboard.resetScores(entry);
                }
            });
        }

        String localizedGroupLine = languageManager.getMessage(player, "scoreboard_your_rank");
        objective.getScore(localizedGroupLine).setScore(1);
    }

}