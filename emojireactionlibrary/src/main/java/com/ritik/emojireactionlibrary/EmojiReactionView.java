package com.ritik.emojireactionlibrary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
    int iCurStep = 0;// current step
    ExecuteAsync task;
    Timer moveBeadTimer, emojiRisingTimer;

    private int[] centre = new int[2];
    private int radius = 150;
    private double angle = Math.PI / 3;
    private int clickedEmojiNumber = 0;
    private boolean clickingAnimWorking, circleAnimWorking, emojiRising;
    ArrayList<RisingEmoji> rects = new ArrayList<>();


    private Rect emojiRect1 = new Rect();
    private Rect emojiRect2 = new Rect();
    private Rect emojiRect3 = new Rect();
    private Rect emojiRect4 = new Rect();

    private int[] emojiPoint1 = new int[2];
    private int[] emojiPoint2 = new int[2];
    private int[] emojiPoint3 = new int[2];
    private int[] emojiPoint4 = new int[2];
    private float[] emojiMovingPoint1 = new float[2];
    private float[] emojiMovingPoint2 = new float[2];
    private float[] emojiMovingPoint3 = new float[2];
    private float[] emojiMovingPoint4 = new float[2];
    private Path emojiPath1 = new Path();
    private Path emojiPath2 = new Path();
    private Bitmap mBitmap;
    private Bitmap emojiBitmap1;
    private Bitmap emojiBitmap2;
    private Bitmap emojiBitmap3;
    private Bitmap emojiBitmap4;
    private int coordLeft = 0;

    private boolean mReady;
    private boolean mSetupPending, alternater = false;
    private Context mContext;

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
                final int resourceId = resourceArray.getResourceId(0, 0);
                final int resourceId1 = resourceArray.getResourceId(1, 0);
                emojiBitmap1 = getBitmapFromDrawable(getResources().getDrawable(resourceId));
                emojiBitmap2 = getBitmapFromDrawable(getResources().getDrawable(resourceId1));
                Log.i("point mi89", "attrs" + i + " " + resourceId);

            }
            resourceArray.recycle();
        }

        arr.recycle();
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

    public void setOnEmojiClickListener(@Nullable ClickInterface l) {
        this.mClickInterface = l;
    }

    @Override
    public ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    void timer() {
        if (moveBeadTimer == null) {
            moveBeadTimer = new Timer();
            moveBeadTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
//                Log.i("point ma255", "run started");
                    task = new ExecuteAsync();
                    task.execute(new String[]{null});
                }
            }, 5, 100);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i("point mi113", "draw");
        super.onDraw(canvas);
