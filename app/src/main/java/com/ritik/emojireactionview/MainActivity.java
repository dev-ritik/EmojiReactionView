package com.ritik.emojireactionview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
//      TODO screen rotation doesn't retains state

        myImage = findViewById(R.id.image);

        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "clicked!!", Toast.LENGTH_SHORT).show();

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
//        myImage.test();
//        Log.i("point 45", myImage.getCentre()[0] + " " + myImage.getCentre()[1]);
//        Log.i("point 46", myImage.getRadius() + " " + myImage.getClickedEmojiNumber());
//        Log.i("point 47", myImage.getNumberOfEmojis() + " " + myImage.getEmojisRisingSpeed());
//        Log.i("point 48", myImage.getCoverRect().left + " " + myImage.getCoverRect().right);
        Log.i("point 49", myImage.getCoverSide() + " " + myImage.getCoverCenterX());
        Log.i("point 50", myImage.getCoverCenterY() + " " + myImage.getEmojiReactSide());
    }

    public void test1(View view) { }

}
