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
            public void onEmoji1Clicked(int x, int y) {
                Toast.makeText(MainActivity.this, "em1!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onEmoji2Clicked(int x, int y) {
                Toast.makeText(MainActivity.this, "em2!", Toast.LENGTH_SHORT).show();

            }
        });
    }

//    ColorMatrixColorFilter getContrastBrightnessFilter(float contrast, float brightness) {
//        ColorMatrix cm = new ColorMatrix(new float[]
//                {
//                        contrast, 0, 0, 0, brightness,
//                        0, contrast, 0, 0, brightness,
//                        0, 0, contrast, 0, brightness,
//                        0, 0, 0, 1, 0
//                });
//        return new ColorMatrixColorFilter(cm);
//    }

    public void test(View view) {
//        Log.i("point ma60", counter + "");
//        myImage.setColorFilter(getContrastBrightnessFilter((float) (counter + 10) / 10, 100));
//        counter++;

        myImage.test();
    }
}
