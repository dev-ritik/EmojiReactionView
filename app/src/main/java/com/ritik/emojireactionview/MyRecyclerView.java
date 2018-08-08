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

        feeds = new ArrayList<>();

        mAdapter = new FeedAdapter(feeds);

        feeds.add(new Feed("Rahul", R.drawable.android1_min, "Nov 6,11:52 AM", "howdy", -1));
        feeds.add(new Feed("Ritik", R.drawable.android1_min, "Nov 6,11:52 AM", "fine", 1));
        feeds.add(new Feed("Raj", R.drawable.android1_min, "Nov 6,11:52 AM", "Yup", 1));
        feeds.add(new Feed("Rajendra", R.drawable.android1_min, "Nov 6,11:52 AM", "Enjoying", 1));
        feeds.add(new Feed("Ravish", R.drawable.android1_min, "Nov 6,11:52 AM", "Life's Good", 1));
        feeds.add(new Feed("Rajnath", R.drawable.android1_min, "Nov 6,11:52 AM", "Beautiful", 1));
        feeds.add(new Feed("Ramesh", R.drawable.android1_min, "Nov 6,11:52 AM", "Great", 1));
        feeds.add(new Feed("Rohit", R.drawable.android1_min, "Nov 6,11:52 AM", "Wooooow", 1));
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }
}
