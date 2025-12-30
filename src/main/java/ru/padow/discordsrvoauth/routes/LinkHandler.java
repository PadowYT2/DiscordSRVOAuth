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

package ru.padow.discordsrvoauth.routes;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import github.scarsz.discordsrv.DiscordSRV;

import ru.padow.discordsrvoauth.Config;
import ru.padow.discordsrvoauth.DiscordSRVOAuth;
import ru.padow.discordsrvoauth.Utils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class LinkHandler implements HttpHandler {
    private Config config = DiscordSRVOAuth.config();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query == null || !query.contains("code")) {
            exchange.sendResponseHeaders(400, -1);
            exchange.close();

            return;
        }

        String code = Utils.queryToMap(query).get("code").replaceAll("[^0-9]", "");

        if (code.length() != 4
                || !DiscordSRV.getPlugin()
                        .getAccountLinkManager()
                        .getLinkingCodes()
                        .containsKey(code)) {
            exchange.sendResponseHeaders(400, -1);
            exchange.close();

            return;
        }

        String token = config.getBotToken();
        String redirect_uri = Utils.getBaseURL(config, true) + "/callback";
        String url =
                "https://discord.com/api/oauth2/authorize?client_id="
                        + config.getClientId()
                        + "&redirect_uri="
                        + URLEncoder.encode(redirect_uri, StandardCharsets.UTF_8)
                        + "&response_type=code&scope=identify"
                        + ((token != null && !token.isEmpty()) ? "+guilds.join" : "")
                        + "&state="
                        + code;

        exchange.getResponseHeaders().set("Location", url);
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }
}
