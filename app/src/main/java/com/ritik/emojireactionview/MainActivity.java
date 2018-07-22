package com.ritik.emojireactionview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.ritik.emojireactionlibrary.ClickInterface;
import com.ritik.emojireactionlibrary.EmojiReactionView;

public class MainActivity extends AppCompatActivity {

    EmojiReactionView myImage;
    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myImage = findViewById(R.id.image);

        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        myImage.setOnEmojiClickListener(new ClickInterface() {
            @Override
            public void onEmojiClicked(int emojiIndex, int x, int y) {
                Toast.makeText(MainActivity.this, "em" + (emojiIndex + 1) + "!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEmojiUnclicked(int emojiIndex, int x, int y) {
                Toast.makeText(MainActivity.this, "em" + (emojiIndex + 1) + "! undo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void test(View view) {
        myImage.test();
    }
}
