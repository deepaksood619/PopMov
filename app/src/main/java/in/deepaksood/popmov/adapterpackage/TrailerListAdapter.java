package in.deepaksood.popmov.adapterpackage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
public class TrailerListAdapter extends BaseAdapter {

    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

    Context context;
    List<TrailerModel> trailerModels;
    private static LayoutInflater layoutInflater = null;

    public TrailerListAdapter(Context context, List<TrailerModel> trailerModels) {
        this.context = context;
        this.trailerModels = trailerModels;
        layoutInflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
