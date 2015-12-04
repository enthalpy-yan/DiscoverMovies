package com.example.han.discovermovies.adapters;

import android.content.Context;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MovieCardAdapter extends RecyclerView.Adapter<MovieCardAdapter.ViewHolder> {
    private static final Map<Integer, String> genreMap = new HashMap<Integer, String>();
    static {
        genreMap.put(9648,"Mystery");
        genreMap.put(80, "Crime");
        genreMap.put(28, "Action");
        genreMap.put(53, "Thriller");
        genreMap.put(37, "Western");
        genreMap.put(27, "Horror");
        genreMap.put(36, "History");
        genreMap.put(10402, "Music");
        genreMap.put(18,"Drama");
        genreMap.put(10752, "War");
        genreMap.put(99, "Documentary");
        genreMap.put(35, "Comedy");
        genreMap.put(12, "Adventure");
        genreMap.put(10769, "Foreign");
        genreMap.put(14, "Fantasy");
        genreMap.put(10770, "TV Movie");
        genreMap.put(878, "Science Fiction");
        genreMap.put(16, "Animation");
        genreMap.put(10751, "Family");
        genreMap.put(10749, "Romance");
    }
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
        List<String> genreList = movie.getGenreIds(genreMap);
        holder.movieGenres.setText(android.text.TextUtils.join(", ", genreList));
        holder.movieReleaseDate.setText(movie.getReleaseDate());
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
        public TextView movieGenres;
        public CardView movieCard;
        public TextView movieReleaseDate;
        public Movie mMovie= null;

        public ViewHolder(View itemView) {
            super(itemView);
            movieCard = (CardView) itemView.findViewById(R.id.movie_card);
            movieTitle = (TextView) itemView.findViewById(R.id.movie_title);
            moviePic = (ImageView) itemView.findViewById(R.id.movie_picture);
            movieRating = (TextView) itemView.findViewById(R.id.movie_rating);
            movieGenres = (TextView) itemView.findViewById(R.id.movie_genres);
            movieReleaseDate = (TextView) itemView.findViewById(R.id.movie_release_date);
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
