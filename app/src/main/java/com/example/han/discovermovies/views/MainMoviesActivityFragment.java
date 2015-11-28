package com.example.han.discovermovies.views;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.han.discovermovies.R;
import com.example.han.discovermovies.adapters.MovieCardAdapter;
import com.example.han.discovermovies.models.DiscoverResponse;
import com.example.han.discovermovies.services.MovieService;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainMoviesActivityFragment extends Fragment {

    private MovieService mMovieService;
    private final String LOG_TAG = MainMoviesActivityFragment.class.getSimpleName();

    public MainMoviesActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_movies, container, false);
        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.movies_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mRecyclerView.setLayoutManager(mLayoutManager);
        final MovieCardAdapter mMovieCardAdapter = new MovieCardAdapter();
        mRecyclerView.setAdapter(mMovieCardAdapter);
        mMovieService = new MovieService();
        mMovieService.getApi()
                .getMovies()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DiscoverResponse>() {
                               @Override
                               public void onCompleted() {

                               }

                               @Override
                               public void onError(Throwable e) {
                                   Log.e(LOG_TAG, e.getMessage());
                               }

                               @Override
                               public void onNext(DiscoverResponse movies) {
                                   mMovieCardAdapter.addData(movies.getResults());
                               }
                           }
                );
        return rootView;
    }
}
