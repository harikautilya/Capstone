package com.example.kautilya.application;

import android.support.annotation.Nullable;

public class Place {

    String name;
    long id;
    String location;
    String image_url;

    public Place() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        super.equals(obj);
        if (obj instanceof Place) {
            Place currentObjet = (Place) obj;
            if (currentObjet.getId() == id && name.equals(currentObjet.getName()) && avaiableTokens == currentObjet.getAvaiableTokens() && location.equals(currentObjet.getLocation()) && image_url.equals(currentObjet.getImage_url())) {
                return true;

            }
        }

        return false;

    }


}
