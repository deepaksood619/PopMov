package in.deepaksood.popmov.master_detail_package;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.deepaksood.popmov.R;
import in.deepaksood.popmov.adapterpackage.MoviesAdapter;
import in.deepaksood.popmov.moviemodelpackage.MovieModel;
import in.deepaksood.popmov.preferencemanagerpackage.PrefManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by deepak on 8/5/16.
 */

// Main Activity Entry point for the application

public class MovieListActivity extends AppCompatActivity {

    private static final String TAG = MovieListActivity.class.getSimpleName();
    private static String api_key="";

    // list of MovieModel for storing the details fetched from TheMovieDB
    public static List<MovieModel> movieModelList = new ArrayList<>();

    // layoutManger for grid view in recycler view using cardview
    protected GridLayoutManager mGridLayoutManager;

    // list of MovieModel that are set as favorites
    public static List<MovieModel> favList = new ArrayList<>();
    public static int lastLocationAccessed = 0;

    //Used for getting view of the activity for SnackBar.
    private CoordinatorLayout coordinatorLayout;

    // save state if the device is tablet or mobile
    //if tablet if will show a master/detail flow
    //if mobile it will show only a master list with onClick to a new detail activity.
    private boolean mTwoPane;

    //Recycler view for displaying list.
    private View recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        //Getting apiKey from local.properties
        try {
            ApplicationInfo applicationInfo = this.getPackageManager()
                    .getApplicationInfo(this.getPackageName(),
                            PackageManager.GET_META_DATA);
            Bundle bundle = applicationInfo.metaData;
            api_key = bundle.getString("api_key");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //initializing coordinator layout
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.cl_main);

        //using toolbar as default actionbar. Material Design specification
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setTitle(getTitle());

        //initializing gridLayoutmanager for number of column = 2
        mGridLayoutManager = new GridLayoutManager(this, 2);

        recyclerView = findViewById(R.id.movie_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.movie_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        //default view will show the list of popular movies fetched from TMDB
        populateList("popular");

    }

    //for checkbox MenuItem. Only one will be active at a time. (like radioCheckBox)
    private MenuItem popularItem, topRatedItem, favoritesItem;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        popularItem = menu.findItem(R.id.menu_popular);
        topRatedItem = menu.findItem(R.id.menu_top_rated);
        favoritesItem = menu.findItem(R.id.menu_favorites);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_popular:
                if(!item.isChecked()) {
                    item.setChecked(true);
                    topRatedItem.setChecked(false);
                    favoritesItem.setChecked(false);
                    populateList("popular");
                }
                break;

            case R.id.menu_top_rated:
                if(!item.isChecked()) {
                    item.setChecked(true);
                    popularItem.setChecked(false);
                    favoritesItem.setChecked(false);
                    populateList("top_rated");
                }
                break;

            case R.id.menu_favorites:
                if(!item.isChecked()) {
                    PrefManager prefManager = new PrefManager(this);
                    List<MovieModel> temp = prefManager.getMovieModel();

                    if(temp != null) {
                        movieModelList.clear();
                        movieModelList = temp;
                        setupRecyclerView((RecyclerView) recyclerView);
                        item.setChecked(true);
                        topRatedItem.setChecked(false);
                        popularItem.setChecked(false);
                    }
                    else {
                        Snackbar.make(coordinatorLayout, "No favorite movie found", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }

                }
        }
        return super.onOptionsItemSelected(item);
    }

    // This method will clear the movieModelList and populate it with new data fetched from TMDB
    public void populateList(String string) {
        movieModelList.clear();
        String url = buildQuery(string);
        requestData(url);
    }

    // Uses Uri builder to build query for fetching data from TMDB
    public String buildQuery(String urlQuery) {
        String url;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("api.themoviedb.org")
                .appendPath("3")
                .appendPath("movie")
                .appendPath(urlQuery)
                .appendQueryParameter("api_key",api_key);

        url = builder.build().toString();
        return url;
    }

    // uses volley for fetching data from TMDB
    //used gson for mapping from json to MovieModel object
    public void requestData(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        final Gson gson = new Gson();
                        MovieModel modelObject;
                        try {
                            JSONObject rootObject = new JSONObject(response);
                            JSONArray rootArray = rootObject.getJSONArray("results");

                            for (int i = 0; i< rootArray.length(); i++) {
                                JSONObject childObject = rootArray.getJSONObject(i);
                                modelObject = gson.fromJson(childObject.toString(), MovieModel.class);
                                movieModelList.add(modelObject);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        setupRecyclerView((RecyclerView) recyclerView);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"response: "+error);
            }
        });

        Volley.newRequestQueue(this).add(stringRequest);
    }

    // method for setting up the recycler view
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(mGridLayoutManager);
        recyclerView.setAdapter(new MoviesAdapter(movieModelList, mTwoPane, getSupportFragmentManager(), this));
    }

    // implementation for using double back press to exit.
    // stops unintentional exit from application, giving user the final exit point.
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if(doubleBackToExitPressedOnce) {
            super.onBackPressed();
        }
        else {
            this.doubleBackToExitPressedOnce = true;
            Snackbar.make(coordinatorLayout, "Please click BACK again to exit", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }
}
