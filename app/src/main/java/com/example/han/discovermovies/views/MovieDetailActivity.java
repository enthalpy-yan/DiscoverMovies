package com.example.han.discovermovies.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.han.discovermovies.BuildConfig;
import com.example.han.discovermovies.R;
import com.example.han.discovermovies.models.Video;
import com.example.han.discovermovies.services.MovieService;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MovieDetailActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener,  YouTubePlayer.PlaybackEventListener  {

    private static final String LOG_TAG = MovieDetailActivity.class.getSimpleName();
    private static final String YOUTUBE_API_KEY = BuildConfig.YOUTUBE_API_KEY;
    private static final String VIDEO_ID = "ej3ioOneTy8";
    private String movieId;

    private YouTubePlayerSupportFragment mYouTubePlayerSupportFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getLayoutInflater().setFactory(this);
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            movieId = bundle.getString("MOVIE_ID");
        }
        setContentView(R.layout.activity_movie_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mYouTubePlayerSupportFragment = (YouTubePlayerSupportFragment) getSupportFragmentManager()
                .findFragmentById(R.id.youtubeplayerfragment);
        mYouTubePlayerSupportFragment.initialize(YOUTUBE_API_KEY, this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (movieId != null) {
            MovieService movieService = new MovieService();
            movieService.getApi().getVideo(movieId)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(videos -> {
                        if (videos != null) {
                            for (Video video : videos.getResults()) {
                                Log.d(LOG_TAG, video.getSite());
                                if (video.getSite().equals("YouTube")) {
                                    Log.d(LOG_TAG, video.getKey());
                                    youTubePlayer.cueVideo(video.getKey());
                                    youTubePlayer.setPlaybackEventListener(this);
                                    youTubePlayer.setPlayerStateChangeListener(new MyPlayerStateChangeListener());
                                }
                            }
                        }
                    });
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    @Override
    public void onBuffering(boolean b) {

    }

    @Override
    public void onPlaying() {
        Log.d("$$$$$$$$$$$$$$$$$$$$$$", "");
    }

    @Override
    public void onSeekTo(int i) {

    }

    @Override
    public void onStopped() {

    }

    @Override
    public void onPaused() {

    }

    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        private void updateLog(String prompt){
            Log.d("$$$$$$$$$$$$$$$$$", prompt);
        };

        @Override
        public void onAdStarted() {
            updateLog("onAdStarted()");
        }

        @Override
        public void onError(
                com.google.android.youtube.player.YouTubePlayer.ErrorReason arg0) {
            updateLog("onError(): " + arg0.toString());
        }

        @Override
        public void onLoaded(String arg0) {
            updateLog("onLoaded(): " + arg0);
        }

        @Override
        public void onLoading() {
            updateLog("onLoading()");
        }

        @Override
        public void onVideoEnded() {
            updateLog("onVideoEnded()");
        }

        @Override
        public void onVideoStarted() {
            updateLog("onVideoStarted()");
        }

    }
}
