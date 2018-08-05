package com.ritik.emojireactionview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ritik.emojireactionlibrary.ClickInterface;
import com.ritik.emojireactionlibrary.EmojiReactionView;

import java.util.ArrayList;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {
    private ArrayList<Feed> mDataSet;
    private Context context;

    // Provide a reference to the views for each data item
    // you provide access to all the views for a data item in a view holder
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView, timeTextView, name;
        //        ImageView photo;
        EmojiReactionView photo;

        private ViewHolder(View view) {
            super(view);
//            photo = view.findViewById(R.id.photoImageView);
            photo = view.findViewById(R.id.photoImageView);
            messageTextView = (TextView) view.findViewById(R.id.messageTextView);

            timeTextView = (TextView) view.findViewById(R.id.time);
            name = (TextView) view.findViewById(R.id.name);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    FeedAdapter(ArrayList<Feed> myDataSet) {
        mDataSet = myDataSet;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feed, parent, false);
        this.context = parent.getContext();
        return new ViewHolder(v);
    }
    //TODO open and close circle anim on view creation

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Feed feed = mDataSet.get(position);
//        Log.i(post.getText(), "standpoint po99");

        holder.messageTextView.setText(feed.getMessage());
        holder.photo.setImageResource(feed.getPicAddress());
        holder.photo.setOnEmojiClickListener(new ClickInterface() {
            @Override
            public void onEmojiClicked(int emojiIndex, int x, int y) {
                Toast.makeText(context, "em "+emojiIndex, Toast.LENGTH_SHORT).show();
                feed.setClickedEmoji(emojiIndex);
            }

            @Override
            public void onEmojiUnclicked(int emojiIndex, int x, int y) {
                Toast.makeText(context, "unem "+emojiIndex, Toast.LENGTH_SHORT).show();
            }
        });

        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show();
            }
        });

        holder.photo.setClickedEmojiNumber(feed.getClickedEmoji());
        holder.timeTextView.setText(feed.getTime());
        holder.name.setText(feed.getName());

    }

    // Return the size of your dataSet (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

}
