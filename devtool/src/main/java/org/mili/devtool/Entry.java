package org.mili.devtool;

import java.util.Set;

public class Entry {

    private String title;
    private String description;
    private Set<String> tags;
    private String content;

    public Entry(String title, String description, Set<String> tags, String content) {
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Set<String> getTags() {
        return tags;
    }

    public String getContent() {
        return content;
    }

}
