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

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Utils {
    private static OkHttpClient client = new OkHttpClient();

    public static String post(String url, Map<String, String> params) throws IOException {
        String parameters =
                params.entrySet().stream()
                        .map(
                                entry ->
                                        URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)
                                                + "="
                                                + URLEncoder.encode(
                                                        entry.getValue(), StandardCharsets.UTF_8))
                        .collect(Collectors.joining("&"));

        RequestBody body =
                RequestBody.create(
                        parameters,
                        MediaType.get("application/x-www-form-urlencoded; charset=UTF-8"));
        Request request = new Request.Builder().url(url).post(body).build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public static String put(String url, Map<String, String> params) throws IOException {
        RequestBody body =
                RequestBody.create(
                        new Gson().toJson(params),
                        MediaType.get("application/json; charset=UTF-8"));
        Request request = new Request.Builder().url(url).put(body).build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public static String get(String url, String accessToken) throws IOException {
        Request request =
                new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer " + accessToken)
                        .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public static Map<String, String> queryToMap(String query) {
        return query != null
                ? Arrays.stream(query.split("&"))
                        .map(param -> param.split("="))
                        .collect(
                                Collectors.toMap(
                                        pair -> URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                                        pair -> URLDecoder.decode(pair[1], StandardCharsets.UTF_8)))
                : new HashMap<>();
    }
}
