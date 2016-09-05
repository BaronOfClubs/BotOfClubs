package net.baronofclubs.botofclubs;

import com.google.gson.JsonObject;

import java.time.ZonedDateTime;

/**
 * DESCRIPTION: Interface defining the methods to be used by all properties.
 */
public interface Properties {

    boolean save();
    boolean load();
    boolean exists();

    JsonObject getAsJsonObject();
    void fromJsonObject(JsonObject jsonObject);
    void setDefaults();

    ZonedDateTime getLastSave();

}
