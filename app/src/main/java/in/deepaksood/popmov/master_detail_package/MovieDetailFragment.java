package in.deepaksood.popmov.master_detail_package;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.deepaksood.popmov.R;
import in.deepaksood.popmov.moviemodelpackage.MovieModel;
import in.deepaksood.popmov.adapterpackage.ReviewListAdapter;
import in.deepaksood.popmov.moviemodelpackage.ReviewModel;
import in.deepaksood.popmov.adapterpackage.TrailerListAdapter;
import in.deepaksood.popmov.moviemodelpackage.TrailerModel;
import in.deepaksood.popmov.preferencemanagerpackage.PrefManager;
import in.deepaksood.popmov.utilitypackage.Utility;

/**
 * Created by deepak on 8/5/16.
 */
public class MovieDetailFragment extends Fragment {

    private static final String TAG = MovieDetailFragment.class.getSimpleName();
    private static String api_key="";
    private static final String BASE_COVER_URL = "http://image.tmdb.org/t/p/w780/";
    private static final String BASE_POSTER_URL = "http://image.tmdb.org/t/p/w500/";

    private MovieModel movieModel;
    List<TrailerModel> trailerModelList = new ArrayList<>();
    private List<ReviewModel> reviewModelList = new ArrayList<>();

    ImageView ivSetFav;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Getting apiKey from local.properties
        try {
            ApplicationInfo applicationInfo = getActivity().getPackageManager()
                    .getApplicationInfo(getActivity().getPackageName(),
                            PackageManager.GET_META_DATA);
            Bundle bundle = applicationInfo.metaData;
            api_key = bundle.getString("api_key");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (getArguments().containsKey("MOVIE_OBJECT")) {

            movieModel = (MovieModel) getArguments().getSerializable("MOVIE_OBJECT");

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(movieModel.getTitle());
            }

            ImageView ivMovieCoverPicture = (ImageView) activity.findViewById(R.id.iv_movie_cover_poster);
            if(ivMovieCoverPicture != null)
            Picasso.with(activity)
                    .load(BASE_COVER_URL +movieModel.getBackdrop_path())
                    .placeholder(R.drawable.ph_cover_picture)
                    .error(R.drawable.ph_cover_picture)
                    .into(ivMovieCoverPicture);



        }
    }

    private ListView lvTrailers;
    ListView lvReviews;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        ivSetFav = (ImageView) rootView.findViewById(R.id.iv_set_fav);

        ImageView ivDetailMoviePoster = (ImageView) rootView.findViewById(R.id.iv_detail_movie_poster);

        TextView tvOriginalTitle = (TextView) rootView.findViewById(R.id.tv_original_title);
        TextView tvReleaseDate = (TextView) rootView.findViewById(R.id.tv_release_date);
        TextView tvRating = (TextView) rootView.findViewById(R.id.tv_rating);
        TextView tvLanguage = (TextView) rootView.findViewById(R.id.tv_language);
        TextView tvOverview = (TextView) rootView.findViewById(R.id.tv_overview);

        tvOriginalTitle.setText(movieModel.getOriginal_title());
        tvReleaseDate.setText(movieModel.getRelease_date());
        tvRating.setText(String.format("Rating: %s/10", Float.toString(movieModel.getVote_average())));
        tvLanguage.setText(String.format("Original language: %s", movieModel.getOriginal_language()));
        tvOverview.setText(String.format("Overview: %s", movieModel.getOverview()));

        Picasso.with(getContext())
                .load(BASE_POSTER_URL +movieModel.getPoster_path())
                .placeholder(R.drawable.placeholder_movie_item_image)
                .error(R.drawable.placeholder_movie_item_image)
                .into(ivDetailMoviePoster);

        String urlTrailers = buildQuery(String.valueOf(movieModel.getId()),"videos");
        requestDataTrailers(urlTrailers);
        String urlReviews = buildQuery(String.valueOf(movieModel.getId()),"reviews");
        requestDataReviews(urlReviews);

        lvTrailers = (ListView) rootView.findViewById(R.id.lv_trailers);
        lvReviews = (ListView) rootView.findViewById(R.id.lv_reviews);

        if(MovieListActivity.movieModelList.get(MovieListActivity.lastLocationAccessed).isFavorite())
            ivSetFav.setImageResource(R.drawable.ic_favorite_solid);
        else {
            ivSetFav.setImageResource(R.drawable.ic_favorite_border);
        }

        final PrefManager prefManager = new PrefManager(getContext());
        ivSetFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MovieListActivity.movieModelList.get(MovieListActivity.lastLocationAccessed).isFavorite()) {
                    ivSetFav.setImageResource(R.drawable.ic_favorite_solid);
                    movieModel.setFavorite(true);

                    MovieListActivity.favList.add(movieModel);
                    MovieListActivity.movieModelList.get(MovieListActivity.lastLocationAccessed).setFavorite(true);

                    prefManager.saveMovieModel(MovieListActivity.favList);

                }
                else {
                    ivSetFav.setImageResource(R.drawable.ic_favorite_border);
                    MovieListActivity.favList.remove(MovieListActivity.lastLocationAccessed);
                    prefManager.clearPref();
                    prefManager.saveMovieModel(MovieListActivity.favList);
                    MovieListActivity.movieModelList.get(MovieListActivity.lastLocationAccessed).setFavorite(false);
                    movieModel.setFavorite(false);
                }
            }
        });

        return rootView;
    }


    private String buildQuery(String movieId, String query) {
        String url;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(movieId)
                .appendPath(query)
                .appendQueryParameter("api_key",api_key);

        url = builder.build().toString();
        return url;
    }

    public void requestDataTrailers(String url) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        final Gson gson = new Gson();
                        TrailerModel modelObject;
                        try {
                            JSONObject rootObject = new JSONObject(response);
                            JSONArray rootArray = rootObject.getJSONArray("results");

                            for (int i = 0; i< rootArray.length(); i++) {
                                JSONObject childObject = rootArray.getJSONObject(i);
                                modelObject = gson.fromJson(childObject.toString(), TrailerModel.class);
                                trailerModelList.add(modelObject);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        lvTrailers.setAdapter(new TrailerListAdapter(getContext(), trailerModelList));
                        Utility.setListViewHeightBasedOnChildren(lvTrailers);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"response: "+error);
            }
        });

        Volley.newRequestQueue(this.getActivity()).add(stringRequest);
    }


    private void requestDataReviews(String url) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        final Gson gson = new Gson();
                        ReviewModel modelObject;
                        try {
                            JSONObject rootObject = new JSONObject(response);
                            JSONArray rootArray = rootObject.getJSONArray("results");

                            for (int i = 0; i< rootArray.length(); i++) {
                                JSONObject childObject = rootArray.getJSONObject(i);
                                modelObject = gson.fromJson(childObject.toString(), ReviewModel.class);
                                reviewModelList.add(modelObject);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        lvReviews.setAdapter(new ReviewListAdapter(getContext(), reviewModelList));
                        Utility.setListViewHeightBasedOnChildren(lvReviews);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"response: "+error);
            }
        });

        Volley.newRequestQueue(this.getActivity()).add(stringRequest);
    }
}
