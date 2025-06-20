package live.blackninja.smp.builder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;

import java.awt.*;

public class TextDisplayBuilder {

    private TextDisplay textDisplay;

    public TextDisplayBuilder(Location location) {

        World world = location.getWorld();
        this.textDisplay = (TextDisplay) world.spawnEntity(location, EntityType.TEXT_DISPLAY);
    }

    public TextDisplayBuilder setText(String text) {
        textDisplay.setText(text);
        return this;
    }

    public TextDisplayBuilder setTextMiniMessage(Component text) {
        textDisplay.text(text);
        return this;
    }

    public TextDisplayBuilder setShadowed(boolean shadowed) {
        textDisplay.setShadowed(shadowed);
        return this;
    }

    public TextDisplayBuilder setBackgroundColor(Color color) {
        textDisplay.setBackgroundColor(color);
        return this;
    }

    public TextDisplayBuilder setDefaultBackground(boolean defaultBackground) {
        textDisplay.setDefaultBackground(defaultBackground);
        return this;
    }

    public TextDisplayBuilder setHeight(float height) {
        textDisplay.setDisplayHeight(height);
        return this;
    }

    public TextDisplayBuilder setWidth(float width) {
        textDisplay.setDisplayWidth(width);
        return this;
    }

    public TextDisplayBuilder setRotation(float yaw, float pitch) {
        textDisplay.setRotation(yaw, pitch);
        return this;
    }

    public TextDisplayBuilder setTag(String tag) {
        textDisplay.addScoreboardTag(tag);
        return this;
    }

    public TextDisplayBuilder setAlignment(TextDisplay.TextAlignment alignment) {
        textDisplay.setAlignment(alignment);
        return this;
    }

    public TextDisplayBuilder setBillboard(Display.Billboard billboard) {
        textDisplay.setBillboard(billboard);
        return this;
    }

    public TextDisplayBuilder setSeeThrough(boolean seeThrough) {
        textDisplay.setSeeThrough(seeThrough);
        return this;
    }

    public TextDisplayBuilder setInvisibleBackground(boolean invisibleBackground) {
        if (invisibleBackground) {
            textDisplay.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        }
        return this;
    }

    public TextDisplayBuilder remove() {
        textDisplay.remove();
        return this;
    }

}
