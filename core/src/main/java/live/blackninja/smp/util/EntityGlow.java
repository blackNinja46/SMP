package live.blackninja.smp.util;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class EntityGlow {

    protected final Entity entity;

    public EntityGlow(Entity entity) {
        this.entity = entity;
    }

    public void setGlowing(NamedTextColor color) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        String teamName = "glow_" + this.entity.getUniqueId().toString().substring(0, 8);

        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }

        team.color(color);

        team.addEntity(this.entity);

        this.entity.setGlowing(true);
    }

    public void removeGlowing() {
        entity.setGlowing(false);

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        String teamName = "glow_" + entity.getUniqueId().toString().substring(0, 8);
        Team team = scoreboard.getTeam(teamName);
        if (team != null) team.unregister();
    }

    public boolean isGlowing() {
        return entity.isGlowing();
    }


}
