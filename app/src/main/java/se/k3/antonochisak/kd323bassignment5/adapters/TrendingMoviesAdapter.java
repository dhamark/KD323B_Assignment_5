package se.k3.antonochisak.kd323bassignment5.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import se.k3.antonochisak.kd323bassignment5.R;
import se.k3.antonochisak.kd323bassignment5.helpers.StaticHelpers;
import se.k3.antonochisak.kd323bassignment5.models.movie.Movie;

/**
 * Created by David on 15-04-29.
 */
public class TrendingMoviesAdapter extends BaseAdapter {

    private int screenWidth;
    private static int mItemWidth;
    private static int mItemHeight;
    private static int mMargin;
    ArrayList<Movie> mMovies;
    LayoutInflater mLayoutInflater;


    public TrendingMoviesAdapter(ArrayList<Movie> mMovies, LayoutInflater mLayoutInflater) {
        this.mMovies = mMovies;
        this.mLayoutInflater = mLayoutInflater;
    }

    // Viewholder for posters
    static class ViewHolder {
        @InjectView(R.id.iw_poster)
        ImageView poster;

        @InjectView(R.id.tw_title)
        TextView mTitle;

        @InjectView(R.id.tw_tagLine)
        TextView mTag;

        @InjectView(R.id.tw_year)
        TextView mYear;

       // @InjectView(R.id.tw_votes)
        //TextView mLikes;

        //@InjectView(R.id.tw_runtime)
        //TextView mRuntime;

        //@InjectView(R.id.tw_rating)
        //TextView mRating;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            int screenWidth = StaticHelpers.getScreenWidth(view.getContext());
            mItemWidth = (screenWidth / 2);
            mItemHeight = (int) ((double) mItemWidth / 0.677);
            mMargin = StaticHelpers.getPixelsFromDp(view.getContext(), 2);
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = mLayoutInflater.inflate(R.layout.trending_list_item, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // Loading images with piacsso for list items.
        Log.i("TendingMoviesAdapter", "list item added!");
        Picasso.with(view.getContext())
                .load(mMovies.get(i).getPoster())
                .resize(mItemWidth, mItemHeight)
                .into(holder.poster);


        // Setting movie title in textview
        holder.mTitle.setText(mMovies.get(i).getTitle());

        // Setting movie tagline in textview
        holder.mTag.setText(mMovies.get(i).getTagline());

        //Converting movie year int to string and setting it on textview.
        String movieYear = String.valueOf(mMovies.get(i).getYear());
        holder.mYear.setText(movieYear);

        // Setting movie no. of likes in textview
       // String movieLikes = String.valueOf(mMovies.get(i).getLikes());
        //holder.mLikes.setText(movieLikes);

        // Setting runtime in textview
        //holder.mRuntime.setText(mMovies.get(i).getRuntime() + " min");

        // Setting scaletype for poster to center crop
        holder.poster.setScaleType(ImageView.ScaleType.CENTER_CROP);

        // Fetch movie rating, remove all decimals including dot or comma using "replaceAll".
        // Store the string in a new string and set it to TextView.
        //String movieRating = mMovies.get(i).getRating().replaceAll("\\..*", "");
       // holder.mRating.setText(movieRating + " out of 10");

        return view;
    }



    @Override
    public int getCount() {
        return mMovies.size();
    }

    @Override
    public Object getItem(int i) {
        return mMovies.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
}
