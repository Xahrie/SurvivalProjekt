package net.mmm.survival.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.util.UUIDTypeAdapter;

public class UUIDFetcher {
  private static final ExecutorService pool = Executors.newCachedThreadPool();
  private static final Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();
  private static final Map<String, UUID> uuidCache = new HashMap<>();
  private static final Map<UUID, String> nameCache = new HashMap<>();
  private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s?at=%d";
  private static final String NAME_URL = "https://api.mojang.com/user/profiles/%s/names";

  private String name;
  private UUID id;

  /**
   * Fetches the uuid asynchronously and passes it to the consumer
   *
   * @param name The name
   * @param action Do what you want to do with the uuid her
   */
  public static void getUUID(final String name, final Consumer<UUID> action) {
    pool.execute(() -> action.accept(getUUID(name)));
  }

  /**
   * Fetches the uuid synchronously and returns it
   *
   * @param name The name
   * @return The uuid
   */
  private static UUID getUUID(final String name) {
    return getUUIDAt(name, System.currentTimeMillis());
  }

  /**
   * Fetches the uuid synchronously for a specified name and time
   *
   * @param name The name
   * @param timestamp Time when the player had this name in milliseconds
   */
  private static UUID getUUIDAt(String name, final long timestamp) {
    name = name.toLowerCase();
    if (uuidCache.containsKey(name)) {
      return uuidCache.get(name);
    }
    try {
      return cacheUUIDsAt(name, timestamp);
    } catch (final IOException ex) {
      ex.printStackTrace();
    }

    return null;
  }

  private static UUID cacheUUIDsAt(final String name, final long timestamp) throws IOException {
    final HttpURLConnection connection = (HttpURLConnection) new URL(String.format(UUID_URL, name, timestamp / 1000)).openConnection();
    connection.setReadTimeout(5000);
    final UUIDFetcher data = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDFetcher.class);

    uuidCache.put(name, data.id);
    nameCache.put(data.id, data.name);

    return data.id;
  }

  /**
   * Fetches the name asynchronously and passes it to the consumer
   *
   * @param uuid The uuid
   * @param action Do what you want to do with the name her
   */
  public static void getName(final UUID uuid, final Consumer<String> action) {
    pool.execute(() -> action.accept(getName(uuid)));
  }

  /**
   * Fetches the name synchronously and returns it
   *
   * @param uuid The uuid
   * @return The name
   */
  public static String getName(final UUID uuid) {
    if (nameCache.containsKey(uuid)) {
      return nameCache.get(uuid);
    }
    try {
      return cacheUUIDs(uuid);
    } catch (final IOException ex) {
      ex.printStackTrace();
    }

    return null;
  }

  private static String cacheUUIDs(final UUID uuid) throws IOException {
    final HttpURLConnection connection = (HttpURLConnection) new URL(String.format(NAME_URL, UUIDTypeAdapter.fromUUID(uuid))).openConnection();
    connection.setReadTimeout(5000);
    final UUIDFetcher[] nameHistory = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDFetcher[].class);
    final UUIDFetcher currentNameData = nameHistory[nameHistory.length - 1];

    uuidCache.put(currentNameData.name.toLowerCase(), uuid);
    nameCache.put(uuid, currentNameData.name);

    return currentNameData.name;
  }
}

