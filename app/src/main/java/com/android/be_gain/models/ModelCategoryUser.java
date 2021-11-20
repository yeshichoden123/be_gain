package com.android.be_gain.models;

public class ModelCategoryUser {

    // make sure to use same spellings for model variable as in firebase

    String id, category, uid;
    String timestamp;

    // constructor empty required for firebase
    public ModelCategoryUser() {

    }

    public ModelCategoryUser(String id, String category, String uid, String timestamp) {
        this.id = id;
        this.category = category;
        this.uid = uid;
        this.timestamp = timestamp;
    }

    /* -- Getter/ Setters --*/

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
