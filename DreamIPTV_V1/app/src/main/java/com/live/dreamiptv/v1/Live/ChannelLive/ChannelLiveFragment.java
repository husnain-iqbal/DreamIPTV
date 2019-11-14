package com.live.dreamiptv.v1.Live.ChannelLive;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.live.dreamiptv.v1.Live.LiveUtils;
import com.live.dreamiptv.v1.R;
import com.live.dreamiptv.v1.Utilities;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by hp on 3/27/2017.
 */
public class ChannelLiveFragment extends Fragment {
    private Activity mActivity;
    private Context mContext;
    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mVideoView;
    private int mSmallScreenVideoViewHeight;
    private RelativeLayout mIpTvLogoImage;
    private RelativeLayout mFragmentsContainer;
    private RelativeLayout mBrightCoveVideoViewContainerFragment;
    private static final String URL = "http://dreamiptv.dyndns.org:25461";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        LiveUtils.setUserPresentOnLargeScreen(false);
        mContext = mActivity.getApplicationContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.channel_view_exoplayer_layout_android, container, false);
        mVideoView = (SimpleExoPlayerView) view.findViewById(R.id.channel_view_exoplayer);
        mIpTvLogoImage = (RelativeLayout) mActivity.findViewById(R.id.iptv_logo_image);
        mFragmentsContainer = (RelativeLayout) mActivity.findViewById(R.id.category_channel_fragments_container);
        mBrightCoveVideoViewContainerFragment = (RelativeLayout) mActivity.findViewById(R.id.fragment_three_live);
        mSmallScreenVideoViewHeight = (int) mActivity.getResources().getDimension(R.dimen.video_view_small_screen_height);
        return view;
    }

    public void attainUrl(String streamType, String streamId) {
        SharedPreferences pref = mContext.getSharedPreferences("MyPref", MODE_PRIVATE);
        String userName = pref.getString(Utilities.USERNAME_TEXT, "no value come");
        String passWord = pref.getString(Utilities.PASSWORD_TEXT, "no value come");
        String link = URL + "/" + streamType + "/" + userName + "/" + passWord + "/" + streamId + ".ts"; //".ts"; // ".m3u8"
        if (LiveUtils.getPreviousUrlStreaming().equals(link)) {
            // Small screen - Large Screen Handling
            if (LiveUtils.isUserPresentOnLargeScreen()) {
                compressVideoView();
            } else {
                stretchVideoView();
            }
        } else {
            playVideo(link);
        }
    }

    private void playVideo(String link) {
        if (mExoPlayer != null) {
            mExoPlayer.release();
        }
        LiveUtils.setPreviousUrlStreaming(link);
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext, mActivity.getResources().getString(R.string.app_name)), bandwidthMeter);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        final MediaSource videoSource = new ExtractorMediaSource(Uri.parse(link), dataSourceFactory, extractorsFactory, null, null);
        mExoPlayer.prepare(videoSource, false, false); // change for resume the player after link goes down
        mExoPlayer.setPlayWhenReady(true);
        mExoPlayer.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object o) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {

            }

            @Override
            public void onLoadingChanged(boolean b) {
                Log.d("Tag", b+"");
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.d("Tag", playbackState + "");
                switch (playbackState) {
                    case ExoPlayer.STATE_IDLE:
                        mExoPlayer.prepare(videoSource);
                        mExoPlayer.setPlayWhenReady(true);
                        break;
                    case ExoPlayer.STATE_BUFFERING:
                        break;
                    case ExoPlayer.STATE_READY:
                        break;
                    case ExoPlayer.STATE_ENDED:
                        break;
                }
            }
            @Override
            public void onPlayerError(ExoPlaybackException e) {
                String errorMsg = e.getMessage();
                if(errorMsg  == null || errorMsg.isEmpty()){
                    errorMsg = "Error In Player While Playing";
                }
                Log.d("@ChannelLiveFragment", errorMsg);
            }

            @Override
            public void onPositionDiscontinuity() {
                Log.d("Tag", "Position Discontinuity");
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });
        mVideoView.setPlayer(mExoPlayer);
        mVideoView.requestFocus();
        mVideoView.bringToFront();
        mVideoView.setKeepScreenOn(true);
        mVideoView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        mVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (LiveUtils.isUserPresentOnLargeScreen()) {
                    compressVideoView();
                }
                return false;
            }
        });
    }

    public void compressVideoView() {
        if (mIpTvLogoImage.getVisibility() == View.GONE) {
            mIpTvLogoImage.setVisibility(View.VISIBLE);
        }
        if (mFragmentsContainer.getVisibility() == View.GONE) {
            mFragmentsContainer.setVisibility(View.VISIBLE);
        }
        mBrightCoveVideoViewContainerFragment.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, mSmallScreenVideoViewHeight));
        LiveUtils.setUserPresentOnLargeScreen(false);
    }

    private void stretchVideoView() {
        if (mIpTvLogoImage.getVisibility() == View.VISIBLE) {
            mIpTvLogoImage.setVisibility(View.GONE);
        }
        if (mFragmentsContainer.getVisibility() == View.VISIBLE) {
            mFragmentsContainer.setVisibility(View.GONE);
        }
        mBrightCoveVideoViewContainerFragment.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        LiveUtils.setUserPresentOnLargeScreen(true);
    }

    public void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.release();
        }
    }

    @Override
    public void onDestroy() {
        releasePlayer();
        super.onDestroy();
    }
}