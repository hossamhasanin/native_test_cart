package com.hossam.hasanin.test_cart.models;

public class UserChat {
    private Integer id;
    private String name;
    private String userImage;

    public UserChat(Integer id, String name, String userImage) {
        this.id = id;
        this.name = name;
        this.userImage = userImage;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String IMAGE = "userImage";
}
