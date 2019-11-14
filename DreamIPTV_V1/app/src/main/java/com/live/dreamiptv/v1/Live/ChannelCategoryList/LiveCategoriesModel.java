package com.live.dreamiptv.v1.Live.ChannelCategoryList;

/**
 * Created by hp on 3/15/2017.
 */

public class LiveCategoriesModel {
    String catgeName;
    String id;

    public LiveCategoriesModel(String catgeName, String id) {
        this.catgeName = catgeName;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCatgeName() {
        return catgeName;
    }

    public void setCatgeName(String catgeName) {
        this.catgeName = catgeName;
    }
}
