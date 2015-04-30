package se.k3.antonochisak.kd323bassignment5.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import se.k3.antonochisak.kd323bassignment5.R;
import se.k3.antonochisak.kd323bassignment5.adapters.TrendingMoviesAdapter;
import se.k3.antonochisak.kd323bassignment5.api.RestClient;
import se.k3.antonochisak.kd323bassignment5.api.model.ApiResponse;
import se.k3.antonochisak.kd323bassignment5.api.model.RootApiResponse;
import se.k3.antonochisak.kd323bassignment5.models.movie.Movie;


import static se.k3.antonochisak.kd323bassignment5.helpers.StaticHelpers.FIREBASE_CHILD;
import static se.k3.antonochisak.kd323bassignment5.helpers.StaticHelpers.FIREBASE_URL;

public class myFragment extends MoviesFragment implements AdapterView.OnItemClickListener{



    // Creating an arraylist of movies
    ArrayList<Movie> mMovies;

    // FireBase stuff
    HashMap<String, Object> mMovieMap;

    RestClient mRestClient;
    Firebase mFireBase;
    Firebase mRef;

    String mCurrentClickedMovie = "";
    CountDownTimer mVoteTimer;
    boolean mIsVoteTimerRunning = false;

    // Out adapter for trending movieslist.
    TrendingMoviesAdapter mAdapter;

    @InjectView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @InjectView(R.id.list_item)
    ListView mMoviesList;//skriv in namnet på list eller grid


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMovies = new ArrayList<>();
        mMovieMap = new HashMap<>();

        mRestClient = new RestClient();
        mFireBase = new Firebase(FIREBASE_URL);
        mRef = mFireBase.child(FIREBASE_CHILD);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_trending_movies, container, false); //Är det rätt?
    mMoviesList = (ListView) view.findViewById(R.id.listView);
        // Inject view
        //ButterKnife.inject(this, view);

        // Creating the tending movies adapter.
        mAdapter = new TrendingMoviesAdapter(mMovies, getActivity().getLayoutInflater());
        mMoviesList.setAdapter(mAdapter); //moviesLIst ska läggas in sedan

        // OnClick Listener for the items in the movies list listview.
        mMoviesList.setOnItemClickListener(this);


        return view;


    }



    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRestClient.getApiService().getTrending("images", this);
        //mProgressBar.setVisibility(View.VISIBLE);
        initVoteTimer();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (!mIsVoteTimerRunning) {
            voteOnMovie(i);
            mVoteTimer.start();
            mIsVoteTimerRunning = true;
        }
    }

    void initVoteTimer() {
        // So that there can only be one vote per every 3 seconds
        mVoteTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                mIsVoteTimerRunning = false;
            }
        };
    }

    void voteOnMovie(final int i) {
        Movie movie = mMovies.get(i);

        // Very important
        mCurrentClickedMovie = movie.getSlugline();

        mMovieMap.put("title", movie.getTitle());
        mMovieMap.put("year", movie.getYear());
        mMovieMap.put("slugline", movie.getSlugline());
        mMovieMap.put("poster", movie.getPoster());
        mMovieMap.put("fanart", movie.getFanArt());

        mRef.child(mCurrentClickedMovie).updateChildren(mMovieMap, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                Toast.makeText(getActivity(), "Gillade " + mMovies.get(i).getTitle(), Toast.LENGTH_SHORT).show();
                updateVotes();
            }
        });
    }

    void updateVotes() {
        mRef.child(mCurrentClickedMovie + "/votes").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue() == null) {
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue((Long) mutableData.getValue() + 1);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    @Override
    public void success(List<RootApiResponse> rootApiResponses, Response response) {
        //mProgressBar.setVisibility(View.GONE);
        for (RootApiResponse r : rootApiResponses) {

            // Build a new movie-object for every response and add to list
            Movie movie = new Movie.Builder()
                    .title(r.apiResponse.title)
                    .slugLine(r.apiResponse.ids.getSlug())
                    .poster(r.apiResponse.image.getPoster().getMediumPoster())
                    .fanArt(r.apiResponse.image.getFanArt().getFullFanArt())
                    .year(r.apiResponse.year)
                    .build();

            mMovies.add(movie);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void failure(RetrofitError error) {
        if(error.getKind() == RetrofitError.Kind.NETWORK) {
            Toast.makeText(getActivity(),
                    getResources().getString(R.string.retrofit_network_error),
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }




}
