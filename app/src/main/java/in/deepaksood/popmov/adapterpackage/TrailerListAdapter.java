package in.deepaksood.popmov.adapterpackage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import in.deepaksood.popmov.R;
import in.deepaksood.popmov.moviemodelpackage.TrailerModel;

/**
 * Created by deepak on 8/5/16.
 */

//adapter for trailer listview
public class TrailerListAdapter extends BaseAdapter {

    //Endpoint for youtube view for trailers.
    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

    private static final String TAG = TrailerListAdapter.class.getSimpleName();

    Context context;
    List<TrailerModel> trailerModels;
    private static LayoutInflater layoutInflater = null;

    public TrailerListAdapter(Context context, List<TrailerModel> trailerModels) {
        this.context = context;
        this.trailerModels = trailerModels;
        try {
            layoutInflater = (LayoutInflater)context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } catch (Exception e) {
            Log.e(TAG,"Exception: "+e);
        }

    }

    @Override
    public int getCount() {
        return trailerModels.size();
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
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // the below implementation is giving error when ViewHolder pattern is used :
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

        View rowView = layoutInflater.inflate(R.layout.trailer_list_item, null);
        holder.tvPlayTrailer = (TextView) rowView.findViewById(R.id.tv_play_trailer);

        holder.tvPlayTrailer.setText(trailerModels.get(position).getName());
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_BASE_URL+trailerModels.get(position).getKey())));
            }
        });

        return rowView;


    }
}
