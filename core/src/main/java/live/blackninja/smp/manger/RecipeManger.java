package live.blackninja.smp.manger;

import live.blackninja.smp.Core;
import live.blackninja.smp.builder.ItemBuilder;
import live.blackninja.smp.builder.MessageBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

public class RecipeManger {

    private final Core instance;

    public RecipeManger(Core instance) {
        this.instance = instance;

        registerDriedGhastRecipe();
    }

    public void registerDriedGhastRecipe() {

        NamespacedKey key = new NamespacedKey(instance, "dried_ghast");
        ShapedRecipe recipe = new ShapedRecipe(key, new ItemBuilder(Material.DRIED_GHAST).build());

        recipe.shape("BSB", "PCP", "DPD");

        recipe.setIngredient('S', Material.SNOWBALL);
        recipe.setIngredient('B', Material.BLUE_ICE);
        recipe.setIngredient('P', Material.PACKED_ICE);
        recipe.setIngredient('D', Material.DIAMOND);

        ItemStack cloudCatalyst = new ItemBuilder(Material.IRON_HORSE_ARMOR)
                .setDisplayName(MessageBuilder.build("<italic:false><color:#c5f5fc>Cloud Catalyst</color>"))
                .setModel("smp:cloud_catalyst")
                .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                .build();

        RecipeChoice.ExactChoice exact = new RecipeChoice.ExactChoice(cloudCatalyst);
        recipe.setIngredient('C', exact);

        Bukkit.addRecipe(recipe);
    }

}
