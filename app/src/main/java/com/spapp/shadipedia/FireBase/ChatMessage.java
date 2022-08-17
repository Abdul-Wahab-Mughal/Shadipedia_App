package com.spapp.shadipedia.FireBase;

public class ChatMessage {
    String id;
    String num;
    String image;
    String name;
    String lastMessage;

    public ChatMessage() {
    }

    public ChatMessage(String name, String id, String num) {
        this.id = id;
        this.num = num;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getId(String key) {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

}