//        if (mBitmap == null) {
//            return;
//        }

        if (circleAnimWorking) {
            canvas.drawBitmap(emojiBitmap1, null, calculateNewRect(emojiRect1, (int) emojiMovingPoint1[0], (int) emojiMovingPoint1[1], 40), null);
            canvas.drawBitmap(emojiBitmap2, null, calculateNewRect(emojiRect2, (int) emojiMovingPoint2[0], (int) emojiMovingPoint2[1], 40), null);
            startcircleAnim();
        }
        if (clickingAnimWorking) {
            canvas.drawBitmap(emojiBitmap1, null, emojiRect1, null);
            canvas.drawBitmap(emojiBitmap2, null, emojiRect2, null);

        }
        Log.i("point mi171", "draw" + emojiRising + " ");

        if (emojiRising) {
            for (RisingEmoji re : rects) {
                canvas.drawBitmap(emojiBitmap1, null, re.getRect(), re.getPaint());
            }
        }
    }

    public void test() {
        emojiRisinginit(1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("point mi213", event.getX() + " " + event.getY());

        if (emojiRect1.contains((int) event.getX(), (int) event.getY())) {
            mClickInterface.onEmoji1Clicked((int) event.getX(), (int) event.getY());
            clickedEmojiNumber = 1;
            startClickingAnim(emojiRect1);
        } else if (emojiRect2.contains((int) event.getX(), (int) event.getY())) {
            mClickInterface.onEmoji2Clicked((int) event.getX(), (int) event.getY());
            clickedEmojiNumber = 2;
            startClickingAnim(emojiRect2);
        }
        return false;

        //TODO: want onclick of user to work, return correctclick && super.onTouchEvent(event)
    }

    private void startClickingAnim(Rect emojiRect) {
        clickingAnimWorking = true;
        clickAnim(emojiRect);
    }

    private void clickAnim(final Rect emojiRect) {
        ValueAnimator animator = ValueAnimator.ofInt(0, 4);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                emojiRect.inset((int) valueAnimator.getAnimatedValue(), (int) valueAnimator.getAnimatedValue());
                invalidate();
                Log.i("point 198", emojiRect.left + " " + emojiRect.right + " " + emojiRect.width());
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
//                clickingAnimWorking = false;
            }
        });
        animator.setDuration(800);
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

    private void riseEmoji() {
        Log.i("point mi257", "riseEmoji" + rects.size());

        for (int i = 0; i < rects.size(); i++) {
            rects.get(i).setRect(calculateNewRect(rects.get(i).getRect(), rects.get(i).getRect().centerX(), rects.get(i).getRect().centerY() - rects.get(i).getSpeed(), rects.get(i).getHalfSide()));
            if (rects.get(i).getRect().top <= rects.get(i).getMaxHeight()) {
                if (rects.get(i).getPaint() == null) rects.get(i).setPaint(new Paint());

                rects.get(i).getPaint().setAlpha(rects.get(i).getPaint().getAlpha() / 2);
                Log.i("point mi268", "riseEmoji" + rects.get(i).getPaint().getAlpha());

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
                break;
            }
//            Log.i("point mi269", "riseEmoji" + rects.size());

        }

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
        mBitmap = getBitmapFromDrawable(getDrawable());
        setup();
    }

    private void setup() {
        Log.i("point mi257", "setup");
        if (!mReady) {
            mSetupPending = true;
            return;
        }
        Log.i("point mi262", "setup");

        if (getWidth() == 0 && getHeight() == 0) {
            return;
        }
        if (mBitmap == null) {
            invalidate();
            return;
        }
        Log.i("point mi273", "here");

        emojiRect1 = new Rect(coordLeft, 20, coordLeft + 80, 70);
        emojiRect2 = new Rect(coordLeft, 60, coordLeft + 80, 120);
//        timer();

        centre[0] = (getWidth() + getPaddingLeft() - getPaddingRight()) / 2;
        centre[1] = getHeight() - getPaddingBottom();

        emojiPoint1[0] = (int) (centre[0] + radius * Math.cos(angle + Math.PI));
        emojiPoint1[1] = (int) (centre[1] + radius * Math.sin(angle + Math.PI));

        emojiPoint2[0] = (int) (centre[0] + radius * Math.cos(2 * angle + Math.PI));
        emojiPoint2[1] = (int) (centre[1] + radius * Math.sin(2 * angle + Math.PI));
        Log.i("point mi340", "centre " + centre[0] + " " + centre[1] + " emojiPoint1 " + emojiPoint1[0] + " " + emojiPoint1[1] + " emojiPoint2 " + emojiPoint2[0] + " " + emojiPoint2[1]);

        emojiPath1.moveTo(centre[0], centre[1]);
        emojiPath1.lineTo(emojiPoint1[0], emojiPoint1[1]);
//
        emojiPath2.moveTo(centre[0], centre[1]);
        emojiPath2.lineTo(emojiPoint2[0], emojiPoint2[1]);

//        Log.i("point mi326", "pl" + getPaddingLeft() + " pr " + getPaddingRight() + " w " + getWidth());
        circleAnimWorking = true;
        startcircleAnim();
    }

    private void startcircleAnim() {
        PathMeasure pm = new PathMeasure(emojiPath1, false);
        PathMeasure pm2 = new PathMeasure(emojiPath2, false);
        float fSegmentLen = pm.getLength() / 20;//20 animation steps
        Log.i("point mi368", iCurStep + "  " + emojiMovingPoint1[0] + "  " + emojiMovingPoint1[1]);
        if (iCurStep <= 20) {
            pm.getPosTan(fSegmentLen * iCurStep, emojiMovingPoint1, null);
            pm2.getPosTan(fSegmentLen * iCurStep, emojiMovingPoint2, null);
            iCurStep++;

            invalidate();
        } else {
            iCurStep = 0;
            circleAnimWorking = false;
        }
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

    // asynctask to reduce load on main thread
    private class ExecuteAsync extends AsyncTask<String, String, String> {

        public ExecuteAsync() {
        }

        @Override
        protected String doInBackground(String... urls) {
//            Log.i("point mi137", "coordleft" + coordLeft);
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

}
