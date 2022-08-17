package com.spapp.shadipedia.FireBase;

import android.net.Uri;

public class Providers {
    public final static String PROVIDER_KEY = "providerId";
    public final static String NAME_KEY = "name";
    public final static String EMAIL_KEY = "email";
    public final static String AGE_KEY = "age";
    public final static String MOBILE_KEY = "mobile";
    public final static String PASSWORD_KEY = "password";
    public final static String IMAGE_KEY = "image";

    private String providerId;
    private String Name;
    private String Email;
    private String Age;
    private String Mobile;
    private String Password;
    private String ImageValue;

    public Providers() {
    }

    public Providers(String providerId, String name, String email, String age, String mobile, String password, String imageValue) {
        this.providerId = providerId;
        this.Name = name;
        this.Email = email;
        this.Age = age;
        this.Mobile = mobile;
        this.Password = password;
        this.ImageValue = imageValue;
    }

    public static String getProviderKey() {
        return PROVIDER_KEY;
    }

    public static String getNameKey() {
        return NAME_KEY;
    }

    public static String getEmailKey() {
        return EMAIL_KEY;
    }

    public static String getAgeKey() {
        return AGE_KEY;
    }

    public static String getMobileKey() {
        return MOBILE_KEY;
    }

    public static String getPasswordKey() {
        return PASSWORD_KEY;
    }

    public static String getImageKey() {
        return IMAGE_KEY;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getImageValue() {
        return ImageValue;
    }

    public void setImageValue(String imageValue) {
        ImageValue = imageValue;
    }
}
