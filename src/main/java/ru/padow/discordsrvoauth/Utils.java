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

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Utils {
    private static final HttpClient client = HttpClient.newHttpClient();

    public static String post(String url, Map<String, String> params)
            throws IOException, InterruptedException {
        String parameters =
                params.entrySet().stream()
                        .map(
                                entry ->
                                        URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)
                                                + "="
                                                + URLEncoder.encode(
                                                        entry.getValue(), StandardCharsets.UTF_8))
                        .collect(Collectors.joining("&"));

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                        .POST(HttpRequest.BodyPublishers.ofString(parameters))
                        .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static String put(String url, Map<String, String> params)
            throws IOException, InterruptedException {
        String json = new Gson().toJson(params);
        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json; charset=UTF-8")
                        .PUT(HttpRequest.BodyPublishers.ofString(json))
                        .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static String get(String url, String accessToken)
            throws IOException, InterruptedException {
        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + accessToken)
                        .GET()
                        .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public static Map<String, String> queryToMap(String query) {
        return Arrays.stream(query.split("&"))
                .map(param -> param.split("="))
                .collect(
                        Collectors.toMap(
                                pair -> URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                                pair -> URLDecoder.decode(pair[1], StandardCharsets.UTF_8)));
    }

    public static String getBaseURL(Config config, Boolean protocol) {
        return (protocol ? (config.isHttps() ? "https://" : "http://") : "")
                + config.getUrl()
                + (config.isHttps() ? "" : ":" + config.getPort());
    }
}
