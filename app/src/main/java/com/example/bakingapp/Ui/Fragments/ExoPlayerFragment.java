package com.example.bakingapp.Ui.Fragments;

import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.bakingapp.GsonResponse.RecipeGsonResponse;
import com.example.bakingapp.R;
import com.example.bakingapp.databinding.FragmentStepsBinding;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


import java.util.ArrayList;
import java.util.HashMap;

public class ExoPlayerFragment extends Fragment {
    private static final String BITMAP_KEY = "BitmapKey";
    private static final String POSITION_KEY = "PositionKey";
    private static final String VIDEO_URL_KEY = "VideoUrlKey";
    private static final String STEP_DESCRIPTION = "StepDescription";
    private SimpleExoPlayerView mPlayerView;
    private SimpleExoPlayer mPlayer;
    private LoadControl mLoadControl;
    private FragmentStepsBinding fragmentStepsBinding;
    private RecipeGsonResponse.StepsBean stepsItem;
    private ArrayList<RecipeGsonResponse.StepsBean> stepsList;
    private String videoURL, thumbnailURL;
    protected Bitmap bitmapG;
    private Long position;
    private boolean isStartPosition = true;
    private int saveId;

    public ExoPlayerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentStepsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_steps, container, false);
        mPlayerView = fragmentStepsBinding.playerView;
        stepsList = getArguments().getParcelableArrayList(RecipeFragment.STEPS_KEY);
        final int[] id = {getArguments().getInt(RecipeFragment.ID_KEY)};
        position = C.TIME_UNSET;

        fragmentStepsBinding.buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id[0]++;
                stopMedia();
                startLoading(id);
            }
        });
        fragmentStepsBinding.buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id[0]--;
                stopMedia();
                startLoading(id);
            }
        });
        isStartPosition = true;
        if (savedInstanceState != null) {
            position = savedInstanceState.getLong(POSITION_KEY);
            id[0] = savedInstanceState.getInt(RecipeFragment.ID_KEY);
            isStartPosition = false;
        }
        startLoading(id);
        exoPlayerFullScreen();
        return fragmentStepsBinding.getRoot();
    }

    private void exoPlayerFullScreen() {
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mPlayerView.getLayoutParams();
            params.width = params.MATCH_PARENT;
            params.height = params.MATCH_PARENT;
            params.setMargins(0, 0, 0, 0);
            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE);
            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
            }
            mPlayerView.setLayoutParams(params);
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        long playerPosition = C.TIME_UNSET;
        playerPosition = mPlayer.getCurrentPosition();

        // outState.putParcelable(BITMAP_KEY, bitmapG);
        outState.putLong(POSITION_KEY, playerPosition);
        outState.putInt(RecipeFragment.ID_KEY, saveId);

    }

    private void startLoading(int[] id) {
        stepsItem = stepsList.get(id[0]);
        String name = getArguments().getString(RecipeFragment.NAME_KEY) + " : " + "Step " + stepsItem.getId();
        getActivity().setTitle(name);
        fragmentStepsBinding.descriptionTv.setText(stepsItem.getDescription());

        // URL of the video to stream
        videoURL = stepsItem.getVideoURL();
        if (!videoURL.equals("")) {
            fragmentStepsBinding.frameLayoutExoPlayer.setVisibility(View.VISIBLE);
            fragmentStepsBinding.frameLayoutThumbnail.setVisibility(View.GONE);
            getPlayer();
        } else {
            fragmentStepsBinding.frameLayoutExoPlayer.setVisibility(View.GONE);
            thumbnailURL = stepsItem.getThumbnailURL();
            if (!thumbnailURL.equals("")) {
                fragmentStepsBinding.frameLayoutThumbnail.setVisibility(View.VISIBLE);
                if (fragmentStepsBinding.stepThumbnail.getDrawable() == null)
                    loadThumbnail();
            }
        }

        if (id[0] == 0) {
            fragmentStepsBinding.buttonPrevious.setVisibility(View.GONE);
            fragmentStepsBinding.buttonNext.setVisibility(View.VISIBLE);
        } else if (id[0] == stepsList.size() - 1) {
            fragmentStepsBinding.buttonPrevious.setVisibility(View.VISIBLE);
            fragmentStepsBinding.buttonNext.setVisibility(View.GONE);
        } else {
            fragmentStepsBinding.buttonPrevious.setVisibility(View.VISIBLE);
            fragmentStepsBinding.buttonNext.setVisibility(View.VISIBLE);
        }
        saveId = id[0];
    }

    private void loadThumbnail() {
        ThumbnailAsyncTask thumbnailAsyncTask = new ThumbnailAsyncTask();
        thumbnailAsyncTask.execute();
    }

    private void getPlayer() {

	/* A TrackSelector that selects tracks provided by the MediaSource to be consumed by each of the available Renderers.
	  A TrackSelector is injected when the player is created. */
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);
        mLoadControl = new DefaultLoadControl();
        // Create the player with previously created TrackSelector
        mPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, mLoadControl);

        // Load the default controller

        mPlayerView.requestFocus();

        // Load the SimpleExoPlayerView with the created player
        mPlayerView.setPlayer(mPlayer);

        // Measures bandwidth during playback. Can be null if not required.
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();

        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                getContext(),
                Util.getUserAgent(getContext(), "BakingApp"),
                defaultBandwidthMeter);

        // Produces Extractor instances for parsing the media data.
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource(
                Uri.parse(videoURL),
                dataSourceFactory,
                extractorsFactory,
                null,
                null);

        // Prepare the player with the source.
        if (!isStartPosition) {
            mPlayer.seekTo(position);
        }
        mPlayer.prepare(videoSource, isStartPosition, false);

        // Autoplay the video when the player is ready
        mPlayer.setPlayWhenReady(true);
        mPlayer.addListener(new ExoPlayer.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == ExoPlayer.STATE_BUFFERING) {
                    fragmentStepsBinding.pbExoLoading.setVisibility(View.VISIBLE);
                } else {
                    fragmentStepsBinding.pbExoLoading.setVisibility(View.GONE);
                }
                if (playbackState == ExoPlayer.STATE_READY) {
                    if (position > Long.valueOf(-1)) {
                        mPlayer.seekTo(position);
                        mPlayer.setPlayWhenReady(true);
                        position = Long.valueOf(-1);

                    }
                }

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPositionDiscontinuity() {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Release the player when it is not needed
        if (mPlayer != null)
            mPlayer.release();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (videoURL != null)
            getPlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopMedia();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopMedia();
    }

    private void stopMedia() {
        if (mPlayer != null) {
            position = mPlayer.getCurrentPosition();
            mPlayer.stop();
            mPlayer.release();
        }
    }

    private class ThumbnailAsyncTask extends AsyncTask<Void, Void, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            fragmentStepsBinding.pbThumbnailLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap = null;
            String videoPath = thumbnailURL;
            MediaMetadataRetriever mediaMetadataRetriever = null;
            try {
                mediaMetadataRetriever = new MediaMetadataRetriever();
                if (Build.VERSION.SDK_INT >= 14)
                    // no headers included
                    mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
                else
                    mediaMetadataRetriever.setDataSource(videoPath);
                //   mediaMetadataRetriever.setDataSource(videoPath);
                bitmap = mediaMetadataRetriever.getFrameAtTime();
            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                if (mediaMetadataRetriever != null)
                    mediaMetadataRetriever.release();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                fragmentStepsBinding.stepThumbnail.setImageBitmap(bitmap);
                bitmapG = bitmap;
            }
            fragmentStepsBinding.pbThumbnailLoading.setVisibility(View.GONE);
        }
    }


}