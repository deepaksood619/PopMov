package in.deepaksood.popmov.adapterpackage;

import android.content.Context;
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

//adapter for review listview
public class ReviewListAdapter extends BaseAdapter {

    private static final String TAG = ReviewListAdapter.class.getSimpleName();

    Context context;
    List<ReviewModel> reviewModels;
    private static LayoutInflater layoutInflater = null;

    public ReviewListAdapter(Context context, List<ReviewModel> reviewModels) {
        this.context = context;
        this.reviewModels = reviewModels;
        try {
            layoutInflater = (LayoutInflater)context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } catch (Exception e) {
            Log.e(TAG,"Exception e: "+e);
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
        TextView tvAuthorName;
        TextView tvReviewContent;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // This implementation is giving error :
        // java.lang.NullPointerException: Attempt to invoke virtual method 'void android.view.View.measure(int, int)' on a null object reference
        //at in.deepaksood.popmov.utilitypackage.Utility.setListViewHeightBasedOnChildren(Utility.java:25)

        // Therefore i had used the old method for inflating views. Will use it next time please consider this in this project.
        // Also the number of trailers is at max 7 in every movie.
        // also i was not able to make a onClickListener in rowView through ViewHolder pattern.

        // I have used two listview inside scrollview which is never encouraged because it may cause bad user experience.
        // But in this project i have used this pattern therefore to find the correct length of the whole layout i have used
        // utility.java which finds the total height of the layout without making listview scrollable hence it is shown to
        //user as a linear layout and not as a listview.

        /*LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Holder holder;

        if(convertView == null) {
            View rowView = inflater.inflate(R.layout.trailer_list_item, parent, false);
            holder = new Holder();
            holder.tvPlayTrailer = (TextView) rowView.findViewById(R.id.tv_play_trailer);
            rowView.setTag(holder);
        }
        else {
            holder = (Holder) convertView.getTag();
        }
        holder.tvPlayTrailer.setText(trailerModels.get(position).getName());
        return convertView;*/


        Holder holder = new Holder();
        View rowView = layoutInflater.inflate(R.layout.review_list_item, null);


        holder.tvAuthorName = (TextView) rowView.findViewById(R.id.tv_author_name);
        holder.tvReviewContent = (TextView) rowView.findViewById(R.id.tv_review_content);
        holder.tvAuthorName.setText(reviewModels.get(position).getAuthor());
        holder.tvReviewContent.setText(reviewModels.get(position).getContent());

        return rowView;
    }
}
