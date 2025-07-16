package live.blackninja.smp.manger;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.ItemBuilder;
import live.blackninja.smp.builder.MessageBuilder;
import live.blackninja.smp.builder.TextDisplayBuilder;
import live.blackninja.smp.config.Config;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.units.qual.A;

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
    private RecipeManger recipeManger;

    private Config config;

    private final int teleportationDelay;

    private TextDisplayBuilder spawnTextDisplay;

    public SMPManger(Core core) {
        this.core = core;

        config = new Config("config");

        homeManger = new HomeManger(core);
        rankManger = new RankManger(core);
        statusManger = new StatusManger(core);
        voteBanManger = new VoteBanManger(core);
        timeOutManger = new TimeOutManger(core);
        elytraManger = new ElytraManger();
        recipeManger = new RecipeManger(core);
        delayedOpeningManger = new DelayedOpeningManger(core, this);

        initDefaultConfig();
        teleportationDelay = config.getConfig().getInt("TeleportationDelay");
    }

    public void initPlayer(String playerName) {
        homeManger.initPlayer(playerName);
        statusManger.updatePlayer(playerName);

        Player player = core.getServer().getPlayer(playerName);
        UUID uuid = player.getUniqueId();
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
                    teleportionList.remove(player);
                    player.sendMessage(MessageBuilder.buildOld(
                            Core.PREFIX + "§7Du wurdest %gerfolgreich %bTeleportiert§7!"
                    ));
                    this.cancel();
                    return;
                }

                if (player.getLocation().distanceSquared(initialLocation) > 0.01) {
                    teleportionList.remove(player);
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

    public void spawnTextDisplay(Location location) {
        spawnTextDisplay = new TextDisplayBuilder(location)
                .setTextMiniMessage(MiniMessage.miniMessage().deserialize(
                        "\n" +
                                "<color:#ffd152>★</color> <gradient:#5CDAC7:#48A3D8>ᴍɪɴᴇᴄʀᴀғᴛ sᴍᴘ sᴇᴀsᴏɴ 5</gradient> <color:#ffd152>★</color>\n" +
                                "<gray>*-----------------------*\n" +
                                "<white>Willkommen Zurück!</white>\n" +
                                "\n" +
                                "<white>Informationen findest du hier</white>\n" +
                                "<dark_gray>>> </dark_gray><color:#7d90ff>discord.blackNinja.live</color> <dark_gray><<</dark_gray>\n" +
                                "\n" +
                                "<color:#59baff>/tpa <dark_gray>| </dark_gray><color:#59baff>/home <dark_gray>|</dark_gray> <color:#59baff>/spawn</color>\n"
                ))
                .setAlignment(TextDisplay.TextAlignment.CENTER)
                .setBillboard(TextDisplay.Billboard.FIXED)
                .setTag("spawn-display")
                .setInvisibleBackground(true)
                .setShadowed(true)
                .setRotation(location.getYaw(), 0);
    }

    public void removeSpawnTextDisplay() {
        for (World world : core.getServer().getWorlds()) {
            world.getEntities().stream()
                    .filter(entity -> entity instanceof TextDisplay && entity.getScoreboardTags().contains("spawn-display"))
                    .forEach(Entity::remove);
        }
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

    public RecipeManger getRecipeManger() {
        return recipeManger;
    }
}
