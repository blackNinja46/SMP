package live.blackninja.smp.builder;

import live.blackninja.smp.manger.StatType;
import org.bukkit.Material;

public class StatDisplayBuilder {
    public final StatType type;
    public final Material material;
    public final String displayName;
    public final int row, column;

    public StatDisplayBuilder(StatType type, Material material, String displayName, int row, int column) {
        this.type = type;
        this.material = material;
        this.displayName = displayName;
        this.row = row;
        this.column = column;
    }
}
