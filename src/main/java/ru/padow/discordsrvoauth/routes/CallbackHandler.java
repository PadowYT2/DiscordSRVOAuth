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

package ru.padow.discordsrvoauth.routes;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import github.scarsz.discordsrv.DiscordSRV;

import ru.padow.discordsrvoauth.Config;
import ru.padow.discordsrvoauth.DiscordSRVOAuth;
import ru.padow.discordsrvoauth.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CallbackHandler implements HttpHandler {
    private Config config = DiscordSRVOAuth.config();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query == null || !query.contains("code") || !query.contains("state")) {
            exchange.sendResponseHeaders(400, -1);
            exchange.close();

            return;
        }

        Map<String, String> params = Utils.queryToMap(query);
        String code = params.get("code");
        String state = params.get("state");

        if (state.length() != 4
                || !DiscordSRV.getPlugin()
                        .getAccountLinkManager()
                        .getLinkingCodes()
                        .containsKey(state)) {
            exchange.sendResponseHeaders(403, -1);
            exchange.close();

            return;
        }

        String tokenResponse = null;
        try {
            tokenResponse =
                    Utils.post(
                            "https://discord.com/api/oauth2/token",
                            new HashMap<String, String>() {
                                {
                                    put("client_id", config.getClientId());
                                    put("client_secret", config.getClientSecret());
                                    put("grant_type", "authorization_code");
                                    put("code", code);
                                    put(
                                            "redirect_uri",
                                            Utils.getBaseURL(config, true) + "/callback");
                                }
                            });
        } catch (InterruptedException e) {
            // NO-OP
        }

        String accessToken =
                JsonParser.parseString(tokenResponse)
                        .getAsJsonObject()
                        .get("access_token")
                        .getAsString();

        String userResponse = null;
        try {
            userResponse = Utils.get("https://discord.com/api/users/@me", accessToken);
        } catch (InterruptedException e) {
            // NO-OP
        }
        JsonObject userJson = JsonParser.parseString(userResponse).getAsJsonObject();

        String id = userJson.get("id").getAsString();
        if (id == null || id.isEmpty()) {
            exchange.sendResponseHeaders(400, -1);
            exchange.close();

            return;
        }

        String token = config.getBotToken();
        if (token != null && !token.isEmpty()) {
            try {
                Utils.put(
                        "https://discord.com/api/guilds/" + config.getGuildId() + "/members/" + id,
                        Map.of("access_token", accessToken));
            } catch (InterruptedException e) {
                // NO-OP
            }
        }

        String response =
                "<style>@import"
                    + " url(https://fonts.bunny.net/css?family=inter:600);html{color-scheme:dark}body{margin:0}a{font-weight:600;font-size:1.2rem;height:100vh;display:flex;align-items:center;justify-content:center;font-family:Inter,sans-serif}</style>"
                    + "<a>"
                        + DiscordSRV.getPlugin().getAccountLinkManager().process(state, id)
                        + "</a>";
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
            os.close();
        }

        exchange.close();
    }
}
