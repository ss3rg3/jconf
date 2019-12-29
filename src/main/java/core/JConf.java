package core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exceptions.Exceptions.JConfCouldNotReadFileConfigException;
import exceptions.Exceptions.JConfFailedToMapJsonToObjectConfig;
import exceptions.Exceptions.JConfFailedToWriteConfigException;
import exceptions.Exceptions.JConfPathToJsonFileDoesNotExistConfigException;
import exceptions.Exceptions.JConfFailedToCreateConfigException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * <h1>How To</h1>
 * <ul>
 *      <li>Initialize via static factory method JConf.create()</li>
 *      <li>If your config needs default values, you can simply set them in the POJO. If your JSON file doesn't contain the field,
 *          then the value from the POJO stays, otherwise it's overwritten. BUT: If you save that config again, then the default value
 *          will be written into the JSON file</li>
 *      <li>If you want GSON to always ignore fields (e.g. for default values), you can use `transient`</li>
 *      <li>Your config file can contain methods. These will be ignored by GSON.</li>
 *      <li>If you have serializable object fields (e.g. `Pattern` fo regex), then you can create GSON with additional
 *      `TypeAdapters` (see `new GsonBuilder().registerTypeAdapter()`)</li>
 *      <li>If you need some fancy logic like @Required fields then you need to implement your own Deserializer in GSON, see here:
 *          https://stackoverflow.com/questions/21626690/gson-optional-and-required-fields</li>
 * </ul>
 */
public class JConf<T> {

    private transient final String pathToJsonFile;
    private final Class<T> classToMapTo;
    private final Gson gson;
    private T configPojo;

    private JConf(final String pathToJsonFile, final Class<T> classToMapTo) throws JConfFailedToCreateConfigException {
        this.pathToJsonFile = pathToJsonFile;
        this.classToMapTo = classToMapTo;
        this.gson = new GsonBuilder().setPrettyPrinting().create();

        this.configPojo = this.loadFromJson();
    }

    private JConf(final String pathToJsonFile, final Class<T> classToMapTo, final Gson gson) throws JConfFailedToCreateConfigException {
        this.pathToJsonFile = pathToJsonFile;
        this.classToMapTo = classToMapTo;
        this.gson = gson;

        this.configPojo = this.loadFromJson();
    }

    // ------------------------------------------------------------------------------------------ //
    // GET CONFIG
    // ------------------------------------------------------------------------------------------ //

    public T get() {
        return this.configPojo;
    }

    public String getJson() {
        return this.gson.toJson(this.configPojo);
    }

    // ------------------------------------------------------------------------------------------ //
    // CREATE (public static factory methods)
    // ------------------------------------------------------------------------------------------ //

    /**
     * This will use the standard implementation of GSON for mapping the JSON to the config POJO
     */
    public static <T> JConf<T> create(final String location, final Class<T> clazz) throws JConfFailedToCreateConfigException {
        return new JConf<>(location, clazz);
    }

    /**
     * This will use your custom implementation of GSON for mapping fromJSON
     */
    public static <T> JConf<T> create(final String location, final Class<T> clazz, final Gson gson) throws JConfFailedToCreateConfigException {
        return new JConf<>(location, clazz, gson);
    }


    // ------------------------------------------------------------------------------------------ //
    // SAVE
    // ------------------------------------------------------------------------------------------ //

    public synchronized void save() throws JConfFailedToWriteConfigException {
        try {
            Files.write(Paths.get(this.pathToJsonFile),
                    this.gson.toJson(this.configPojo).getBytes(),
                    StandardOpenOption.CREATE);
        } catch (final IOException e) {
            throw new JConfFailedToWriteConfigException("Couldn't write config file to: " + this.pathToJsonFile, e);
        }
        try {
            this.configPojo = this.loadFromJson();
        } catch (Exception e) {
            throw new JConfFailedToWriteConfigException("", e);
        }
    }

    // ------------------------------------------------------------------------------------------ //
    // LOAD
    // ------------------------------------------------------------------------------------------ //

    private T loadFromJson() throws JConfFailedToCreateConfigException {

        final Path pathToJsonFile = Paths.get(this.pathToJsonFile);
        if (!pathToJsonFile.toFile().exists()) {
            throw new JConfPathToJsonFileDoesNotExistConfigException("File does not exist: " + this.pathToJsonFile);
        }

        final String json;
        try {
            json = new String(Files.readAllBytes(pathToJsonFile));
        } catch (final IOException e) {
            throw new JConfCouldNotReadFileConfigException("Failed to read file.", e);
        }

        try {
            return this.gson.fromJson(json, this.classToMapTo);
        } catch (final Exception e) {
            throw new JConfFailedToMapJsonToObjectConfig("Couldn't map JSON to object", e);
        }
    }


}
