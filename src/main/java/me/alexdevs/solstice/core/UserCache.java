package me.alexdevs.solstice.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import me.alexdevs.solstice.Solstice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * The reason I made this instead of using Minecraft's UserCache is because:
 * <p>
 * 1. I do not want to use the API to look up missing profiles, just return an empty value instead.
 * <p>
 * 2. Using the API to look up profiles is slow and hangs the server, it's annoying.
 * <p>
 * The source file is the original usercache.json and saving is disabled.
 */
public class UserCache {
    private final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .disableHtmlEscaping()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    private final Map<String, Entry> byName = Maps.newConcurrentMap();
    private final Map<UUID, Entry> byUUID = Maps.newConcurrentMap();
    private final AtomicLong accessCount = new AtomicLong();
    private final File cacheFile;

    public UserCache(File cacheFile) {
        this.cacheFile = cacheFile;
        Lists.reverse(this.load()).forEach(this::add);
    }

    private long incrementAndGetAccessCount() {
        return this.accessCount.incrementAndGet();
    }

    public Optional<GameProfile> getByName(String name) {
        name = name.toLowerCase(Locale.ROOT);
        var entry = byName.get(name);
        if (entry != null && new Date().getTime() >= entry.expirationDate.getTime()) {
            this.byUUID.remove(entry.getProfile().getId());
            this.byName.remove(entry.getProfile().getName().toLowerCase(Locale.ROOT));
            return Optional.empty();
        }

        if (entry == null) {
            return Optional.empty();
        } else {
            return Optional.of(entry.getProfile());
        }
    }

    public Optional<GameProfile> getByUUID(UUID uuid) {
        var entry = byUUID.get(uuid);
        if (entry == null) {
            return Optional.empty();
        } else {
            return Optional.of(entry.getProfile());
        }
    }

    public void add(GameProfile profile) {
        var calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, 1);
        var date = calendar.getTime();
        var entry = new Entry(profile, date);
        this.add(entry);
        //this.save();
    }

    private void add(Entry entry) {
        var gameProfile = entry.getProfile();
        entry.setLastAccessed(this.incrementAndGetAccessCount());
        var name = gameProfile.getName();
        if (name != null) {
            this.byName.put(name.toLowerCase(Locale.ROOT), entry);
        }

        var uuid = gameProfile.getId();
        if (uuid != null) {
            byUUID.put(uuid, entry);
        }
    }

    private static JsonElement entryToJson(Entry entry, DateFormat dateFormat) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", entry.getProfile().getName());
        UUID uUID = entry.getProfile().getId();
        jsonObject.addProperty("uuid", uUID == null ? "" : uUID.toString());
        jsonObject.addProperty("expiresOn", dateFormat.format(entry.getExpirationDate()));
        return jsonObject;
    }

    private static Optional<Entry> entryFromJson(JsonElement json, DateFormat dateFormat) {
        if (!json.isJsonObject())
            return Optional.empty();

        var root = json.getAsJsonObject();
        var nameJson = root.get("name");
        var uuidJson = root.get("uuid");
        var expiresJson = root.get("expiresOn");
        if (nameJson == null || uuidJson == null) {
            return Optional.empty();
        }

        var uuid = uuidJson.getAsString();
        var name = nameJson.getAsString();
        Date date = null;
        if (expiresJson != null) {
            try {
                date = dateFormat.parse(expiresJson.getAsString());
            } catch (ParseException e) {
            }
        }

        if (name != null && uuid != null && date != null) {
            UUID uUID;
            try {
                uUID = UUID.fromString(uuid);
            } catch (Throwable e) {
                return Optional.empty();
            }

            return Optional.of(new Entry(new GameProfile(uUID, name), date));
        }
        return Optional.empty();
    }

    public List<Entry> load() {
        var list = new ArrayList<Entry>();

        try {
            var reader = Files.newReader(this.cacheFile, StandardCharsets.UTF_8);

            try {
                var array = gson.fromJson(reader, JsonArray.class);
                if (array == null)
                    return list;

                var dateFormat = getDateFormat();
                array.forEach(json -> entryFromJson(json, dateFormat).ifPresent(list::add));
            } catch (Exception e) {
                try {
                    reader.close();
                } catch (Throwable ee) {
                    e.addSuppressed(ee);
                }
            }

            if (reader != null)
                reader.close();

            return list;
        } catch (FileNotFoundException e) {
        } catch (JsonParseException | IOException e) {
            Solstice.LOGGER.warn("Failed to load Solstice profile cache {}", this.cacheFile, e);
        }

        return list;
    }

    private void save() {
        var jsonArray = new JsonArray();
        var dateFormat = getDateFormat();
        this.getLastAccessedEntries(1000).forEach(entry -> jsonArray.add(entryToJson(entry, dateFormat)));
        var json = this.gson.toJson(jsonArray);

        try {
            var writer = Files.newWriter(this.cacheFile, StandardCharsets.UTF_8);
            try {
                writer.write(json);
            } catch (IOException e) {
                try {
                    writer.close();
                } catch (IOException ee) {
                    e.addSuppressed(ee);
                }
                throw e;
            }
            writer.close();
        } catch (IOException e) {
        }
    }

    private Stream<Entry> getLastAccessedEntries(int limit) {
        return ImmutableList.copyOf(this.byUUID.values()).stream()
                .sorted(Comparator.comparing(Entry::getLastAccessed).reversed())
                .limit(limit);
    }

    private static DateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.ROOT);
    }

    public static class Entry {
        private final GameProfile profile;
        final Date expirationDate;
        private volatile long lastAccessed;

        Entry(GameProfile profile, Date expirationDate) {
            this.profile = profile;
            this.expirationDate = expirationDate;
        }

        public GameProfile getProfile() {
            return this.profile;
        }

        public Date getExpirationDate() {
            return this.expirationDate;
        }

        public void setLastAccessed(long lastAccessed) {
            this.lastAccessed = lastAccessed;
        }

        public long getLastAccessed() {
            return this.lastAccessed;
        }
    }
}
