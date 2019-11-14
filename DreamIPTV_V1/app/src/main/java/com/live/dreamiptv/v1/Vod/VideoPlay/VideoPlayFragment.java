package com.live.dreamiptv.v1.Vod.VideoPlay;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.live.dreamiptv.v1.R;
import com.live.dreamiptv.v1.Utilities;
import com.live.dreamiptv.v1.Vod.VodUtils;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by hp on 3/27/2017.
 */

public class VideoPlayFragment extends Fragment {
    private Activity mActivity;
    private Context mContext;
    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mVideoView;
    private RelativeLayout mIpTvLogoImage;
    private RelativeLayout mFragmentsContainer;
    private RelativeLayout mVideoViewContainerFragment;
    private int mSmallScreenVideoViewHeight;
    private long mCurrentVideoPosition;
    private ImageButton mPlayButton;
    private ImageButton mPauseButton;
    private ImageButton mFastForwardButton;
    private ImageButton mRewindButton;
    private static final String URL = "http://dreamiptv.dyndns.org:25461";

    private long getCurrentVideoPosition() {
        return mCurrentVideoPosition;
    }

    private void setCurrentVideoPosition(long position) {
        mCurrentVideoPosition = position;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mContext = mActivity.getApplicationContext();
        VodUtils.setUserOnLargeScreen(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.video_view_exoplayer_layout_android, container, false);
        mVideoView = (SimpleExoPlayerView) view.findViewById(R.id.video_view_exoplayer);
        mPlayButton = (ImageButton) view.findViewById(R.id.exo_play);
        mPauseButton = (ImageButton) view.findViewById(R.id.exo_pause);
        mRewindButton = (ImageButton) view.findViewById(R.id.exo_rew);
        mFastForwardButton = (ImageButton) view.findViewById(R.id.exo_ffwd);
        mIpTvLogoImage = (RelativeLayout) mActivity.findViewById(R.id.iptv_logo_image);
        mFragmentsContainer = (RelativeLayout) mActivity.findViewById(R.id.category_videos_fragments_container);
        mVideoViewContainerFragment = (RelativeLayout) mActivity.findViewById(R.id.fragment_three_vod);
        mSmallScreenVideoViewHeight = (int) mActivity.getResources().getDimension(R.dimen.video_view_small_screen_height);
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mExoPlayer != null) {
            mExoPlayer.release();
        }
    }

    public void attainUrl(String streamType, String streamId, String extension, boolean isVideoClicked) {
        SharedPreferences pref = mContext.getSharedPreferences("MyPref", MODE_PRIVATE);
        String userName = pref.getString(Utilities.USERNAME_TEXT, "no value come");
        String passWord = pref.getString(Utilities.PASSWORD_TEXT, "no value come");
        String link = URL + "/" + streamType + "/" + userName + "/" + passWord + "/" + streamId + "." + extension;
        if (isVideoClicked) { // if video-button is clicked to play it
            if (!VodUtils.getPreviousUrlStreaming().equals(link)) {
                setCurrentVideoPosition(0);
                playVideoWithExoPlayer(link);
            }
            if (!VodUtils.isUserOnLargeScreen()) {
                stretchVideoView();
            }
        } else { // if back-button pressed on large screen
            compressVideoView();
        }
    }

    private void playVideoWithExoPlayer(String link) {
        if (mExoPlayer != null) {
            mExoPlayer.release();
        }
        VodUtils.setPreviousUrlStreaming(link);
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
//                Log.d("Tag", b+"");
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.d("Tag", playbackState + "");
                switch (playbackState) {
                    case ExoPlayer.STATE_IDLE:
                        mExoPlayer.prepare(videoSource);
                        mExoPlayer.seekTo(getCurrentVideoPosition());
                        mExoPlayer.setPlayWhenReady(true);
                        break;
                    case ExoPlayer.STATE_BUFFERING:
                        setCurrentVideoPosition(mExoPlayer.getCurrentPosition());
                        break;
                    case ExoPlayer.STATE_READY:
                        setCurrentVideoPosition(mExoPlayer.getCurrentPosition());
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
                Log.d("@VideoPlayFragment", errorMsg);
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
        mVideoView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        mVideoView.bringToFront();
        mVideoView.requestFocus();
//        final Handler handler = new Handler();
//        final Runnable r = new Runnable() {
//            public void run() {
//                handler.postDelayed(this, 1000);
//                int exoPlayerState = mExoPlayer.getPlaybackState();
//                if (exoPlayerState == ExoPlayer.STATE_IDLE) {
//                    mExoPlayer.prepare(videoSource);
//                    mExoPlayer.seekTo(getCurrentVideoPosition());
//                    mExoPlayer.setPlayWhenReady(true);
//                    Log.e("@VideoPlayFragment", "State idle");
//                } else if (exoPlayerState == ExoPlayer.STATE_BUFFERING || exoPlayerState == ExoPlayer.STATE_READY) {
//                    setCurrentVideoPosition(mExoPlayer.getCurrentPosition());
//                } else if (exoPlayerState == ExoPlayer.STATE_ENDED) {
//                    Log.e("@VideoPlayFragment", "State ended");
//                }
//            }
//        };
//        handler.postDelayed(r, 1000);
    }

    public void performVideoPlayOrPause() {
        mVideoView.showController();
        if (mPlayButton.getVisibility() == View.VISIBLE) {
            mPlayButton.performClick();
            mPlayButton.requestFocus();
        } else {
            mPauseButton.performClick();
            mPauseButton.requestFocus();
        }
    }

    public void performVideoFastForward() {
        mVideoView.showController();
        mFastForwardButton.performClick();
        if (mPlayButton.getVisibility() == View.VISIBLE) {
            mPlayButton.requestFocus();
        } else {
            mPauseButton.requestFocus();
        }
    }

    public void performVideoRewind() {
        mVideoView.showController();
        mRewindButton.performClick();
        if (mPlayButton.getVisibility() == View.VISIBLE) {
            mPlayButton.requestFocus();
        } else {
            mPauseButton.requestFocus();
        }
    }

    public void compressVideoView() {
        if (mIpTvLogoImage.getVisibility() == View.GONE) {
            mIpTvLogoImage.setVisibility(View.VISIBLE);
        }
        if (mFragmentsContainer.getVisibility() == View.GONE) {
            mFragmentsContainer.setVisibility(View.VISIBLE);
        }
        mVideoViewContainerFragment.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, mSmallScreenVideoViewHeight));
        VodUtils.setUserOnLargeScreen(false);
    }

    private void stretchVideoView() {
        if (mIpTvLogoImage.getVisibility() == View.VISIBLE) {
            mIpTvLogoImage.setVisibility(View.GONE);
        }
        if (mFragmentsContainer.getVisibility() == View.VISIBLE) {
            mFragmentsContainer.setVisibility(View.GONE);
        }
        mVideoViewContainerFragment.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        VodUtils.setUserOnLargeScreen(true);
    }

    @Override
    public void onDestroy() {
        if (mExoPlayer != null) {
            mExoPlayer.release();
        }
        super.onDestroy();
    }
}