<!--- PROJECT_TOC -->
[1. Install](readme.md#install)<br>
[2. Usage](readme.md#usage)<br>
[3. Tips](readme.md#tips)<br>
<!--- TOC_END -->




# Install

Clone the repository

```bash
git clone https://github.com/53rg3/jconf.git
```

Then install locally.

```bash
mvn clean install -Dmaven.test.skip=true
```

Add the dependency in the POM. Make sure the versions match (see pom.xml in repository)

```xml
<dependency>
    <groupId>io.github.53rg3</groupId>
    <artifactId>jconf</artifactId>
    <version>0.1.0</version>
</dependency>
```



# Usage

1. Create a JSON config file:

```json
{
  "name": "some_value"
}
```

2. Create a singleton wrapper class, a public static value or a context with a dependency injection library for a `JConf` instance:

```java
public class ExampleConfig {

    public static void main(final String[] args) throws Exception {

        // Load config (You can also provide your own GSON implementation)
        JConf<ConfigPojo> jConf = JConf.create("config.json", ConfigPojo.class);
        out.println(jConf.get().name); // prints "some_value"

        // Update config
        jConf.get().name = "some_value_updated";
        jConf.save(); // save to file (this also reloads the file)
        out.println(jConf.get().name); // prints "some_value_updated", changes are reflected in config file

    }

    private static class ConfigPojo {
        String name;
    }

}
```

# Tips

- If your config needs default values, you can simply set them in the POJO. If your JSON file doesn't contain the field, then the value from the POJO stays, otherwise it's overwritten. Note: If you save that config again, then the default value will be written into the JSON file
- If you want GSON to always ignore fields (e.g. for default values), you can use `transient`.
- Your config file can contain methods. These will be ignored by GSON.
- If you have serializable object fields (e.g. `Pattern` fo regex), then you can create GSON with additional
  `TypeAdapters` (see `new GsonBuilder().registerTypeAdapter()`)
- If you need some fancy logic like `@Required` annotated fields then you need to implement your own Deserializer in GSON, see [here](https://stackoverflow.com/questions/21626690/gson-optional-and-required-fields).
