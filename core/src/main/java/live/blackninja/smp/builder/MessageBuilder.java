package live.blackninja.smp.builder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class MessageBuilder {

    public static Component build(String text) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        Component component = miniMessage.deserialize(
                text
                .replace("<%y>", "<color:#fed84f>")
                .replace("<%o>", "<color:#ff8045>")
                .replace("<%r>", "<color:#fe2436>")
                .replace("<%g>", "<color:#4aff65>")
                .replace("<%b>", "<color:#00ddff>")
                .replace("<%p>", "<color:#be57ff>")
                .replace("%>", "»")
                .replace("%<", "«")
                .replace("%.", "●")
                .replace("%->", "→")
                .replace("%<-", "←")
        );

        return component;
    }

    public static String buildOld(String text) {
        String component = text
                .replace("%y", "§x§f§e§d§8§4§f")
                .replace("%o", "§x§f§f§8§0§4§5")
                .replace("%r", "§x§f§e§2§4§3§6")
                .replace("%g", "§x§4§a§f§f§6§5")
                .replace("%b", "§x§0§0§d§d§f§f")
                .replace("%p", "§x§b§e§5§7§f§f")
                .replace("%>", "»")
                .replace("%<", "«")
                .replace("%.", "●")
                .replace("%->", "→")
                .replace("%<-", "←");

        return component;
    }

}
