package com.example.han.discovermovies.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.han.discovermovies.R;
import com.example.han.discovermovies.adapters.MovieCardAdapter;
import com.example.han.discovermovies.models.Movie;
import com.example.han.discovermovies.services.MovieService;
import com.trello.rxlifecycle.components.RxFragment;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainMoviesActivityFragment extends RxFragment {

    private final String LOG_TAG = MainMoviesActivityFragment.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private MovieService mMovieService;
    private MovieCardAdapter movieCardAdapter;
    private Observable<List<Movie>> mMovieObservable;
    private String orderBy = "now_playing";
    private int page = 1;
    private boolean viewLoading = true;
    private int pastVisibleItems, visibleItemCount, totalItemCount;

    public MainMoviesActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
        mMovieService = new MovieService();
        mMovieObservable = this.getMovies(mMovieService, orderBy, page);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_movies, container, false);
        mRecyclerView = this.setRecyclerView(rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        movieCardAdapter = new MovieCardAdapter();
        mRecyclerView.setAdapter(movieCardAdapter);
        movieCardAdapter.setOnItemListener(movie -> {
            Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
            startActivity(intent);
        });
        mMovieObservable.subscribe(movies -> {
            movieCardAdapter.addData(movies);
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_movies, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_now_playing) {
            orderBy = "now_playing";
        } else if (id == R.id.action_popular) {
            orderBy = "popular";
        } else if (id == R.id.action_rating) {
            orderBy = "top_rated";
        } else if (id == R.id.action_upcoming) {
            orderBy = "upcoming";
        }
        page = 1;
        movieCardAdapter.clear();
        mMovieObservable = this.getMovies(mMovieService, orderBy, page);
        mMovieObservable.subscribe(movies -> {
            movieCardAdapter.addData(movies);
            mRecyclerView.scrollToPosition(0);
        });

        return super.onOptionsItemSelected(item);
    }

    private RecyclerView setRecyclerView(View rootView) {
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.movies_recycler_view);
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

                if (viewLoading) {
                    if (page <= 10) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            viewLoading = false;
                            page += 1;
                            mMovieObservable = getMovies(mMovieService, orderBy, page);
                            mMovieObservable.subscribe(movies -> {
                                viewLoading = true;
                                movieCardAdapter.clear();
                                Log.d(LOG_TAG, String.valueOf(movies.size()));
                                movieCardAdapter.addData(movies);
                            });
                        }
                    }
                }

            }
        });

        mRecyclerView.setLayoutManager(mLayoutManager);
        return mRecyclerView;
    }

    private Observable<List<Movie>> getMovies(MovieService movieService, String orderBy, int page) {
        return Observable.range(1, page)
                .concatMap(pageNum -> movieService.getApi().getMovies(orderBy, pageNum))
                .map(result -> result.getResults())
                .scan((List<Movie> results, List<Movie> result2) -> {
                    List<Movie> ret = new ArrayList<>();
                    ret.addAll(results);
                    ret.addAll(result2);
                    return ret;
                })
                .last()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .cache();
    }
}
