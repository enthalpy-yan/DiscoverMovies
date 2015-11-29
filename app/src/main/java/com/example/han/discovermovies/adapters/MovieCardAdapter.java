package com.example.han.discovermovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.han.discovermovies.R;
import com.example.han.discovermovies.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MovieCardAdapter extends RecyclerView.Adapter<MovieCardAdapter.ViewHolder> {
    private static final String LOG_TAG = MovieCardAdapter.class.getSimpleName();
    private List<Movie> mMovies;
    private ICardViewClicks cardViewClickListener;

    public MovieCardAdapter() {
        super();
        mMovies = new ArrayList<Movie>();
    }

    public void addData(List<Movie> movie) {
        mMovies.addAll(movie);
        notifyDataSetChanged();
    }

    public void clear() {
        mMovies.clear();
    }

    @Override
    public MovieCardAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movies_recycler_view, parent, false);
        ViewHolder viewholder = new ViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(MovieCardAdapter.ViewHolder holder, int position) {
        Movie movie = mMovies.get(position);
        holder.movieTitle.setText(movie.getTitle());
        holder.movieRating.setText(String.valueOf(movie.getVoteAverage()));
        holder.movieOverview.setText(movie.getOverview());
        holder.mMovie = movie;
        Context context = holder.moviePic.getContext();
        Picasso.with(context)
                .load(movie.getPosterPath())
                .into(holder.moviePic);
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView movieTitle;
        public ImageView moviePic;
        public TextView movieRating;
        public TextView movieOverview;
        public CardView movieCard;
        public Movie mMovie= null;

        public ViewHolder(View itemView) {
            super(itemView);
            movieCard = (CardView) itemView.findViewById(R.id.movie_card);
            movieTitle = (TextView) itemView.findViewById(R.id.movie_title);
            moviePic = (ImageView) itemView.findViewById(R.id.movie_picture);
            movieRating = (TextView) itemView.findViewById(R.id.movie_rating);
            movieOverview = (TextView) itemView.findViewById(R.id.movie_overview);
            movieCard.setClickable(true);
            movieCard.setOnClickListener(v -> {
                cardViewClickListener.onCardClick(mMovie);
            });
        }

    }

    public interface ICardViewClicks {
        public void onCardClick(Movie movie);
    }

    public void setOnItemListener(ICardViewClicks listener) {
        cardViewClickListener = listener;
    }

}
