import io.pmem.pmemkv.Database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BasicExample {
    private static final String PMEM_ROOT_PATH = "/dev/shm";
    private static final int PMEM_SIZE = 1073741824 / 16;
    public static void main(String[] args) {
        String[] supportedEngines = new String [] {"cmap", "vsmap", "vcmap", "tree3", "stree"};
        for (String engine : supportedEngines) {
            System.out.println("Starting engine " + engine);
            try {
                runEngine(engine, generateConf(engine));
            } catch (Exception e) {
                System.out.println("Failed to test engine " + engine);
                System.out.println(e);
            }
        }
    }
    private static String generateConf(String engine) {
        boolean isVolatile = engine.equals("vsmap") || engine.equals("vcmap");
        Path fullpath = Paths.get(PMEM_ROOT_PATH, engine);
        StringBuilder confStr = new StringBuilder();
        confStr.append("{\"path\":\"").append(fullpath.toString())
                .append("\", \"size\":").append(PMEM_SIZE);
        if (!isVolatile) {
            confStr.append(", \"force_create\":1");
        }
        confStr.append("}");

        if (Files.exists(fullpath)) {
            if (!isVolatile) {
                try {
                    Files.delete(fullpath);
                    System.out.println("Successfully cleaned " + fullpath.toString());
                } catch (IOException ioe) {
                    System.out.println("Failed to clean " + fullpath.toString() + ioe.getMessage());
                }
            }
        } else if (isVolatile){
            try {
                Files.createDirectory(fullpath);
            } catch (IOException ioe) {
                System.out.println("Failed to create " + fullpath.toString() + ioe.getMessage());
            }
        }
        return confStr.toString();
    }
    private static void runEngine(String engine, String confStr) {
        System.out.println(String.format("Loading Engine %s @ %s", engine, confStr));
        Database db = new Database(engine, confStr);

        System.out.println("Putting new key");
        db.put("key1", "value1");
        assert db.countAll() == 1;

        System.out.println("Reading key back");
        assert db.get("key1").equals("value1");

        System.out.println("Iterating existing keys");
        db.put("key2", "value2");
        db.put("key3", "value3");
        db.getKeys((String k) -> System.out.println("  visited: " + k));

        System.out.println("Removing existing key");
        db.remove("key1");
        assert !db.exists("key1");

        System.out.println("Stopping engine");
        db.stop();
    }
}
