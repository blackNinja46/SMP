package live.blackninja.economy.gui;

public enum AuctionSortMode {
    TIME("End Zeit"),
    PRICE_UP("Preis (aufsteigend)"),
    PRICE_DOWN("Preis (absteigend)"),
    SELF_AUCTION("Eigene Auktionen");

    private String displayName;

    AuctionSortMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
