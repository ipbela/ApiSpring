package com.example.springboot.models;

import java.util.List;

//model for message, content = message, type = user ou bot, options
public class MessageModel {
    private String content;
    private String type;
    private List<String> options;

    public MessageModel(String content, String type, List<String> options) {
        this.content = content;
        this.type = type;
        this.options = options;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

}
