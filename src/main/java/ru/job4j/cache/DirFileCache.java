package ru.job4j.cache;

import java.io.*;

public class DirFileCache extends AbstractCache<String, String> {

    private final String cachingDir;

    public DirFileCache(String cachingDir) {
        this.cachingDir = cachingDir;
    }

    @Override
    protected String load(String key) {
        String value = null;
        try (BufferedReader br = new BufferedReader(new FileReader(cachingDir + '\\' + key))) {
            value = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }

}
