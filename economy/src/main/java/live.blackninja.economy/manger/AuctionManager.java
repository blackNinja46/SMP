package live.blackninja.economy.manger;

import live.blackninja.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AuctionManager {

    private Economy economy;

    private final List<Auction> auctions = new ArrayList<>();
    private final Set<Material> bannedItems = new HashSet<>();
    private final List<String> logs = new ArrayList<>();
    private Map<UUID, Integer> pendingPayouts = new HashMap<>();
    private Map<UUID, List<PendingPayment>> pendingPayments = new HashMap<>();

    private Map<UUID, List<ItemStack>> unclaimedItems = new HashMap<>();

    private File auctionFile, bannedItemsFile, logFile, unclaimedFile, payoutsFile, paymentsFile;
    private YamlConfiguration auctionConfig, bannedConfig, logConfig, unclaimedConfig, payoutsConfig, paymentsConfig;

    public AuctionManager(Economy economy) {
        this.economy = economy;

        auctionFile = new File(economy.getDataFolder(), "auctions.yml");
        bannedItemsFile = new File(economy.getDataFolder(), "banneditems.yml");
        logFile = new File(economy.getDataFolder(), "logs.yml");
        unclaimedFile = new File(economy.getDataFolder(), "unclaimeditems.yml");
        payoutsFile = new File(economy.getDataFolder(), "pendingpayouts.yml");
        paymentsFile = new File(economy.getDataFolder(), "pendingpayments.yml");

        if (!auctionFile.exists()) {
            try {
                auctionFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!bannedItemsFile.exists()) {
            try {
                bannedItemsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!unclaimedFile.exists()) {
            try {
                unclaimedFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!payoutsFile.exists()) {
            try {
                payoutsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!paymentsFile.exists()) {
            try {
                paymentsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        auctionConfig = YamlConfiguration.loadConfiguration(auctionFile);
        bannedConfig = YamlConfiguration.loadConfiguration(bannedItemsFile);
        logConfig = YamlConfiguration.loadConfiguration(logFile);
        unclaimedConfig = YamlConfiguration.loadConfiguration(unclaimedFile);
        payoutsConfig = YamlConfiguration.loadConfiguration(payoutsFile);
        paymentsConfig = YamlConfiguration.loadConfiguration(paymentsFile);

        loadUnclaimedItems();
        loadPendingPayouts();
        loadPendingPayments();

        Bukkit.getScheduler().runTaskTimer(economy, this::checkExpiredPendingPayments, 20L, 20L * 60 * 60);
    }

    public List<Auction> getAuctions() {
        return auctions;
    }

    public void addAuction(Auction auction) {
        auctions.add(auction);
        saveAuctions();
    }

    public void removeAuction(Auction auction) {
        auctions.remove(auction);
        saveAuctions();
    }

    public void saveAuctions() {
        auctionConfig.set("auctions", null);
        for (int i = 0; i < auctions.size(); i++) {
            Auction auction = auctions.get(i);
            auctionConfig.set("auctions." + i + ".seller", auction.getSeller().toString());
            auctionConfig.set("auctions." + i + ".item", auction.getItem());
            auctionConfig.set("auctions." + i + ".startPrice", auction.getStartPrice());
            auctionConfig.set("auctions." + i + ".finalPrice", auction.getFinalPrice());
            auctionConfig.set("auctions." + i + ".highestBid", auction.getHighestBid());
            auctionConfig.set("auctions." + i + ".highestBidder", auction.getHighestBidder() != null ? auction.getHighestBidder().toString() : null);
            auctionConfig.set("auctions." + i + ".endTime", auction.getEndTime());
        }
        try {
            auctionConfig.save(auctionFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAuctions() {
        loadLogs();

        auctions.clear();
        if (auctionConfig.contains("auctions")) {
            for (String key : auctionConfig.getConfigurationSection("auctions").getKeys(false)) {
                UUID seller = UUID.fromString(auctionConfig.getString("auctions." + key + ".seller"));
                ItemStack item = auctionConfig.getItemStack("auctions." + key + ".item");
                int startPrice = auctionConfig.getInt("auctions." + key + ".startPrice");
                int finalPrice = auctionConfig.getInt("auctions." + key + ".finalPrice");
                int highestBid = auctionConfig.getInt("auctions." + key + ".highestBid");
                UUID highestBidder = auctionConfig.getString("auctions." + key + ".highestBidder") != null
                        ? UUID.fromString(auctionConfig.getString("auctions." + key + ".highestBidder"))
                        : null;
                long endTime = auctionConfig.getLong("auctions." + key + ".endTime");

                Auction auction = new Auction(seller, item, startPrice, finalPrice,  endTime);
                auction.setHighestBid(highestBid);
                auction.setHighestBidder(highestBidder);
                auctions.add(auction);
            }
        }
        if (bannedConfig.contains("banned")) {
            for (String mat : bannedConfig.getStringList("banned")) {
                bannedItems.add(Material.valueOf(mat));
            }
        }
    }

    public int getPlayerDiamonds(Player player) {
        ItemStack[] inventory = player.getInventory().getContents();
        int diamonds = 0;
        for (ItemStack item : inventory) {
            if (item != null && item.getType() == Material.DIAMOND) {
                diamonds += item.getAmount();
            }
        }
        return diamonds;
    }

    public boolean removePlayerDiamonds(Player player, int amount) {
        ItemStack[] inventory = player.getInventory().getContents();
        for (ItemStack item : inventory) {
            if (item != null && item.getType() == Material.DIAMOND) {
                if (item.getAmount() >= amount) {
                    item.setAmount(item.getAmount() - amount);
                    break;
                } else {
                    amount -= item.getAmount();
                    item.setAmount(0);
                }
            }
        }
        return false;
    }

    public void startAuctionChecker() {
        Bukkit.getScheduler().runTaskTimer(economy, () -> {
            Iterator<Auction> iterator = auctions.iterator();
            while (iterator.hasNext()) {
                Auction auction = iterator.next();
                if (auction.getEndTime() <= System.currentTimeMillis()) {
                    Player seller = Bukkit.getPlayer(auction.getSeller());

                    removeAuction(auction);

                    if (auction.getHighestBidder() != null) {
                        Player buyer = Bukkit.getPlayer(auction.getHighestBidder());

                        if (buyer != null && buyer.isOnline()) {
                            if (removePlayerDiamonds(buyer, auction.getHighestBid())) {
                                buyer.getInventory().addItem(auction.getItem());
                                buyer.sendMessage("§aDu hast die Auktion gewonnen und dein Item erhalten!");
                            } else {
                                addPendingPayment(auction.getHighestBidder(), auction.getItem(), auction.getHighestBid());
                            }
                        } else {
                            addPendingPayment(auction.getHighestBidder(), auction.getItem(), auction.getHighestBid());
                        }

                        if (seller != null && seller.isOnline()) {
                            addPlayerDiamonds(seller, auction.getHighestBid());
                            seller.sendMessage("§aDein Item wurde verkauft für §e" + auction.getHighestBid() + " §aDiamanten!");
                        } else {
                            addPendingPayout(auction.getSeller(), auction.getHighestBid());
                        }
                        return;
                    }

                    if (seller != null && seller.isOnline()) {
                        addItemToPlayer(seller, auction.getItem());
                        seller.sendMessage("§eDein Item aus dem Auktionshaus ist abgelaufen und wurde dir zurückgegeben.");

                        addLog("Auktion von " + (seller != null ? seller.getName() : auction.getSeller()) + " für " + auction.getItem().getType().name() + " ist abgelaufen.");
                    }else {
                        addUnclaimedItem(auction.getSeller(), auction.getItem());
                    }
                    iterator.remove();
                }
            }
        }, 20L, 20L * 60);
    }

    public void addUnclaimedItem(UUID playerUUID, ItemStack item) {
        unclaimedItems.computeIfAbsent(playerUUID, k -> new ArrayList<>()).add(item);
        saveUnclaimedItems();
    }

    public void saveUnclaimedItems() {
        unclaimedConfig.set("unclaimed", null);
        for (UUID uuid : unclaimedItems.keySet()) {
            List<ItemStack> items = unclaimedItems.get(uuid);
            unclaimedConfig.set("unclaimed." + uuid.toString(), items);
        }
        try {
            unclaimedConfig.save(unclaimedFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadUnclaimedItems() {
        unclaimedItems.clear();
        if (unclaimedConfig.contains("unclaimed")) {
            for (String key : unclaimedConfig.getConfigurationSection("unclaimed").getKeys(false)) {
                List<ItemStack> items = (List<ItemStack>) unclaimedConfig.getList("unclaimed." + key);
                unclaimedItems.put(UUID.fromString(key), items);
            }
        }
    }

    public boolean hasUnclaimedItems(UUID uuid) {
        return unclaimedItems.containsKey(uuid) && !unclaimedItems.get(uuid).isEmpty();
    }

    public List<ItemStack> claimUnclaimedItems(UUID uuid) {
        List<ItemStack> items = new ArrayList<>(unclaimedItems.getOrDefault(uuid, new ArrayList<>()));
        unclaimedItems.remove(uuid);
        saveUnclaimedItems();
        return items;
    }

    public void banItem(Material material) {
        bannedItems.add(material);
        saveBannedItems();
    }

    public void unbanItem(Material material) {
        bannedItems.remove(material);
        saveBannedItems();
    }

    public boolean isBanned(Material material) {
        return bannedItems.contains(material);
    }

    private void saveBannedItems() {
        List<String> list = new ArrayList<>();
        for (Material mat : bannedItems) {
            list.add(mat.name());
        }
        bannedConfig.set("banned", list);
        try {
            bannedConfig.save(bannedItemsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addLog(String log) {
        String formattedLog = "§8[§f" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) + "§8] §7" + log;
        logs.add(formattedLog);
        if (logs.size() > 50) {
            logs.remove(0);
        }
        saveLogs();
    }

    public void saveLogs() {
        logConfig.set("logs", logs);
        try {
            logConfig.save(logFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadLogs() {
        logs.clear();
        if (logConfig.contains("logs")) {
            logs.addAll(logConfig.getStringList("logs"));
        }
    }

    public void savePendingPayouts() {
        payoutsConfig.set("payouts", null);
        for (UUID uuid : pendingPayouts.keySet()) {
            payoutsConfig.set("payouts." + uuid.toString(), pendingPayouts.get(uuid));
        }
        try {
            payoutsConfig.save(payoutsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadPendingPayouts() {
        pendingPayouts.clear();
        if (payoutsConfig.contains("payouts")) {
            for (String key : payoutsConfig.getConfigurationSection("payouts").getKeys(false)) {
                int amount = payoutsConfig.getInt("payouts." + key);
                pendingPayouts.put(UUID.fromString(key), amount);
            }
        }
    }

    public void addPendingPayment(UUID buyerUUID, ItemStack item, int amount) {
        PendingPayment payment = new PendingPayment(item, amount);
        pendingPayments.computeIfAbsent(buyerUUID, k -> new ArrayList<>()).add(payment);
        savePendingPayments();
    }

    public void claimPendingPayment(Player player) {
        UUID uuid = player.getUniqueId();
        if (pendingPayments.containsKey(uuid)) {
            List<PendingPayment> payments = new ArrayList<>(pendingPayments.get(uuid));
            Iterator<PendingPayment> iterator = payments.iterator();
            while (iterator.hasNext()) {
                PendingPayment payment = iterator.next();
                if (getPlayerDiamonds(player) >= payment.getAmount()) {
                    removePlayerDiamonds(player, payment.getAmount());
                    player.getInventory().addItem(payment.getItem());
                    player.sendMessage("§aDeine Zahlung von §e" + payment.getAmount() + " §aDiamanten wurde durchgeführt. Dein Item wurde dir gegeben!");
                    iterator.remove();
                } else {
                    player.sendMessage("§cDu hast nicht genug Diamanten, um dein Auktions-Item zu bezahlen!");
                    break;
                }
            }
            if (payments.isEmpty()) {
                pendingPayments.remove(uuid);
            } else {
                pendingPayments.put(uuid, payments);
            }
            savePendingPayments();
        }
    }

    public void savePendingPayments() {
        paymentsConfig.set("payments", null);
        for (UUID uuid : pendingPayments.keySet()) {
            List<Map<String, Object>> serialized = new ArrayList<>();
            for (PendingPayment payment : pendingPayments.get(uuid)) {
                Map<String, Object> data = new HashMap<>();
                data.put("item", payment.getItem());
                data.put("amount", payment.getAmount());
                data.put("createdAt", payment.getCreatedAt());
                serialized.add(data);
            }
            paymentsConfig.set("payments." + uuid.toString(), serialized);
        }
        try {
            paymentsConfig.save(paymentsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadPendingPayments() {
        pendingPayments.clear();
        if (paymentsConfig.contains("payments")) {
            for (String key : paymentsConfig.getConfigurationSection("payments").getKeys(false)) {
                List<Map<?, ?>> dataList = paymentsConfig.getMapList("payments." + key);
                List<PendingPayment> payments = new ArrayList<>();
                for (Map<?, ?> data : dataList) {
                    ItemStack item = (ItemStack) data.get("item");
                    int amount = (int) data.get("amount");
                    long createdAt = (long) data.get("createdAt");
                    PendingPayment payment = new PendingPayment(item, amount);

                    try {
                        Field field = PendingPayment.class.getDeclaredField("createdAt");
                        field.setAccessible(true);
                        field.set(payment, createdAt);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    payments.add(payment);
                }
                pendingPayments.put(UUID.fromString(key), payments);
            }
        }
    }

    public void checkExpiredPendingPayments() {
        Iterator<Map.Entry<UUID, List<PendingPayment>>> iterator = pendingPayments.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, List<PendingPayment>> entry = iterator.next();
            UUID buyerUUID = entry.getKey();
            List<PendingPayment> payments = entry.getValue();
            Iterator<PendingPayment> paymentIterator = payments.iterator();
            while (paymentIterator.hasNext()) {
                PendingPayment payment = paymentIterator.next();
                if (System.currentTimeMillis() - payment.getCreatedAt() > 7 * 24 * 60 * 60 * 1000L) {

                    Player seller = Bukkit.getPlayer(buyerUUID);
                    if (seller != null && seller.isOnline()) {
                        seller.getInventory().addItem(payment.getItem());
                        seller.sendMessage("§eEin nicht bezahltes Auktions-Item wurde dir zurückgegeben.");
                    } else {
                        addUnclaimedItem(buyerUUID, payment.getItem());
                    }
                    paymentIterator.remove();
                    addLog("Nicht bezahltes Auktions-Item wurde nach 7 Tagen zurückgegeben: " + payment.getItem().getType().name());
                }
            }
            if (payments.isEmpty()) {
                iterator.remove();
            }
        }
        savePendingPayments();
    }

    public boolean payDebts(Player player) {
        UUID uuid = player.getUniqueId();
        boolean paidAny = false;
        if (pendingPayments.containsKey(uuid)) {
            List<PendingPayment> payments = new ArrayList<>(pendingPayments.get(uuid));
            Iterator<PendingPayment> iterator = payments.iterator();
            while (iterator.hasNext()) {
                PendingPayment payment = iterator.next();
                if (getPlayerDiamonds(player) >= payment.getAmount()) {
                    removePlayerDiamonds(player, payment.getAmount());
                    HashMap<Integer, ItemStack> notStored = player.getInventory().addItem(payment.getItem());
                    if (!notStored.isEmpty()) {
                        for (ItemStack leftover : notStored.values()) {
                            player.getWorld().dropItemNaturally(player.getLocation(), leftover);
                        }
                    }
                    player.sendMessage("§aSchuld von §e" + payment.getAmount() + " §aDiamanten bezahlt! Du hast dein Auktions-Item erhalten.");
                    iterator.remove();
                    paidAny = true;
                } else {
                    int remaining = payment.getAmount() - getPlayerDiamonds(player);
                    player.sendMessage("§cDu hast nicht genug Diamanten für eine ausstehende Zahlung! (§b" + remaining + "§c)");
                    break;
                }
            }
            if (payments.isEmpty()) {
                pendingPayments.remove(uuid);
            } else {
                pendingPayments.put(uuid, payments);
            }
            savePendingPayments();
        }
        return paidAny;
    }

    public void addPlayerDiamonds(Player player, int amount) {
        ItemStack diamonds = new ItemStack(Material.DIAMOND, amount);
        HashMap<Integer, ItemStack> notStored = player.getInventory().addItem(diamonds);
        if (!notStored.isEmpty()) {
            for (ItemStack leftover : notStored.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), leftover);
            }
        }
    }

    public void addPendingPayout(UUID uuid, int amount) {
        pendingPayouts.put(uuid, pendingPayouts.getOrDefault(uuid, 0) + amount);
        savePendingPayouts();
    }

    public int getPendingPayout(UUID uuid) {
        return pendingPayouts.getOrDefault(uuid, 0);
    }

    public void claimPendingPayout(Player player) {
        UUID uuid = player.getUniqueId();
        if (pendingPayouts.containsKey(uuid)) {
            int amount = pendingPayouts.remove(uuid);
            addPlayerDiamonds(player, amount);
            player.sendMessage("§aDu hast §e" + amount + " §aDiamanten von Auktionen erhalten!");
            savePendingPayouts();
        }
    }

    public void addItemToPlayer(Player player, ItemStack item) {
        HashMap<Integer, ItemStack> notStored = player.getInventory().addItem(item);
        if (!notStored.isEmpty()) {
            for (ItemStack leftover : notStored.values()) {
                player.getWorld().dropItemNaturally(player.getLocation(), leftover);
            }
        }
    }


    public List<String> getLogs() {
        return new ArrayList<>(logs);
    }
}