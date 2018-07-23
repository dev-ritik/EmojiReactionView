package com.ritik.emojireactionlibrary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class EmojiReactionView extends ImageView {

    private static final ScaleType SCALE_TYPE = ScaleType.FIT_XY;

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 2;
    ClickInterface mClickInterface;
    ExecuteAsync task;
    Timer emojiRisingTimer;

    private int[] centre = new int[2];
    private int radius = 150;
    private double angle;
    private int clickedEmojiNumber = -1;
    private int numberOfEmojis = 0;
    private int clickedRadius = 50;
    private boolean clickingAnimWorking, circleAnimWorking, emojiRising;
    ArrayList<RisingEmoji> rects = new ArrayList<>();

    ArrayList<Rect> emojiRect = new ArrayList<>();
    private Rect coverRect = new Rect();

    int iCurStep = 0;// current step
    private ArrayList<int[]> emojiPoint = new ArrayList<>();
    private ArrayList<float[]> emojiMovingPoint = new ArrayList<>();
    private ArrayList<Path> emojiPath = new ArrayList<>();

    private Bitmap coverBitmap;
    private ArrayList<Bitmap> emojiBitmap = new ArrayList<>();
    private int coordLeft = 0;

    private boolean mReady;
    private boolean mSetupPending, alternater = false;
    private Context mContext;
    private boolean coverEmojiVisible;

    public EmojiReactionView(Context context) {
        super(context);
        init();
    }

    public EmojiReactionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmojiReactionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        this.initBaseXMLAttrs(context, attrs);

        setScaleType(ScaleType.FIT_XY);
        init();
    }

    final void initBaseXMLAttrs(Context context, AttributeSet attrs) {
        final TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.EmojiReactionView);

        final int arrayResourceId = arr.getResourceId(
                R.styleable.EmojiReactionView_emojis, 0);
        Log.i("point mi93", "attrs" + arrayResourceId);

        if (arrayResourceId != 0) {
            final TypedArray resourceArray = context.getResources().obtainTypedArray(arrayResourceId);
            for (int i = 0; i < resourceArray.length(); i++) {
                emojiBitmap.add(getBitmapFromDrawable(getResources().getDrawable(resourceArray.getResourceId(i, 0))));
                Log.i("point mi100", "attrs" + emojiBitmap.get(i).getHeight() + " " + emojiBitmap.get(i).getWidth());

            }
            resourceArray.recycle();
        }
        arr.recycle();
        numberOfEmojis = emojiBitmap.size();
    }

    private void init() {
        super.setScaleType(SCALE_TYPE);
        Log.i("point mi73", "init");
        mReady = true;

        if (mSetupPending) {
            setup();
            mSetupPending = false;
        }
    }

    private void setup() {
        if (!mReady) {
            mSetupPending = true;
            return;
        }
        Log.i("point mi262", "setup");

        if (getWidth() == 0 && getHeight() == 0) {
            return;
        }
        Log.i("point mi273", "here");

        coverRect = new Rect(getPaddingLeft() + 10, getHeight() - getPaddingRight() - 80, 80, getHeight() - getPaddingRight() - 10);

        for (int i = 1; i <= numberOfEmojis; i++) {
            emojiRect.add(new Rect(coordLeft, 20, coordLeft + 80, 70));
        }

        centre[0] = (getWidth() + getPaddingLeft() - getPaddingRight()) / 2;
        centre[1] = getHeight() - getPaddingBottom();

        angle = Math.PI / (numberOfEmojis + 1);

        for (int i = 1; i <= numberOfEmojis; i++) {
            emojiPoint.add(new int[]{(int) (centre[0] + radius * Math.cos(i * angle + Math.PI)), (int) (centre[1] + radius * Math.sin(i * angle + Math.PI))});

        }
        Path emojiPath1;
        for (int i = 0; i < numberOfEmojis; i++) {
            emojiPath1 = new Path();
            emojiPath1.moveTo(centre[0], centre[1]);
            emojiPath1.lineTo(emojiPoint.get(i)[0], emojiPoint.get(i)[1]);
            emojiPath.add(emojiPath1);
        }

        coverEmojiVisible = true;
    }

    public void setOnEmojiClickListener(@Nullable ClickInterface l) {
        this.mClickInterface = l;
    }

    @Override
    public ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i("point mi113", "draw" + circleAnimWorking + clickingAnimWorking + emojiRising);
        super.onDraw(canvas);

        if (coverEmojiVisible)
            if (clickedEmojiNumber == -1)
                canvas.drawBitmap(coverBitmap, null, coverRect, null);
            else canvas.drawBitmap(emojiBitmap.get(clickedEmojiNumber), null, coverRect, null);

        if (circleAnimWorking) {
            if (clickedEmojiNumber != -1) {
                Paint paint = new Paint();
                paint.setColor(Color.argb(125, 185, 185, 185));
                canvas.drawCircle((int) emojiMovingPoint.get(clickedEmojiNumber)[0], (int) emojiMovingPoint.get(clickedEmojiNumber)[1], clickedRadius, paint);
            }
            for (int i=0;i<numberOfEmojis;i++){
                Log.i("point mi182", "draw" +emojiBitmap.size()+"" + emojiRect.size()+"" + emojiMovingPoint.size());
                canvas.drawBitmap(emojiBitmap.get(i), null, calculateNewRect(emojiRect.get(i), (int) emojiMovingPoint.get(i)[0], (int) emojiMovingPoint.get(i)[1], 40), null);
            }
            startCircleAnim();
        }
        if (clickingAnimWorking) {
            if (clickedEmojiNumber != -1) {
                Paint paint = new Paint();
                paint.setColor(Color.argb(125, 185, 185, 185));
                canvas.drawCircle((int) emojiMovingPoint.get(clickedEmojiNumber)[0], (int) emojiMovingPoint.get(clickedEmojiNumber)[1], clickedRadius, paint);
            }
            for (int i=0;i<numberOfEmojis;i++){
                canvas.drawBitmap(emojiBitmap.get(i), null, emojiRect.get(i), null);
            }
        }

        if (emojiRising) {
            for (RisingEmoji re : rects) {
                canvas.drawBitmap(emojiBitmap.get(clickedEmojiNumber), null, re.getRect(), re.getPaint());
            }
        }
    }

    public void test() {
        circleAnimWorking = true;
        setColorFilter(Color.rgb(12, 12, 12), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    private void startCircleAnim() {
        ArrayList<PathMeasure> pms = new ArrayList<>();
        float[] emojiMovingPoint1;

        for (int i = 0; i < numberOfEmojis; i++) {
            pms.add(new PathMeasure(emojiPath.get(i), false));
            emojiMovingPoint1 = new float[2];
            emojiMovingPoint.add(emojiMovingPoint1);
        }
        float fSegmentLen = pms.get(0).getLength() / 20;//20 animation steps

        if (iCurStep <= 20) {
            for (int i = 0; i < numberOfEmojis; i++) {
                pms.get(i).getPosTan(fSegmentLen * iCurStep, emojiMovingPoint.get(i), null);
                emojiMovingPoint.set(i, emojiMovingPoint.get(i));
            }
            iCurStep++;
            setColorFilter(Color.rgb(255 - 6 * iCurStep, 255 - 6 * iCurStep, 255 - 6 * iCurStep), android.graphics.PorterDuff.Mode.MULTIPLY);
        } else {
            iCurStep = 0;
            circleAnimWorking = false;
        }
    }

    private void startClickingAnim(int clickedIndex) {
        clickingAnimWorking = true;
        clickAnim(clickedIndex);
    }

    private void clickAnim(final int clickedIndex) {
        ValueAnimator animator = ValueAnimator.ofInt(0, 2);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                emojiRect.get(clickedIndex).inset((int) valueAnimator.getAnimatedValue(), (int) valueAnimator.getAnimatedValue());
                Log.i("Point 240", "va" + valueAnimator.getAnimatedValue());
                invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setColorFilter(Color.rgb(255, 255, 255), android.graphics.PorterDuff.Mode.MULTIPLY);
                clickingAnimWorking = false;
                clickedEmojiNumber = clickedIndex;
                emojiRisinginit(1);
            }
        });
        animator.setDuration(800);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
    }

    private void startUnclickingAnim(final int clickedIndex) {
        clickingAnimWorking = true;
        ValueAnimator animator = ValueAnimator.ofInt(0, 2);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                emojiRect.get(clickedIndex).inset((int) valueAnimator.getAnimatedValue(), (int) valueAnimator.getAnimatedValue());
                Log.i("Point 270", "va" + valueAnimator.getAnimatedValue());
                clickedRadius = 50 - (int) valueAnimator.getAnimatedValue() * 8;
                invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                clickingAnimWorking = false;
                clickedEmojiNumber = -1;
                coverEmojiVisible = true;
                setColorFilter(Color.rgb(255, 255, 255), android.graphics.PorterDuff.Mode.MULTIPLY);
                clickedRadius = 50;
            }
        });
        animator.setDuration(800);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
    }


    private void emojiRisinginit(int emojiNumber) {
        emojiRising = true;
        for (int i = 0; i < 24; i++) {
            Rect risingRect = calculateNewRect(new Rect(), new Random().nextInt(getWidth() - getPaddingLeft() - getPaddingRight() + 1), getHeight() - getPaddingBottom() + new Random().nextInt(300), 20);
            rects.add(new RisingEmoji(risingRect, new Random().nextInt(10) + 20, new Random().nextInt(130) + 160, new Random().nextInt(6) + 20));
        }
        emojiRisingAnim();
    }

    private void emojiRisingAnim() {

        if (emojiRisingTimer == null) {
            emojiRisingTimer = new Timer();
            emojiRisingTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
//                Log.i("point ma255", "run started");
                    task = new ExecuteAsync();
                    task.execute(new String[]{null});
                }
            }, 5, 100);
        }

    }

    private class ExecuteAsync extends AsyncTask<String, String, String> {

        public ExecuteAsync() {
        }

        @Override
        protected String doInBackground(String... urls) {
            riseEmoji();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            invalidate();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }

    private void riseEmoji() {
        Log.i("point mi257", "riseEmoji" + rects.size());

        for (int i = 0; i < rects.size(); i++) {
            rects.get(i).setRect(calculateNewRect(rects.get(i).getRect(), rects.get(i).getRect().centerX(), rects.get(i).getRect().centerY() - rects.get(i).getSpeed(), rects.get(i).getHalfSide()));
            if (rects.get(i).getRect().top <= rects.get(i).getMaxHeight()) {
                if (rects.get(i).getPaint() == null) rects.get(i).setPaint(new Paint());

                rects.get(i).getPaint().setAlpha(rects.get(i).getPaint().getAlpha() / 2);

                if (rects.get(i).getPaint().getAlpha() < 10) {
                    rects.remove(rects.get(i));
                    i--;
                }

            }
//            Log.i("point mi271", "riseEmoji" + rects.size());
            if (rects.size() == 0) {
                emojiRising = false;
                if (emojiRisingTimer != null) {
                    emojiRisingTimer.cancel();
                    emojiRisingTimer.purge();
                    emojiRisingTimer = null;
                }
                coverEmojiVisible = true;
                break;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("point mi213", event.getX() + " " + event.getY());

//        TODO: switching of emoji interface method

        for(int i=0;i<numberOfEmojis;i++){
            if (emojiRect.get(i).contains((int) event.getX(), (int) event.getY())) {

                if (clickedEmojiNumber == emojiRect.indexOf(emojiRect.get(i))) {
                    mClickInterface.onEmojiUnclicked(clickedEmojiNumber, (int) event.getX(), (int) event.getY());
                    startUnclickingAnim(clickedEmojiNumber);
                    return false;
                }
                mClickInterface.onEmojiClicked(clickedEmojiNumber, (int) event.getX(), (int) event.getY());
                startClickingAnim(i);
                return false;
            }
        }

         if (coverRect.contains((int) event.getX(), (int) event.getY())) {
            coverEmojiVisible = false;
            circleAnimWorking = true;
            startCircleAnim();
        }
        return false;

        //TODO: want onclick of user to work, return correctclick && super.onTouchEvent(event)
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        setup();
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        super.setPaddingRelative(start, top, end, bottom);
        setup();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        initializeBitmap();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        initializeBitmap();
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);
        initializeBitmap();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        initializeBitmap();
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private void initializeBitmap() {
        coverBitmap = getBitmapFromDrawable(getResources().getDrawable(R.drawable.cover));
        Log.i("point mi430", "initializeBitmap" + coverBitmap.getWidth() + " " + coverBitmap.getHeight());

        setup();
    }

    private RectF calculateBounds() {
        return new RectF(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
    }

    private Rect calculateNewRect(Rect initialRect, int x, int y, int halfSide) {

        initialRect.left = x - halfSide;
        initialRect.right = x + halfSide;
        initialRect.top = y - halfSide;
        initialRect.bottom = y + halfSide;
        return initialRect;
    }
}
