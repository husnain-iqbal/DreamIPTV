package com.live.dreamiptv.v1.Live;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.live.dreamiptv.v1.Live.ChannelCategoryList.LiveCategoryDetailFragment;
import com.live.dreamiptv.v1.Live.ChannelList.ChannelDetailFragment;
import com.live.dreamiptv.v1.Live.ChannelLive.ChannelLiveFragment;
import com.live.dreamiptv.v1.R;
import com.live.dreamiptv.v1.Utilities;

public class LiveMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_live_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_one_live, new LiveCategoryDetailFragment(), Utilities.FRAGMENT_ONE_TAG);
        fragmentTransaction.add(R.id.fragment_two_live, new ChannelDetailFragment(), Utilities.FRAGMENT_TWO_TAG);
        fragmentTransaction.add(R.id.fragment_three_live, new ChannelLiveFragment(), Utilities.FRAGMENT_THREE_TAG);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
//      this.finish();
        LiveUtils.setPreviousUrlStreaming("");
        super.onBackPressed();
    }
}
