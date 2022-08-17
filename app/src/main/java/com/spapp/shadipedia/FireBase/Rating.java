package com.spapp.shadipedia.FireBase;

public class Rating {

    String id;
    String name;
    String comment;
    String rating;
    String star;
    String num;

    public Rating() {
    }

    public Rating(String star, String num) {
        this.star = star;
        this.num = num;
    }

    public Rating(String id, String name, String comment, String rating) {
        this.id = id;
        this.name = name;
        this.comment = comment;
        this.rating = rating;
    }

    public Rating(String name, String comment, String rating) {
        this.name = name;
        this.comment = comment;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
