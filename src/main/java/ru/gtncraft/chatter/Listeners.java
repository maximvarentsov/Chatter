package ru.gtncraft.chatter;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class Listeners implements Listener {

    private final Map<UUID, String> lastMessage = new HashMap<>();
    private final TextComponent kickMessage = new TextComponent("В чате запрещен флуд, мат, спам и КАПС.");
    private final Pattern regexp = Pattern.compile(
        "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)"
    );

    public Listeners() {

    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onChat(final ChatEvent event) {

        if (event.isCommand()) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        String message = normalize(event.getMessage());

        // check server ip
        if (regexp.matcher(message).find()) {
            event.getSender().disconnect(kickMessage);
            event.setCancelled(true);
            return;
        }

        // check duplicates
        String last = lastMessage.get(player.getUniqueId());
        if (last != null && last.equals(message)) {
            event.getSender().disconnect(kickMessage);
            event.setCancelled(true);
            return;
        }

        lastMessage.put(player.getUniqueId(), message);

        // check uppercase
        int upper = 0;
        int abc = 0;
        message = event.getMessage();
        for (int i = 0; i < message.length(); i++) {
            char symbol = message.charAt(i);
            if (Character.isAlphabetic(symbol)) {
                if (Character.isUpperCase(symbol)) {
                    upper++;
                }
                abc++;
            }
        }

        if (abc > 2) {
            double percentUpper = (upper / abc) * 100;
            if (percentUpper > 50) {
                event.getSender().disconnect(kickMessage);
                event.setCancelled(true);
            }
        }

        /*if (message.startsWith("!")) {
            message = message.substring(1).trim();
            event.setCancelled(true);
            for (ProxiedPlayer pl : ProxyServer.getInstance().getPlayers()) {
                pl.sendMessage(ChatColor.DARK_GREEN + "[] §e" + s.getName() + ChatColor.DARK_GREEN + ": " + msg);
            }
        }*/
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onPlayerDisconnect(final PlayerDisconnectEvent event) {
        lastMessage.remove(event.getPlayer().getUniqueId());
    }

    String normalize(String message) {
        return message.toLowerCase();
    }
}
