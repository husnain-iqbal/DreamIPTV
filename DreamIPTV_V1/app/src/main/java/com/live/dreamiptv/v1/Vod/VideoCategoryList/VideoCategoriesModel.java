package com.live.dreamiptv.v1.Vod.VideoCategoryList;

/**
 * Created by hp on 3/15/2017.
 */

public class VideoCategoriesModel {
    String mCategoryId;
    String mCategoryName;

    public VideoCategoriesModel(String categoryName, String categoryId) {
        mCategoryId = categoryId;
        mCategoryName = categoryName;
    }

    public String getCategoryId() {
        return mCategoryId;
    }

    public void setCategoryId(String categoryId) {
        mCategoryId = categoryId;
    }

    public String getCategoryName() {
        return mCategoryName;
    }

    public void setCategoryName(String categoryName) {
        mCategoryName = categoryName;
    }
}
