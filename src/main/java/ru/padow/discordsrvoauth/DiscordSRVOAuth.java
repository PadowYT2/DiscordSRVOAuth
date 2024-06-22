/*
 * DiscordSRVOAuth - https://github.com/PadowYT2/DiscordSRVOAuth
 * Copyright (C) 2024  PadowYT2
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.padow.discordsrvoauth;

import com.sun.net.httpserver.HttpServer;

import github.scarsz.configuralize.DynamicConfig;
import github.scarsz.configuralize.ParseException;
import github.scarsz.discordsrv.DiscordSRV;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.ChatColor;

import org.bstats.bukkit.Metrics;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import ru.padow.discordsrvoauth.routes.CallbackHandler;
import ru.padow.discordsrvoauth.routes.LinkHandler;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class DiscordSRVOAuth extends JavaPlugin implements Listener {
    private HttpServer server;
    private DynamicConfig config;

    @Override
    public void onEnable() {
        config = new DynamicConfig();
        config.addSource(DiscordSRVOAuth.class, "config", new File(getDataFolder(), "config.yml"));

        loadConfig();
        startServer();

        if (config.getBoolean("bstats")) new Metrics(this, 22313);

        Logger logger = getLogger();

        try {
            Class.forName("net.kyori.adventure.text.minimessage.MiniMessage");

            logger.info("Using MiniMessage for the kick message");
        } catch (Exception e) {
            logger.info("Using legacy codes for the kick message");
        }

        getServer().getPluginManager().registerEvents(this, this);
        getCommand("discordsrvoauth").setExecutor(this);
    }

    @Override
    public void onDisable() {
        server.stop(1);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("discordsrvoauth")) {
            if (args.length == 0) {
                String message =
                        String.join(
                                "\n",
                                "  <color:#235a8a>█<color:#a77044>█<color:#235a8c>█<color:#225786>█<color:#245c8f>█<color:#1f4f7a>█<color:#a77044>█<color:#235a8a>█",
                                "  <color:#a77044>█<color:#fbaca2>█<color:#95643d>█<color:#1f4f7a>█<color:#20527e>█<color:#95643d>█<color:#fbaca2>█<color:#a77044>█",
                                "  <color:#1e4d77>█<color:#235a8c>█<color:#163856>█<color:#1a4266>█<color:#1a4266>█<color:#1b456a>█<color:#1f4f7a>█<color:#245c8f>█",
                                "  <color:#1e4c77>█<color:#163856>█<color:#ae8f79>█<color:#c19d82>█<color:#ae8f79>█<color:#c19d82>█<color:#163856>█<color:#20527e>█"
                                    + "    <gold>DiscordSRVOAuth <gray>v"
                                        + getDescription().getVersion(),
                                "  <color:#193f62>█<color:#c29e84>█<color:#ae8f79>█<color:#c9a68c>█<color:#fbbc94>█<color:#fbbc94>█<color:#c9a68b>█<color:#1d4a72>█"
                                    + "       <green>Made by"
                                    + " <click:open_url:'https://padow.ru'><color:#256091>PadowYT2</click>",
                                "  <color:#c9a68b>█<color:#fbbc94>█<color:#fbbc94>█<color:#febb92>█<color:#febb92>█<color:#ffb991>█<color:#ffb991>█<color:#c9a68b>█",
                                "  <color:#ffb990>█<color:#ffb991>█<color:#a67044>█<color:#242424>█<color:#333332>█<color:#a67044>█<color:#ffb991>█<color:#ffb991>█",
                                "  <color:#ffb584>█<color:#ffb990>█<color:#ffc197>█<color:#f87e70>█<color:#fa9589>█<color:#ffc197>█<color:#ffb990>█<color:#ffb584>█");

                try {
                    if (!(sender instanceof Player)) throw new Exception();
                    Class.forName("net.kyori.adventure.text.minimessage.MiniMessage");

                    sender.sendMessage(
                            MiniMessage.miniMessage().deserialize("\n" + message + "\n"));
                } catch (Exception e) {
                    sender.sendMessage(
                            ChatColor.GOLD
                                    + "DiscordSRVOAuth"
                                    + ChatColor.GRAY
                                    + "v"
                                    + getDescription().getVersion()
                                    + ChatColor.GREEN
                                    + "Made by "
                                    + ChatColor.DARK_BLUE
                                    + "PadowYT2");
                }

                return true;
            }

            if (args[0].equalsIgnoreCase("reload")
                    && sender.hasPermission("discordsrvoauth.reload")) {
                server.stop(1);

                loadConfig();
                startServer();

                sender.sendMessage(ChatColor.GREEN + "Reloaded the plugin");

                return true;
            }
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(
            CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("discordsrvoauth")
                && sender.hasPermission("discordsrvoauth.reload")
                && args.length == 1) {
            return Arrays.asList("reload");
        }

        return null;
    }

    @SuppressWarnings("deprecation")
    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
        UUID playerUuid = event.getUniqueId();
        String discordId =
                DiscordSRV.getPlugin().getAccountLinkManager().getDiscordIdBypassCache(playerUuid);

        if (discordId == null) {
            String code = DiscordSRV.getPlugin().getAccountLinkManager().generateCode(playerUuid);
            String route = "/" + config.getString("link_route") + "?code=" + code;
            String url =
                    config.getBoolean("https")
                            ? config.getString("url") + route
                            : config.getString("url") + ":" + config.getInt("port") + route;

            String kickMessage =
                    config.getString("kick_message")
                            .replaceAll("&", "§")
                            .replace(
                                    "{JOIN}",
                                    (config.getBoolean("https") ? "https://" : "http://") + url)
                            .replace("{KICK}", url);

            try {
                Class.forName("net.kyori.adventure.text.minimessage.MiniMessage");

                event.disallow(
                        AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST,
                        MiniMessage.miniMessage().deserialize(kickMessage));
            } catch (Exception e) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, kickMessage);
            }
        }
    }

    public void loadConfig() {
        try {
            config.saveAllDefaults(false);
            config.loadAll();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void startServer() {
        try {
            server = HttpServer.create(new InetSocketAddress(config.getInt("port")), 0);
            server.createContext("/", exchange -> exchange.sendResponseHeaders(404, -1));
            server.createContext("/" + config.getString("link_route"), new LinkHandler(config));
            server.createContext("/callback", new CallbackHandler(config));
            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
