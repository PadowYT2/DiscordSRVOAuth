/*
 * DiscordSRVOAuth - https://github.com/PadowYT2/DiscordSRVOAuth
 * Copyright (C) 2024 - 2026  PadowYT2
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
import com.tcoded.folialib.FoliaLib;

import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.toml.TomlJacksonConfigurer;
import eu.okaeri.configs.yaml.bukkit.YamlBukkitConfigurer;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;

import lombok.Getter;
import lombok.experimental.Accessors;

import net.kyori.adventure.text.minimessage.MiniMessage;

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
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class DiscordSRVOAuth extends JavaPlugin implements Listener {
    @Getter
    @Accessors(fluent = true)
    private static Config config;

    private FoliaLib foliaLib;
    private HttpServer server;
    private ExecutorService executor;

    @SuppressWarnings("deprecation")
    @Override
    public void onEnable() {
        Logger logger = getLogger();
        foliaLib = new FoliaLib(this);

        File toml = new File(getDataFolder(), "config.toml");
        File yaml = new File(getDataFolder(), "config.yml");

        if (yaml.exists() && !toml.exists()) {
            try {
                Config config =
                        ConfigManager.create(
                                Config.class,
                                (it) -> {
                                    it.withConfigurer(new YamlBukkitConfigurer());
                                    it.withBindFile(yaml);
                                    it.load();
                                });

                config.withConfigurer(new TomlJacksonConfigurer());
                config.withBindFile(toml);
                config.save();

                yaml.renameTo(new File(getDataFolder(), "config.yml.old"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            config =
                    ConfigManager.create(
                            Config.class,
                            (it) -> {
                                it.withConfigurer(new TomlJacksonConfigurer());
                                it.withBindFile(toml);
                                it.saveDefaults();
                                it.load(true);
                            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        foliaLib.getScheduler().runAsync(task -> startServer());

        if (config.isBstats()) new Metrics(this, 22358);

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
        stopServer();
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
                            "§6DiscordSRVOAuth §7v"
                                    + getDescription().getVersion()
                                    + "\n§aMade by §1PadowYT2");
                }

                return true;
            }

            if (args[0].equalsIgnoreCase("reload")
                    && sender.hasPermission("discordsrvoauth.reload")) {
                if (server != null) server.stop(1);

                config.load();

                foliaLib.getScheduler().runAsync(task -> startServer());

                sender.sendMessage("§aReloaded the plugin");

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
        AccountLinkManager accountLinkManager = DiscordSRV.getPlugin().getAccountLinkManager();
        if (accountLinkManager == null) return;

        UUID playerUuid = event.getUniqueId();
        String discordId = accountLinkManager.getDiscordIdBypassCache(playerUuid);

        if (discordId == null) {
            String code = accountLinkManager.generateCode(playerUuid);
            String route = "/" + config.getLinkRoute() + "?code=" + code;

            String kickMessage =
                    config.getKickMessage()
                            .replaceAll("&", "§")
                            .replace("{JOIN}", Utils.getBaseURL(config, true) + route)
                            .replace("{KICK}", Utils.getBaseURL(config, false) + route);

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

    private void startServer() {
        stopServer();

        if (config.isDisableWebserver()) return;

        try {
            System.setProperty("sun.net.httpserver.maxReqTime", "10000");
            System.setProperty("sun.net.httpserver.maxRspTime", "10000");

            server = HttpServer.create(new InetSocketAddress("0.0.0.0", config.getPort()), 50);
            server.createContext(
                    "/",
                    exchange -> {
                        exchange.sendResponseHeaders(404, -1);
                        exchange.close();
                    });
            server.createContext("/" + config.getLinkRoute(), new LinkHandler());
            server.createContext("/callback", new CallbackHandler());

            executor = Executors.newCachedThreadPool();
            server.setExecutor(executor);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopServer() {
        if (executor != null) executor.shutdown();
        if (server != null) server.stop(1);
    }
}
