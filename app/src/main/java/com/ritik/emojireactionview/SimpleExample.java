package com.ritik.emojireactionview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ritik.emojireactionlibrary.ClickInterface;
import com.ritik.emojireactionlibrary.EmojiReactionView;

public class SimpleExample extends Fragment {

    EmojiReactionView myImage;
    int clickedEmoji = 0;

    public SimpleExample() {
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
        View view=inflater.inflate(R.layout.simple, container, false);
        myImage = view.findViewById(R.id.image);
        if (savedInstanceState != null) {
            clickedEmoji = savedInstanceState.getInt("emojiNumber");
            myImage.setClickedEmojiNumber(clickedEmoji);
        }

        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public
            void onClick(View v) {
                Toast.makeText(getActivity(), "clicked!!", Toast.LENGTH_SHORT).show();
            }
        });

        myImage.setOnEmojiClickListener(new ClickInterface() {
            @Override
            public void onEmojiClicked(int emojiIndex, int x, int y) {
                Toast.makeText(getActivity(), "em" + (emojiIndex + 1) + "!", Toast.LENGTH_SHORT).show();
                clickedEmoji = emojiIndex;
            }

            @Override
            public void onEmojiUnclicked(int emojiIndex, int x, int y) {
                Toast.makeText(getActivity(), "em" + (emojiIndex + 1) + "! undo", Toast.LENGTH_SHORT).show();
            }
        });
        return view;

    }

}
