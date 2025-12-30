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

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Config extends OkaeriConfig {
    @Comment("Enable HTTPS if you have a reverse proxy for the URL")
    private boolean https = false;

    private String url = "example.com";
    private int port = 8082;

    @Comment(
            "This route will be used to use the Discord OAuth2 (ex."
                    + " http://example.com:8080/link?code=xxxx)")
    private String linkRoute = "link";

    @Comment({
        "MiniMessage will only work on PaperMC 1.18.2+ (https://webui.advntr.dev)",
        "On SpigotMC and/or Minecraft 1.18.1 and lower, legacy codes must be used",
        "\nPlaceholders",
        "{JOIN} - {https}://{url}:{port}/{link_route}?code=xxxx",
        "{KICK} - {url}:{port}/{link_route}?code=xxxx",
        "\nYou can also use <click:open_url:'{JOIN}'>click here</click>",
        "if you are not getting kicked from the server on join (only with MiniMessage)"
    })
    private String kickMessage =
            "<gray>To play, you need to link your <color:#5865f2>Discord<gray> account.\n\n"
                    + "<gray>Please go to <green>{KICK} <gray>to link your account.";

    @Comment("You can find these values in your Discord application's settings")
    private String clientId = "000000000000000000";

    private String clientSecret = "";

    @Comment("If you want to have the user to join your guild, fill these values")
    private String botToken = "";

    private long guildId = 0L;

    @Comment({
        "If you have DiscordSRVOAuth setup on an another server and you are",
        "actively syncing DiscordSRV users, you should disable the webserver"
    })
    private boolean disableWebserver = false;

    private boolean bstats = true;

    @Comment("!!! DO NOT EDIT THIS !!!")
    private int version = 1;
}
