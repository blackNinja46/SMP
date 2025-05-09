package live.blackninja.smp.manger;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.ItemBuilder;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.config.Config;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;

public class SMPManger {

    private Core core;
    private int taskID;

    private final ArrayList<Player> teleportionList = new ArrayList<>();

    private HomeManger homeManger;
    private RankManger rankManger;
    private StatusManger statusManger;
    private VoteBanManger voteBanManger;
    private TimeOutManger timeOutManger;
    private ElytraManger elytraManger;
    private DelayedOpeningManger delayedOpeningManger;

    private Config config;

    private final int teleportationDelay;

    public SMPManger(Core core) {
        this.core = core;

        homeManger = new HomeManger(core);
        rankManger = new RankManger(core);
        statusManger = new StatusManger(core);
        voteBanManger = new VoteBanManger(core);
        timeOutManger = new TimeOutManger(core);
        elytraManger = new ElytraManger();
        delayedOpeningManger = new DelayedOpeningManger(core, this);

        config = new Config("config");
        initDefaultConfig();
        teleportationDelay = config.getConfig().getInt("TeleportationDelay");

    }

    public void initPlayer(String playerName) {
        homeManger.initPlayer(playerName);
        statusManger.updatePlayer(playerName);

        Player player = core.getServer().getPlayer(playerName);
        UUID uuid = player.getUniqueId();

        if (!elytraManger.hasUsed(uuid)) {
            player.getInventory().setChestplate(new ItemBuilder(Material.ELYTRA).setDisplayName("§bTemporäre Elytra").build());
            elytraManger.markReceived(uuid);

            player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§7Du hast eine %btemporäre Elytra §7erhalten!"));
        }
    }

    public void initDefaultConfig() {
        if (!config.getConfig().contains("TeleportationDelay")) {
            config.getConfig().set("TeleportationDelay", 5);
        }
        if (!config.getConfig().contains("MaxHomes")) {
            config.getConfig().set("MaxHomes", 3);
        }
        config.save();
    }

    public void teleport(Player player, Location location) {
        if (!teleportionList.contains(player)) {
            teleportionList.add(player);
            delayedTeleport(player, location, teleportationDelay);
            return;
        }
        player.sendMessage(MessageBuilder.buildOld(Core.PREFIX + "§cWarte §7bis du %yTeleportiert §7wurdest..."));
    }

    public void delayedTeleport(Player player, Location location, int delaySeconds) {
        Location initialLocation = player.getLocation().clone();

        new BukkitRunnable() {
            int countdown = delaySeconds;

            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    return;
                }

                if (countdown <= 0) {
                    player.teleport(location);
                    player.sendMessage(MessageBuilder.buildOld(
                            Core.PREFIX + "§7Du wurdest %gerfolgreich %bTeleportiert§7!"
                    ));
                    this.cancel();
                    return;
                }

                if (player.getLocation().distanceSquared(initialLocation) > 0.01) {
                    player.sendMessage(MessageBuilder.buildOld(
                            Core.PREFIX + "§7Du hast dich %ybewegt§7! Die %yTeleportation §7wurde %rabgebrochen"
                    ));
                    this.cancel();
                    return;
                }

                player.sendActionBar(MessageBuilder.buildOld(
                        "§8| §7Teleportation in %b" + countdown + " Sekunden §8|"
                ));
                countdown--;
            }
        }
                .runTaskTimer(core, 20L, 20L);
    }

    public DelayedOpeningManger getDelayedOpeningManger() {
        return delayedOpeningManger;
    }

    public TimeOutManger getTimeOutManger() {
        return timeOutManger;
    }

    public StatusManger getStatusManger() {
        return statusManger;
    }

    public RankManger getRankManger() {
        return rankManger;
    }

    public HomeManger getHomeManger() {
        return homeManger;
    }

    public VoteBanManger getVoteBanManger() {
        return voteBanManger;
    }

    public Config getConfig() {
        return config;
    }

    public ElytraManger getElytraManger() {
        return elytraManger;
    }
}
