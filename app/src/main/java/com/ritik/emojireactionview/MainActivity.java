package com.ritik.emojireactionview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ritik.emojireactionlibrary.ClickInterface;
import com.ritik.emojireactionlibrary.EmojiReactionView;

public class MainActivity extends AppCompatActivity {

    EmojiReactionView myImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myImage=findViewById(R.id.image);

        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        myImage.setOnEmojiClickListener(new ClickInterface() {
            @Override
            public void onEmoji1Clicked(int x, int y) {
                Toast.makeText(MainActivity.this, "em1!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onEmoji2Clicked(int x, int y) {
                Toast.makeText(MainActivity.this, "em2!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void test(View view) {
        Toast.makeText(this, "tested!", Toast.LENGTH_SHORT).show();
    }
}
