package in.deepaksood.popmov;

import android.app.Activity;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
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

import in.deepaksood.popmov.moviemodelpackage.MovieModel;
import in.deepaksood.popmov.adapterpackage.ReviewListAdapter;
import in.deepaksood.popmov.moviemodelpackage.ReviewModel;
import in.deepaksood.popmov.adapterpackage.TrailerListAdapter;
import in.deepaksood.popmov.moviemodelpackage.TrailerModel;

public class MovieDetailFragment extends Fragment {

    private static final String TAG = MovieDetailFragment.class.getSimpleName();
    private static final String api_key="ed3e485287a973b1d147b39aedff970b";
    private static final String BASE_COVER_URL = "http://image.tmdb.org/t/p/w780/";
    private static final String BASE_POSTER_URL = "http://image.tmdb.org/t/p/w500/";

    MovieModel movieModel;
    List<TrailerModel> trailerModelList = new ArrayList<>();
    List<ReviewModel> reviewModelList = new ArrayList<>();

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    ListView lvTrailers;
    ListView lvReviews;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        ImageView ivDetailMoviePoster = (ImageView) rootView.findViewById(R.id.iv_detail_movie_poster);

        TextView tvOriginalTitle = (TextView) rootView.findViewById(R.id.tv_original_title);
        TextView tvReleaseDate = (TextView) rootView.findViewById(R.id.tv_release_date);
        TextView tvRating = (TextView) rootView.findViewById(R.id.tv_rating);
        TextView tvLanguage = (TextView) rootView.findViewById(R.id.tv_language);
        TextView tvOverview = (TextView) rootView.findViewById(R.id.tv_overview);

        tvOriginalTitle.setText(movieModel.getOriginal_title());
        tvReleaseDate.setText(movieModel.getRelease_date());
        tvRating.setText("Rating: "+Float.toString(movieModel.getVote_average())+"/10");
        tvLanguage.setText("Original language: "+movieModel.getOriginal_language());
        tvOverview.setText("Overview: "+movieModel.getOverview());

        Picasso.with(getContext())
                .load(BASE_POSTER_URL +movieModel.getPoster_path())
                .placeholder(R.drawable.placeholder_movie_item_image)
                .error(R.drawable.placeholder_movie_item_image)
                .into(ivDetailMoviePoster);

        String urlTrailers = buildQuery(String.valueOf(movieModel.getId()),"videos");
        Log.v(TAG,"usl: "+urlTrailers);
        requestDataTrailers(urlTrailers);
        String urlReviews = buildQuery(String.valueOf(movieModel.getId()),"reviews");
        requestDataReviews(urlReviews);

        lvTrailers = (ListView) rootView.findViewById(R.id.lv_trailers);
        lvReviews = (ListView) rootView.findViewById(R.id.lv_reviews);

        return rootView;
    }


    public String buildQuery(String movieId, String query) {
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
        Log.v(TAG,"Request DAta for mobile");

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v(TAG,"response: "+response);
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
                        Log.v(TAG,"size: "+trailerModelList.size());
                        lvTrailers.setAdapter(new TrailerListAdapter(getContext(), trailerModelList));
                        setListViewHeightBasedOnChildren(lvTrailers);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v(TAG,"response: "+error);
            }
        });

        Volley.newRequestQueue(this.getActivity()).add(stringRequest);
    }


    public void requestDataReviews(String url) {
        Log.v(TAG,"Request DAta for mobile");

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v(TAG,"response: "+response);
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
                        Log.v(TAG,"size: "+reviewModelList.size());
                        lvReviews.setAdapter(new ReviewListAdapter(getContext(), reviewModelList));
                        setListViewHeightBasedOnChildren(lvReviews);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v(TAG,"response: "+error);
            }
        });

        Volley.newRequestQueue(this.getActivity()).add(stringRequest);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(
                listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;

        View view = null;

        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(
                        desiredWidth, CollapsingToolbarLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
