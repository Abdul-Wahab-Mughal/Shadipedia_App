package com.spapp.shadipedia.FireBase;

public class Post_Item {

    private String num;
    private String Name;
    private String Title;
    private String Description;
    private String Mobile;
    private String Address;
    private String City;
    private String Category;
    private String price;
    private String providerId;

    Post_Item() {
    }

    public Post_Item(String num) {
        this.num = num;
    }

    public Post_Item(String num, String name, String title, String description, String mobile,
                     String address, String city, String category, String price, String providerId) {
        this.num = num;
        Name = name;
        Title = title;
        Description = description;
        Mobile = mobile;
        Address = address;
        City = city;
        Category = category;
        this.price = price;
        this.providerId = providerId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProviderId(String key) {
        return providerId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public void setProviderId() {
        this.providerId = providerId;
    }

    // Count of Post
    public String getNum(String key) {
        return num;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setNum() {
        this.num = num;
    }

}
