package examples;

import io.pmem.pmemkv.Database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BasicExample {
    private static final String PMEM_ROOT_PATH = "/mnt/mem";
    private static final int PMEM_SIZE = 1073741824 / 16;
    public static void main(String[] args) {
        String[] supportedEngines = new String [] {"cmap", "vsmap", "vcmap", "stree", "csmap"};
        for (String engine : supportedEngines) {
            System.out.println("Starting engine " + engine);
            try {
                runEngine(engine, generateConf(engine));
                System.out.println("Successfully tested engine " + engine);
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

        // for stree only:  range query and iterator
        if (engine.equals("stree") || engine.equals("csmap")) {
            final List<String> keys = new ArrayList<>();
            final List<String> values = new ArrayList<>();
            assert db.countAbove("key1") == 2;
            db.get_above("key1", (k, v) -> {keys.add(k);values.add(v);});
            assert keys.size() == 2;
            assert values.size() == 2;
            assert keys.get(0).equals("key2");
            assert keys.get(1).equals("key3");
            assert values.get(0).equals("value2");
            assert values.get(1).equals("value3");

            keys.clear();
            values.clear();
            assert db.countEqualAbove("key1") == 3;
            db.get_equal_above("key1", (k, v) -> {keys.add(k);values.add(v);});
            assert keys.size() == 3;
            assert values.size() == 3;
            assert keys.get(0).equals("key1");
            assert keys.get(1).equals("key2");
            assert keys.get(2).equals("key3");
            assert values.get(0).equals("value1");
            assert values.get(1).equals("value2");
            assert values.get(2).equals("value3");

            keys.clear();
            values.clear();
            assert db.countBelow("key2") == 1;
            db.get_below("key2", (k, v) -> {keys.add(k);values.add(v);});
            assert keys.size() == 1;
            assert values.size() == 1;
            assert keys.get(0).equals("key1");
            assert values.get(0).equals("value1");

            keys.clear();
            values.clear();
            assert db.countEqualBelow("key2") == 2;
            db.get_equal_below("key2", (k, v) -> {keys.add(k);values.add(v);});
            assert keys.size() == 2;
            assert values.size() == 2;
            assert keys.get(0).equals("key1");
            assert keys.get(1).equals("key2");
            assert values.get(0).equals("value1");
            assert values.get(1).equals("value2");

            keys.clear();
            values.clear();
	    // a bit tricky, lower_bound
            db.get_between("key1", "key3", (k, v) -> {keys.add(k);values.add(v);
            System.out.println("get_between  visited key: " + k + "visited value:"+ v );});

            if (engine.equals("stree")) {
                assert db.countBetween("key1", "key3") == 2;
            }  else {
                assert db.countBetween("key1", "key3") == 1;
            }


            try (Database.BytesIterator it = db.iterator()) {
                while (it.isValid()) {
                    byte[] key = it.key();
                    byte[] value = it.value();
                    System.out.println(String.format("Iterating [%s:%s] ", new String(key), new String(value)));
                    it.next();
                }

                it.seek("key1".getBytes());
                System.out.println(String.format("seek to key1 : %s:%s  ", new String(it.key()), new String(it.value())));
                it.seek("key2".getBytes());
                System.out.println(String.format("seek to key2 : %s:%s  ", new String(it.key()), new String(it.value())));
                it.seek("key3".getBytes());
                System.out.println(String.format("seek to key3 : %s:%s  ", new String(it.key()), new String(it.value())));

                it.seekToFirst();
                System.out.println(String.format("seek to first : %s:%s  ", new String(it.key()), new String(it.value())));
                it.seekToLast();
                System.out.println(String.format("seek to last : %s:%s  ", new String(it.key()), new String(it.value())));

                it.seekForPrev("key1".getBytes());
                System.out.println(String.format("seek to key1's prev : %s:%s  ", new String(it.key()), new String(it.value())));
                it.seekForPrev("key2".getBytes());
                System.out.println(String.format("seek to key2's prev : %s:%s  ", new String(it.key()), new String(it.value())));
                it.seekForPrev("key3".getBytes());
                System.out.println(String.format("seek to key3's prev : %s:%s  ", new String(it.key()), new String(it.value())));

                it.prev();
                System.out.println(String.format("seek to key2's prev : %s:%s  ", new String(it.key()), new String(it.value())));
                it.prev();
                System.out.println(String.format("seek to key1's prev : %s:%s  ", new String(it.key()), new String(it.value())));

                it.seekForNext("key1".getBytes());
                System.out.println(String.format("seek to key1's next : %s:%s  ", new String(it.key()), new String(it.value())));
                it.seekForNext("key2".getBytes());
                System.out.println(String.format("seek to key2's next : %s:%s  ", new String(it.key()), new String(it.value())));
                it.seekForNext("key3".getBytes());
                System.out.println(String.format("seek to key3's next : %s:%s  ", new String(it.key()), new String(it.value())));
            } finally {
                System.out.println("Done iterator!");
            }
        }

        System.out.println("Removing existing key");
        db.remove("key1");
        assert !db.exists("key1");

        System.out.println("Stopping engine");
        db.stop();
    }
}
