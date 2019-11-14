package com.live.dreamiptv.v1.Live.ChannelCategoryList;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.live.dreamiptv.v1.Live.ChannelList.ChannelDetailFragment;
import com.live.dreamiptv.v1.Network.AppController;
import com.live.dreamiptv.v1.R;
import com.live.dreamiptv.v1.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by hp on 3/28/2017.
 */

public class LiveCategoryDetailFragment extends Fragment implements LiveCategoryBaseAdapter.SendData {
    private Context mContext;
    private Activity mActivity;
    private ListView mListView;
    private boolean isSetAdapter;
    private String mChannelCategoriesUrl;
    private LiveCategoryBaseAdapter mCategoryAdapter;
    private ArrayList<String> mCategoryIdList;
    private ArrayList<LiveCategoriesModel> mCategoryList;

    public String getChannelCategoriesUrl() {
        return mChannelCategoriesUrl;
    }

    public void setChannelCategoriesUrl(String url) {
        mChannelCategoriesUrl = url;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isSetAdapter = true;
        mActivity = getActivity();
        mContext = mActivity.getApplicationContext();
        mCategoryList = new ArrayList<>();
        mCategoryIdList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.live_category_detail_fragment, container, false);
        mListView = (ListView) view.findViewById(R.id.listLiveCategories);
        mCategoryAdapter = new LiveCategoryBaseAdapter(this, mActivity, mCategoryList);
        mListView.setItemsCanFocus(true);
        mListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fetchChannelCategories(getChannelCategoriesUrl());
                mCategoryAdapter.setSelectedCategoryIndex(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        mListView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    Utilities.setFocusedFragmentLiveId(Utilities.FRAGMENT_ONE_TAG);
//                }
//            }
//        });
        SharedPreferences pref = mContext.getSharedPreferences("MyPref", MODE_PRIVATE);
        String userName = pref.getString(Utilities.USERNAME_TEXT, "no value come");
        String passWord = pref.getString(Utilities.PASSWORD_TEXT, "no value come");
        Log.i("username", userName);
        Log.i("password", passWord);
        String liveCategoriesUrl = Utilities.URL + Utilities.USERNAME_TEXT + Utilities.EQUAL_SIGN + userName + Utilities.AND_SIGN + Utilities.PASSWORD_TEXT + Utilities.EQUAL_SIGN + passWord + Utilities.AND_SIGN + Utilities.URL_ACTION_LIVE_CATEGORIES;
        setChannelCategoriesUrl(liveCategoriesUrl);
        fetchChannelCategories(liveCategoriesUrl);
        return view;
    }

    private void fetchChannelCategories(String url) {
        JsonArrayRequest jsonObject = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        String categoryName = object.getString(Utilities.CATEGORY_NAME_TEXT);
                        final String categoryId = object.getString(Utilities.CATEGORY_ID_TEXT);
                        if (!mCategoryIdList.contains(categoryId)) {
                            mCategoryList.add(new LiveCategoriesModel(categoryName, categoryId));
                            mCategoryIdList.add(categoryId);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (isSetAdapter) {
                    isSetAdapter = false;
                    mListView.setAdapter(mCategoryAdapter);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Utilities.showShortToast(mContext, Utilities.ERROR_FAILED_TO_LOAD_CATEGORIES);
            }
        });
        AppController.getInstances().addToRequestQueue(jsonObject);
    }

    public void selectListViewFirstItem() {
        mListView.setSelection(0);
    }

    public void selectListViewLastItem(int position) {
        mListView.setSelection(position);
    }

    @Override
    public void sendCategoryId(String position) {
        ChannelDetailFragment fragment = (ChannelDetailFragment) getFragmentManager().findFragmentById(R.id.fragment_two_live);
        if (fragment != null) {
            fragment.setChannelCategoryId(position);
            fragment.populateListView();
        }
    }

    @Override
    public void sendKeyRotate2ChannelListFirstItemEvent() {
        ChannelDetailFragment fragment = (ChannelDetailFragment) getFragmentManager().findFragmentById(R.id.fragment_two_live);
        if (fragment != null) {
            fragment.selectListViewFirstItem();
        }
    }

    @Override
    public void sendKeyRotate2CategoryListFirstItemEvent() {
        selectListViewFirstItem();
    }

    @Override
    public void sendKeyRotate2CategoryListLastItemEvent(int lastItemPosition) {
        selectListViewLastItem(lastItemPosition);
    }
}
