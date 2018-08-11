package com.ritik.emojireactionview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class MyRecyclerView extends Fragment {

    public static RecyclerView.Adapter mAdapter;
    ArrayList<Feed> feeds;

    public MyRecyclerView() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.my_recycler, container, false);

        RecyclerView mRecyclerView = view.findViewById(R.id.my_recycler_view);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(mRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        feeds = new ArrayList<>();

        mAdapter = new FeedAdapter(feeds);

        feeds.add(new Feed("Adam", R.drawable.feed8, "Nov 6,11:52 AM", "The journey not the arrival matters.", -1));
        feeds.add(new Feed("Alina", R.drawable.feed9, "Nov 6,11:52 AM", "Just living is not enough...", -1));
        feeds.add(new Feed("James", R.drawable.feed4, "Nov 6,11:52 AM", "The dog is the perfect portrait subject. He doesn't pose. He isn't aware of the camera.", -1));
        feeds.add(new Feed("Emily", R.drawable.feed10, "Nov 6,11:52 AM", "Dream as if you’ll live forever, live as if you’ll die today.", -1));
        feeds.add(new Feed("Moore", R.drawable.feed3, "Nov 6,11:52 AM", "Quotes .... truth!! life", -1));
        feeds.add(new Feed("Thomson", R.drawable.feed5, "Nov 6,11:52 AM", "I spent 90 percent of my money on women and drink. The rest I wasted!", -1));
        feeds.add(new Feed("William", R.drawable.feed6, "Nov 6,11:52 AM", "Music is my medicine!!", -1));
        feeds.add(new Feed("Olivia", R.drawable.feed7, "Nov 6,11:52 AM", "It's refreshing to have some time off from wondering whether I look fat.", -1));
        feeds.add(new Feed("Sophia", R.drawable.feed1, "Nov 6,11:52 AM", "Adventure may hurt you but monotony will kill you.", -1));
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }
}
