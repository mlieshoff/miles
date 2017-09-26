package org.mili.devtool;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Michael Lieshoff, 28.07.17
 */
public class DefaultConnector implements Connector {

    private final File directory;

    public DefaultConnector(File directory) {
        this.directory = directory;
    }

    public File getDirectory() {
        return directory;
    }

    @Override
    public List<Entry> search(String s) {
        List<Entry> list = new ArrayList<>();
        List<Entry> found = search(directory, new Query(s));
        if (CollectionUtils.isNotEmpty(found)) {
            list.addAll(found);
        }
        return list;
    }

    private List<Entry> search(File dir, Query query) {
        List<Entry> list = new ArrayList<>();
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                List<Entry> sub = search(file, query);
                if (CollectionUtils.isNotEmpty(sub)) {
                    list.addAll(sub);
                }
            } else {
                Entry entry = findContent(file, query);
                if (entry != null) {
                    list.add(entry);
                }
            }
        }
        return list;
    }

    private Entry findContent(File file, Query query) {
        try {
            List<String> lines = FileUtils.readLines(file, "UTF-8");
            String title = null;
            String description = null;
            Set<String> tags = new HashSet<>();
            StringBuilder content = new StringBuilder();
            for (int i = 0, n = lines.size(); i < n; i ++) {
                if (i == 0) {
                    title = lines.get(0);
                } else if (i == 1) {
                    description = lines.get(1);
                } else if (i == 2) {
                    String[] array = lines.get(2).split(",");
                    for (String tag : array) {
                        tags.add(tag.trim());
                    }
                } else {
                    content.append(lines.get(i));
                    content.append("\n");
                }
            }
            Entry entry = new Entry(title, description, tags, content.toString());
            if (query.match(entry)) {
                return entry;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
