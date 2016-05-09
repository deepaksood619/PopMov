package in.deepaksood.popmov.adapterpackage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import in.deepaksood.popmov.master_detail_package.MovieDetailActivity;
import in.deepaksood.popmov.master_detail_package.MovieDetailFragment;
import in.deepaksood.popmov.master_detail_package.MovieListActivity;
import in.deepaksood.popmov.R;
import in.deepaksood.popmov.moviemodelpackage.MovieModel;

/**
 * Created by deepak on 8/5/16.
 */
public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    private static final String BASE_URL = "http://image.tmdb.org/t/p/w500/";

    List<MovieModel> movieModels;
    private boolean mTwoPane;
    FragmentManager fragmentManager;
    Context context;

    public MoviesAdapter(List<MovieModel> movieModels, boolean mTwoPane, FragmentManager fragmentManager, Context context) {
        this.movieModels = movieModels;
        this.mTwoPane = mTwoPane;
        this.fragmentManager = fragmentManager;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        public final View mView;
        public final ImageView ivMoviePoster;

        public ViewHolder(View view) {
            super(view);
            cv = (CardView)itemView.findViewById(R.id.cv);
            mView = view;
            ivMoviePoster = (ImageView) view.findViewById(R.id.iv_movie_poster);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Picasso.with(context).load(BASE_URL+movieModels.get(position).getPoster_path()).into(holder.ivMoviePoster);


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
//                    arguments.putString(MovieDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                    MovieDetailFragment fragment = new MovieDetailFragment();
                    fragment.setArguments(arguments);
                    MovieListActivity.lastLocationAccessed = position;
                    fragmentManager.beginTransaction()
                            .replace(R.id.movie_detail_container, fragment)
                            .commit();

                } else {
                    Context context = v.getContext();

                    Intent intent = new Intent(context, MovieDetailActivity.class);
                    intent.putExtra("MOVIE_OBJECT", movieModels.get(position));
                    MovieListActivity.lastLocationAccessed = position;
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieModels.size();
    }

}
