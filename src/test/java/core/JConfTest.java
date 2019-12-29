package core;

import com.google.gson.Gson;
import exceptions.Exceptions.JConfCouldNotReadFileConfigException;
import exceptions.Exceptions.JConfFailedToMapJsonToObjectConfig;
import exceptions.Exceptions.JConfFailedToWriteConfigException;
import exceptions.Exceptions.JConfPathToJsonFileDoesNotExistConfigException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JConfTest {

    // ------------------------------------------------------------------------------------------ //
    // SET UP
    // ------------------------------------------------------------------------------------------ //

    private static final File noReadPermissionConfigFile = new File(getAbsolutePathToTestResources("no_read_permission.json"));
    private static final File saveTestConfigFile = new File(getAbsolutePathToTestResources("saveTestFile.json"));

    @BeforeClass
    public static void setUp() throws Exception {
        noReadPermissionConfigFile.setReadable(false);
        Files.write(Paths.get(getAbsolutePathToTestResources("saveTestFile.json")),
                createJsonForConfig().getBytes(),
                StandardOpenOption.CREATE);
    }

    @AfterClass
    public static void tearDown() {
        noReadPermissionConfigFile.setReadable(true);
        saveTestConfigFile.delete();
    }


    // ------------------------------------------------------------------------------------------ //
    // CREATE CONFIG OBJECT
    // ------------------------------------------------------------------------------------------ //

    @Test
    public void createWithStandardGson() throws Exception {
        final JConf<TestConfigModel> jConf = JConf.create(getClassLoaderResourcesPath("test_config_1.json"),
                TestConfigModel.class);
        final TestConfigModel config = jConf.get();
        assertThat(config.getName(), is("test_config"));
        assertThat(config.getDefaultValue(), is("default_value"));
    }

    @Test
    public void createWithCustomGson() throws Exception {
        final JConf<TestConfigModel> jConf = JConf.create(getClassLoaderResourcesPath("test_config_1.json"),
                TestConfigModel.class,
                new Gson());
        final TestConfigModel config = jConf.get();
        assertThat(config.getName(), is("test_config"));
        assertThat(config.getDefaultValue(), is("default_value"));
    }

    @Test(expected = JConfPathToJsonFileDoesNotExistConfigException.class)
    public void jsonFileDoesNotExist() throws Exception {
        JConf.create("DOES_NOT_EXIST",
                TestConfigModel.class);
    }

    @Test(expected = JConfFailedToMapJsonToObjectConfig.class)
    public void malformedJson() throws Exception {
        JConf.create(getClassLoaderResourcesPath("malformed.json"),
                TestConfigModel.class);
    }

    @Test(expected = JConfCouldNotReadFileConfigException.class)
    public void noReadPermission() throws Exception {
        JConf.create(noReadPermissionConfigFile.getPath(),
                TestConfigModel.class);
    }


    // ------------------------------------------------------------------------------------------ //
    // SAVE
    // ------------------------------------------------------------------------------------------ //

    @Test
    public void x1_save() throws Exception {
        final JConf<TestConfigModel> jConf = JConf.create(saveTestConfigFile.toString(),
                TestConfigModel.class);
        TestConfigModel config = jConf.get();
        assertThat(config.getName(), is("test_config"));

        config.setName("test_config_updated");
        jConf.save();
        assertThat(jConf.get().getName(), is("test_config_updated"));
    }

    @Test(expected = JConfFailedToWriteConfigException.class)
    public void x2_saveNoWritePermission() throws Exception {
        saveTestConfigFile.setWritable(false);

        final JConf<TestConfigModel> jConf = JConf.create(saveTestConfigFile.toString(),
                TestConfigModel.class);
        TestConfigModel config = jConf.get();
        assertThat(config.getName(), is("test_config_updated"));

        config.setName("test_config_updated_more");
        jConf.save();
    }


    // ------------------------------------------------------------------------------------------ //
    // HELPERS
    // ------------------------------------------------------------------------------------------ //

    private static String getClassLoaderResourcesPath(final String pathToResource) {
        final URL path = ClassLoader.getSystemResource(pathToResource);
        if (path == null) {
            throw new IllegalStateException("\n" +
                    "Can't find '" + pathToResource + "', check existence in /test/resources!\n" +
                    "Don't use the absolute path! Use: \"some/folder/someFile.csv\"");
        }
        return path.getPath();
    }

    private static String getAbsolutePathToTestResources(final String pathToResource) {
        return getClassLoaderResourcesPath("")
                .replace("/target/test-classes", "/src/test/java/resources/" + pathToResource);
    }

    private static String createJsonForConfig() {
        return "{\"name\": \"test_config\"}";
    }
}
