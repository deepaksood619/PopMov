package in.deepaksood.popmov;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.deepaksood.popmov.moviemodelpackage.MovieModel;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Movies. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MovieDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class MovieListActivity extends AppCompatActivity {

    private static final String TAG = MovieListActivity.class.getSimpleName();
    private static final String api_key="ed3e485287a973b1d147b39aedff970b";
    private List<MovieModel> movieModelList = new ArrayList<>();
    protected GridLayoutManager mGridLayoutManager;
    protected MoviesAdapter mMoviesAdapter;

    private boolean mTwoPane;

    View recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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

        populateList("popular");

    }

    private Menu menu;
    private MenuItem popularItem, topRatedItem;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        this.menu = menu;
        popularItem = menu.findItem(R.id.menu_popular);
        topRatedItem = menu.findItem(R.id.menu_top_rated);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_popular:
                if(!item.isChecked()) {
                    item.setChecked(true);
                    topRatedItem.setChecked(false);
                    populateList("popular");
                }
                break;

            case R.id.menu_top_rated:
                if(!item.isChecked()) {
                    item.setChecked(true);
                    popularItem.setChecked(false);
                    populateList("top_rated");
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void populateList(String string) {
        movieModelList.clear();
        String url = buildQuery(string);
        requestData(url);
    }

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

    public void requestData(String url) {
        Log.v(TAG,"Request DAta");

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.v(TAG,"response: "+response);
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
                        Log.v(TAG,"size: "+movieModelList.size());

                        setupRecyclerView((RecyclerView) recyclerView);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v(TAG,"response: "+error);
            }
        });

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setLayoutManager(mGridLayoutManager);
        recyclerView.setAdapter(new MoviesAdapter(movieModelList, mTwoPane, getSupportFragmentManager(), this));
    }
}
