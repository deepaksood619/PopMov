package in.deepaksood.popmov.adapterpackage;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import in.deepaksood.popmov.R;
import in.deepaksood.popmov.moviemodelpackage.ReviewModel;

/**
 * Created by deepak on 8/5/16.
 */
public class ReviewListAdapter extends BaseAdapter {

    private static final String TAG = ReviewListAdapter.class.getSimpleName();

    Context context;
    List<ReviewModel> reviewModels;
    private static LayoutInflater layoutInflater = null;

    public ReviewListAdapter(Context context, List<ReviewModel> reviewModels) {
        this.context = context;
        this.reviewModels = reviewModels;
        Log.v(TAG,"size1: "+this.reviewModels.size());
        Log.v(TAG,"size2: "+reviewModels.size());
        try {
            layoutInflater = (LayoutInflater)context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } catch (Exception e) {
            Log.v(TAG,"Exception e: "+e);
        }

    }

    @Override
    public int getCount() {
        return reviewModels.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView tvPlayTrailer;
        TextView tvReviewContent;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView = layoutInflater.inflate(R.layout.review_list_item, null);

        holder.tvPlayTrailer = (TextView) rowView.findViewById(R.id.tv_play_trailer);
        holder.tvReviewContent = (TextView) rowView.findViewById(R.id.tv_review_content);
        holder.tvPlayTrailer.setText(reviewModels.get(position).getAuthor());
        holder.tvReviewContent.setText(reviewModels.get(position).getContent());

        return rowView;
    }
}
