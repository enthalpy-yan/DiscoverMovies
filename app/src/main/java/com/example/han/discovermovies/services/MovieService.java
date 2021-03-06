package com.example.han.discovermovies.services;

import com.example.han.discovermovies.BuildConfig;
import com.example.han.discovermovies.models.DiscoverResponse;
import com.example.han.discovermovies.models.VideoResponse;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public class MovieService {
    private static final String BASE_URL = "http://api.themoviedb.org";
    //Replace with your MovieDB api key.
    private static final String API_KEY = BuildConfig.MOVIE_DB_API_KEY;
    private final MovieApi mMovieApi;

    public MovieService() {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("User-Agent", "DiscoverMovieApp")
                        .addHeader("Accept", "Application/json")
                        .build();
                HttpUrl url = newRequest.httpUrl().newBuilder()
                        .addQueryParameter("api_key", API_KEY)
                        .build();
                newRequest = newRequest.newBuilder().url(url).build();
                return chain.proceed(newRequest);
            }
        };

        HttpLoggingInterceptor httpInterceptor = new HttpLoggingInterceptor();
        httpInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(interceptor);
        client.interceptors().add(httpInterceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        mMovieApi = retrofit.create(MovieApi.class);
    }

    public MovieApi getApi() {
        return mMovieApi;
    }

    public interface MovieApi {
        @GET("3/movie/{sort_by}")
        Observable<DiscoverResponse> getMovies(@Path("sort_by") String sortBy, @Query("page") int page);

        @GET("3/movie/{movie_id}/videos")
        Observable<VideoResponse> getVideo(@Path("movie_id") String movieId);
    }
}
