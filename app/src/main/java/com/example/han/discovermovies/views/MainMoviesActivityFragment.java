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
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainMoviesActivityFragment extends Fragment {

    private final String LOG_TAG = MainMoviesActivityFragment.class.getSimpleName();
    private MovieService mMovieService;
    private Subscription subscription;
    private MovieCardAdapter movieCardAdapter;
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
        this.subscription = mMovieService.getApi()
                .getMovies("vote_average.desc")
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
                                   movieCardAdapter.addData(movies.getResults());
                               }
                           }
                );
        return rootView;
    }

    @Override
    public void onDestroy() {
        this.subscription.unsubscribe();
        super.onDestroy();
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
                if(firstVisibleItems != null && firstVisibleItems.length > 0) {
                    pastVisibleItems = firstVisibleItems[0];
                }

                if (viewLoading) {
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        viewLoading = false;
                        Log.d(LOG_TAG, "END OF ITEMS");
                    }
                }
            }
        });

        mRecyclerView.setLayoutManager(mLayoutManager);
        return mRecyclerView;
    }
}
