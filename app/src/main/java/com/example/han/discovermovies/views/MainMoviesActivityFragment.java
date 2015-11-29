package com.example.han.discovermovies.views;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.han.discovermovies.R;
import com.example.han.discovermovies.adapters.MovieCardAdapter;
import com.example.han.discovermovies.services.MovieService;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainMoviesActivityFragment extends Fragment {

    private final String LOG_TAG = MainMoviesActivityFragment.class.getSimpleName();
    private MovieService mMovieService;
    private Subscription subscription;
    private MovieCardAdapter movieCardAdapter;
    private String orderBy = "original_title.asc";
    private int page = 1;
    private boolean viewLoading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount;

    public MainMoviesActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_movies, container, false);
        movieCardAdapter = new MovieCardAdapter();
        RecyclerView mRecyclerView = this.setRecyclerView(rootView);
        mRecyclerView.setAdapter(movieCardAdapter);
        this.mMovieService = new MovieService();
        this.subscription = this.getMovies(mMovieService, movieCardAdapter, orderBy, page);
        this.setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onDestroy() {
        this.subscription.unsubscribe();
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_movies, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        } else {
            if (id == R.id.action_sort_by_date) {
                orderBy = "release_date.desc";
            } else if (id == R.id.action_sort_by_popularity) {
                orderBy = "popularity.desc";
            } else if (id == R.id.action_sort_by_rating) {
                orderBy = "vote_average.desc";
            } else if (id == R.id.action_sort_by_title) {
                orderBy = "original_title.asc";
            }
            page = 1;
            movieCardAdapter.clear();
            getMovies(mMovieService, movieCardAdapter, orderBy, page);
        }

        return super.onOptionsItemSelected(item);
    }

    private RecyclerView setRecyclerView(View rootView) {
        RecyclerView mRecyclerView = (RecyclerView) rootView.findViewById(R.id.movies_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        final StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                int[] firstVisibleItems = null;
                firstVisibleItems = mLayoutManager.findFirstVisibleItemPositions(firstVisibleItems);
                if (firstVisibleItems != null && firstVisibleItems.length > 0) {
                    pastVisibleItems = firstVisibleItems[0];
                }

                if (page <= 10) {
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        page += 1;
                        getMovies(mMovieService, movieCardAdapter, orderBy, page);
                    }
                }
            }
        });

        mRecyclerView.setLayoutManager(mLayoutManager);
        return mRecyclerView;
    }

    private Subscription getMovies(MovieService movieService,
                                   final MovieCardAdapter movieCardAdapter,
                                   String orderBy,
                                   int page) {
        return movieService.getApi()
                .getMovies(orderBy, page)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(movies -> movieCardAdapter.addData(movies.getResults()));
    }
}
