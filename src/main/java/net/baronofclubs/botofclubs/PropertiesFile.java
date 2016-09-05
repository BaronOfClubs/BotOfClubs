package net.baronofclubs.botofclubs;

import com.google.gson.*;
import net.baronofclubs.Debug;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;

/**
 * DESCRIPTION: Abstract Parent Class for controlling file saving and conversion to/from json using Gson.
 * DETAILS:
 *      Use: Extend this class in combination with implementing Properties to save and load files from disk.
 *      Add toJson(), fromJson(), and setDefaults() methods to the child class. This will control the behavior
 *      of the Json using Gson. Check examples such as the Settings class.
 */

public abstract class PropertiesFile implements Properties {

    private String fileName;
    private Path filePath;
    private Charset encoding;

    private ZonedDateTime lastSave;

    private GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting().serializeNulls();
    private Gson gson = gsonBuilder.create();

    public PropertiesFile(String fileName, Path filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.encoding = FALLBACKS.encoding;
        if(!exists()) {
            setDefaults();
            save();
        } else {
            load();
        }
        debug("Created with default charset");
    }

    public PropertiesFile(String fileName, Path filePath, Charset encoding) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.encoding = encoding;
        if(!exists()) {
            save();
        } else {
            load();
        }
        debug("Created with non-default charset");
    }

    public boolean save() {
        debug("Saving");
        JsonObject jsonObject;
        try {
            jsonObject = getAsJsonObject();
        } catch (NullPointerException e) {
            setDefaults();
            jsonObject = getAsJsonObject();
        }

        if (!exists()) {
            setDefaults();
            createBlank();
        }

        lastSave = ZonedDateTime.now();
        jsonObject.addProperty("last_save", lastSave.toString());

        String jsonString = gson.toJson(jsonObject);

        writeString(jsonString);
        return true;
    }

    public boolean load() {
        debug("Loading");
        Path fullPath = Paths.get(filePath.toString() + File.separator + fileName);

        InputStream inputStream;
        try {
            inputStream = new FileInputStream(fullPath.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        Reader inputStreamReader = new InputStreamReader(inputStream);

        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(inputStreamReader);
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        lastSave = ZonedDateTime.parse(jsonObject.get("last_save").getAsString());
        fromJsonObject(jsonObject);

        return true;
    }

    public boolean exists() {
        debug("Checking file");
        Path fullPath = Paths.get(filePath.toString() + File.separator + fileName);
        File file = new File(fullPath.toString());
        debug("File exists: " + file.exists());
        return file.exists();
    }

    public ZonedDateTime getLastSave() {
        return lastSave;
    }

    private boolean createBlank() {
        debug("Creating blank file");
        Path fullPath = Paths.get(filePath.toString() + File.separator + fileName);
        try {
            File file = new File(fullPath.toString());
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean writeString(String string) {
        debug("Writing file");
        Path fullPath = Paths.get(filePath.toString() + File.separator + fileName);
        try {
            // Note: I hate Java's file output system. I mean, look at this! Bullshit, I tell you.
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fullPath.toString()), encoding));
            writer.write(string);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    private static final class FALLBACKS {
        public static Charset encoding = StandardCharsets.UTF_8;
    }

    private void debug(String status) {
        Debug.print("(" + fileName + ") " + status, "Properties File", Debug.Level.MEDIUM);
    }
}
