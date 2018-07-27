package com.ritik.emojireactionlibrary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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

import static android.util.TypedValue.TYPE_DIMENSION;

public class EmojiReactionView extends ImageView {
    private ClickInterface mClickInterface;
    private Bitmap[] emojiBitmap;
    private int clickedEmojiNumber = -1;
    private int numberOfEmojis = 0;
    private boolean mReady;
    private boolean mSetupPending;
    private ArrayList<Integer> emojiId = new ArrayList<>();
    private Context context;
    private float densityFactor = getResources().getDisplayMetrics().density;

    //coverEmoji
    private Rect coverRect = new Rect();
    private int coverSide = 80;
    private float coverCenterX;
    private float coverCenterY;
    private Bitmap coverBitmap;
    private boolean coverEmojiVisible;

    //circleAnim
    private float[] centre = new float[2];
    private float radius = 150;
    private int emojiReactSide = 80;
    private Rect[] emojiRect;
    private int[][] emojiMovingPoint;
    private PathMeasure[] pms;
    private boolean circleAnimWorking;

    //clicking/unclicking
    private int clickedRadius = 50;
    private Paint clickedPaint = new Paint();
    int iCurStep = 0;// current step
    private boolean clickingAnimWorking;

    //risingEmoji
    private int emojisRisingSpeed = 20;
    private ArrayList<RisingEmoji> risingEmojis = new ArrayList<>();
    private int numberOfRisers = 24;
    private boolean emojiRising;
    private Timer emojiRisingTimer;
    private int vanished = 0;
    private boolean startDisappear = false;

    public EmojiReactionView(Context context) {
        super(context);
        init();
    }

    public EmojiReactionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmojiReactionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        this.initBaseXMLAttrs(context, attrs);

        init();
    }

    final void initBaseXMLAttrs(Context context, AttributeSet attrs) {
        final TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.EmojiReactionView);

        Log.i("point mi88", "attrs" + densityFactor);
        final int N = arr.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = arr.getIndex(i);
//            Log.i("point mi92", "attrs" + i);

            if (attr == R.styleable.EmojiReactionView_emojis) {
                final TypedArray resourceArray = context.getResources().obtainTypedArray(arr.getResourceId(R.styleable.EmojiReactionView_emojis, 0));
                for (int j = 0; j < resourceArray.length(); j++) {
                    emojiId.add(resourceArray.getResourceId(j, 0));
                }
                resourceArray.recycle();
            } else if (attr == R.styleable.EmojiReactionView_center_X) {
//                Log.i("point 104", "attrs");
                if (arr.peekValue(attr).type == TYPE_DIMENSION)
                    centre[0] = arr.getDimensionPixelSize(attr, -1);
                else {
                    centre[0] = checkFraction(arr.getFraction(attr, 1, 1, -1));
                }
                Log.i("point 107", "here" + centre[0]);

            } else if (attr == R.styleable.EmojiReactionView_center_Y) {
                if (arr.peekValue(attr).type == TYPE_DIMENSION)
                    centre[1] = arr.getDimensionPixelSize(attr, -1);
                else
                    centre[1] = checkFraction(arr.getFraction(attr, 1, 1, -1));
                Log.i("point 114", "here" + centre[1]);
            } else if (attr == R.styleable.EmojiReactionView_radius) {
                if (arr.peekValue(attr).type == TYPE_DIMENSION)
                    radius = arr.getDimensionPixelSize(attr, (int) radius);
                else
                    radius = checkFraction(arr.getFraction(attr, 1, 1, radius));
//                Log.i("point mi117", radius + "");

            } else if (attr == R.styleable.EmojiReactionView_cover_Center_X) {
                if (arr.peekValue(attr).type == TYPE_DIMENSION)
                    coverCenterX = arr.getDimensionPixelSize(attr, -1);
                else
                    coverCenterX = checkFraction(arr.getFraction(attr, 1, 1, -1));
//                Log.i("point mi117", radius + "");

            } else if (attr == R.styleable.EmojiReactionView_cover_Center_Y) {
                if (arr.peekValue(attr).type == TYPE_DIMENSION)
                    coverCenterY = arr.getDimensionPixelSize(attr, -1);
                else
                    coverCenterY = checkFraction(arr.getFraction(attr, 1, 1, -1));
//                Log.i("point mi117", radius + "");

            } else if (attr == R.styleable.EmojiReactionView_cover_side) {
                if (arr.peekValue(attr).type == TYPE_DIMENSION)
                    coverSide = arr.getDimensionPixelSize(attr, coverSide);
//                Log.i("point mi117", radius + "");

            } else if (attr == R.styleable.EmojiReactionView_emoji_react_side) {
                if (arr.peekValue(attr).type == TYPE_DIMENSION)
                    emojiReactSide = arr.getDimensionPixelSize(attr, emojiReactSide);
//                Log.i("point mi117", radius + "");

            } else if (attr == R.styleable.EmojiReactionView_emojis_rising_speed) {
                if (arr.peekValue(attr).type == TYPE_DIMENSION)
                    emojisRisingSpeed = arr.getDimensionPixelSize(attr, emojisRisingSpeed);
//                Log.i("point mi117", radius + "");

            }
//            else if (attr == R.styleable.EmojiReactionView_height_emojis_rising) {
//                if (arr.peekValue(attr).type == TYPE_DIMENSION)
//                    radius = arr.getDimensionPixelSize(attr, 100);
//                else
//                    radius = (int) arr.getFraction(attr, 100, 100, 100);
//            }
            else if (attr == R.styleable.EmojiReactionView_emojis_rising_number) {
                numberOfRisers = arr.getInt(attr, numberOfEmojis);
            } else if (attr == R.styleable.EmojiReactionView_set_emoji) {
                clickedEmojiNumber = arr.getInt(attr, clickedEmojiNumber);
            }
        }

        arr.recycle();
        numberOfEmojis = emojiId.size();
        emojiBitmap = new Bitmap[numberOfEmojis];

    }

    private float checkFraction(float input) {
        if (input == -1 || (input <= 1 && input >= 0)) {
            return input;
        } else throw new IllegalArgumentException();
    }

    public float[] getCentre() {
        return centre;
    }

    public void setCentre(float[] centre) {
        this.centre = centre;
        setPathCircle();
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        setPathCircle();
    }

    public int getClickedEmojiNumber() {
        return clickedEmojiNumber;
    }

    public void setClickedEmojiNumber(int clickedEmojiNumber) {
        if (this.clickedEmojiNumber == -1) {
            mClickInterface.onEmojiUnclicked(clickedEmojiNumber, -1, -1);
        } else if (this.clickedEmojiNumber != clickedEmojiNumber) {
            mClickInterface.onEmojiUnclicked(this.clickedEmojiNumber, -1, -1);
            mClickInterface.onEmojiClicked(clickedEmojiNumber, -1, -1);
        } else {
            mClickInterface.onEmojiUnclicked(this.clickedEmojiNumber, -1, -1);
        }
        this.clickedEmojiNumber = clickedEmojiNumber;
        invalidate();
    }

    public int getNumberOfEmojis() {
        return numberOfEmojis;
    }

    public boolean isClickingAnimWorking() {
        return clickingAnimWorking;
    }

    public boolean isCircleAnimWorking() {
        return circleAnimWorking;
    }

    public boolean isEmojiRising() {
        return emojiRising;
    }

    public int getEmojisRisingSpeed() {
        return emojisRisingSpeed;
    }

    public void setEmojisRisingSpeed(int emojisRisingSpeed) {
        this.emojisRisingSpeed = emojisRisingSpeed;
    }

    public Rect getCoverRect() {
        return coverRect;
    }

    public int getCoverSide() {
        return coverSide;
    }

    public float getCoverCenterX() {
        return coverCenterX;
    }

    public void setCoverCenterX(float coverCenterX) {
        this.coverCenterX = coverCenterX;
        setCoverRect();
        invalidate();
    }

    public float getCoverCenterY() {
        return coverCenterY;
    }

    public void setCoverCenterY(float coverCenterY) {
        this.coverCenterY = coverCenterY;
        setCoverRect();
        invalidate();
    }

    public int getEmojiReactSide() {
        return emojiReactSide;
    }

    public Bitmap getCoverBitmap() {
        return coverBitmap;
    }

    public void setCoverBitmap(Bitmap coverBitmap) {
        this.coverBitmap = coverBitmap;
    }

    public boolean isCoverEmojiVisible() {
        return coverEmojiVisible;
    }

    public void setOnEmojiClickListener(@Nullable ClickInterface l) {
        this.mClickInterface = l;
    }

    private void init() {
        Log.i("point mi73", "init" + densityFactor + " " + getWidth());
        mReady = true;
        clickedPaint.setColor(Color.argb(125, 185, 185, 185));
        emojiRect = new Rect[numberOfEmojis];
        for (int i = 0; i < numberOfEmojis; i++) {
            emojiRect[i] = new Rect();
        }
        if (clickedEmojiNumber != -1) {
            emojiBitmap[clickedEmojiNumber] = getBitmapFromId(emojiId.get(clickedEmojiNumber));
        }

        multiplyDensity();

        if (mSetupPending) {
            setup();
            mSetupPending = false;
        }
    }

    private void multiplyDensity() {
        Log.i("point 314", "multiplyDensity" + coverSide);

//        coverSide *= densityFactor;
//        emojiReactSide *= densityFactor;
//        clickedRadius*=densityFactor;
        Log.i("point 317", "multiplyDensity" + coverSide);

    }

    private void setup() {
        Log.i("point 158", "setup" + getWidth() + " " + densityFactor);

        if (!mReady) {
            mSetupPending = true;
            return;
        }
        if (emojiId == null) {
            return;
        }

//        Log.i("point mi262", "setup" + getWidth() + " ");

        if (getWidth() == 0 && getHeight() == 0) {
            return;
        }
        Log.i("point mi273", "here" + getHeight() + " " + densityFactor);
//        Log.i("point 204", "here" + coverCenterX + " " + coverCenterY + " " + coverSide);

        setCoverRect();
        setPathCircle();

        coverEmojiVisible = true;
    }

    private void setCoverRect() {
        if (coverCenterX == -1 || coverCenterX == 0) {
            coverCenterX = getPaddingLeft() + 10 + coverSide / 2;
        } else if (coverCenterX > 0 && coverCenterX < 1) {
            coverCenterX *= getWidth() + getPaddingLeft() - getPaddingRight();
        }
        if (coverCenterY == -1 || coverCenterY == 0) {
            coverCenterY = getHeight() - getPaddingRight() - coverSide / 2 - 10;
        } else if (coverCenterY > 0 && coverCenterY < 1) {
            coverCenterY *= getHeight() - getPaddingBottom();
        }

        coverBitmap = getBitmapFromId(R.drawable.cover_min);
        coverRect = new Rect((int) (coverCenterX - coverSide / 2), (int) (coverCenterY - coverSide / 2), (int) (coverCenterX + coverSide / 2), (int) (coverCenterY + coverSide / 2));
    }

    private void setPathCircle() {
        if (centre[0] == 0 || centre[0] == -1)
            centre[0] = (getWidth() + getPaddingLeft() - getPaddingRight()) / 2;
        else if (centre[0] <= 1 && centre[0] > 0) {
            centre[0] *= getWidth() + getPaddingLeft() - getPaddingRight();
        }
        if (centre[1] == -1 || centre[1] == 0)
            centre[1] = getHeight() - getPaddingBottom();
        else if (centre[1] <= 1 && centre[1] > 0) {
            centre[1] *= getHeight() - getPaddingBottom();
        }

        if (radius == -1)
            radius = (getWidth() + getPaddingLeft() - getPaddingRight());
        else if (radius <= 1 && radius >= 0) {
            radius *= (getWidth() + getPaddingLeft() - getPaddingRight());
        }

        double angle = Math.PI / (numberOfEmojis + 1);

        Path emojiPath1;
        pms = new PathMeasure[numberOfEmojis];
        for (int i = 1; i <= numberOfEmojis; i++) {
            emojiPath1 = new Path();
            emojiPath1.moveTo(centre[0], centre[1]);
            emojiPath1.lineTo((float) (centre[0] + radius * Math.cos(i * angle + Math.PI)), (float) (centre[1] + radius * Math.sin(i * angle + Math.PI)));
            pms[i - 1] = new PathMeasure(emojiPath1, false);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
//        Log.i("point mi113", "draw" + coverEmojiVisible + circleAnimWorking + clickingAnimWorking + emojiRising);
//        Log.i("point 386", "here" + emojiBitmap.length + " " + emojiId.size());
        super.onDraw(canvas);

        if (coverEmojiVisible)
            if (clickedEmojiNumber == -1)
                canvas.drawBitmap(coverBitmap, null, coverRect, null);
            else canvas.drawBitmap(emojiBitmap[clickedEmojiNumber], null, coverRect, null);

        if (circleAnimWorking) {
            if (clickedEmojiNumber != -1) {
                canvas.drawCircle(emojiMovingPoint[clickedEmojiNumber][0], emojiMovingPoint[clickedEmojiNumber][1], clickedRadius, clickedPaint);
            }
            for (int i = 0; i < numberOfEmojis; i++) {
                canvas.drawBitmap(emojiBitmap[i], null, calculateNewRect(emojiRect[i], emojiMovingPoint[i][0], emojiMovingPoint[i][1], emojiReactSide / 2), null);
            }
            startCircleAnim();
        }
        if (clickingAnimWorking) {
            if (clickedEmojiNumber != -1) {
                canvas.drawCircle(emojiMovingPoint[clickedEmojiNumber][0], emojiMovingPoint[clickedEmojiNumber][1], clickedRadius, clickedPaint);
            }
            for (int i = 0; i < numberOfEmojis; i++) {
                canvas.drawBitmap(emojiBitmap[i], null, emojiRect[i], null);
            }
        }

        if (emojiRising) {
            for (RisingEmoji re : risingEmojis) {
                canvas.drawBitmap(emojiBitmap[clickedEmojiNumber], null, re.getRect(), re.getPaint());
            }
        }
    }

    public void test() {
        circleAnimWorking = false;
        coverEmojiVisible = false;
        clickingAnimWorking = false;
        emojiRising = false;
        invalidate();
    }

    private void startCircleAnim() {
        //TODO: rotate!!
        for (int i = 0; i < numberOfEmojis; i++) {
            if (emojiBitmap[i] == null) {
                emojiBitmap[i] = getBitmapFromId(emojiId.get(i));
            }
        }
        float[] emojiMovingPointFloat = new float[2];
        int[] emojiMovingPointInt;

        if (emojiMovingPoint == null) {
            emojiMovingPoint = new int[numberOfEmojis][2];
            for (int i = 0; i < numberOfEmojis; i++) {
                emojiMovingPointInt = new int[2];
                emojiMovingPoint[i] = emojiMovingPointInt;
            }
        }

        float fSegmentLen = pms[0].getLength() / 20;//20 animation steps

        if (iCurStep <= 20) {
            for (int i = 0; i < numberOfEmojis; i++) {
                pms[i].getPosTan(fSegmentLen * iCurStep, emojiMovingPointFloat, null);
                emojiMovingPoint[i] = convertFloatArrayToIntArray(emojiMovingPointFloat, emojiMovingPoint[i]);
            }
            iCurStep++;
            setColorFilter(Color.rgb(255 - 6 * iCurStep, 255 - 6 * iCurStep, 255 - 6 * iCurStep), android.graphics.PorterDuff.Mode.MULTIPLY);
        } else {
            iCurStep = 0;
        }
    }

    private int[] convertFloatArrayToIntArray(float[] emojiMovingPointFloat, int[] ints) {
        ints[0] = (int) emojiMovingPointFloat[0];
        ints[1] = (int) emojiMovingPointFloat[1];
        return ints;
    }

    private void startClickingAnim(final int clickedIndex) {
        circleAnimWorking = false;
        clickingAnimWorking = true;
        ValueAnimator animator = ValueAnimator.ofInt(0, 2);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                emojiRect[clickedIndex].inset((int) valueAnimator.getAnimatedValue(), (int) valueAnimator.getAnimatedValue());
//                Log.i("Point 240", "va" + valueAnimator.getAnimatedValue());
                invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setColorFilter(Color.rgb(255, 255, 255), android.graphics.PorterDuff.Mode.MULTIPLY);
                clickingAnimWorking = false;
                clickedEmojiNumber = clickedIndex;
                emojiRisinginit();
            }
        });
        animator.setDuration(800);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
    }

    private void startUnclickingAnim(final int clickedIndex) {
        circleAnimWorking = false;
        clickingAnimWorking = true;
        ValueAnimator animator = ValueAnimator.ofInt(0, 2);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                emojiRect[clickedIndex].inset((int) valueAnimator.getAnimatedValue(), (int) valueAnimator.getAnimatedValue());
//                Log.i("Point 270", "va" + valueAnimator.getAnimatedValue());
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

    private void emojiRisinginit() {
        emojiRising = true;
        for (int i = 0; i < numberOfRisers; i++) {
            Rect risingRect = calculateNewRect(new Rect(), new Random().nextInt(getWidth() - getPaddingLeft() - getPaddingRight() + 1), getHeight() - getPaddingBottom() + new Random().nextInt(300), 20);
            risingEmojis.add(new RisingEmoji(risingRect, new Random().nextInt(10) + 20, 300, new Random().nextInt(6) + emojisRisingSpeed));
        }

        emojiRisingAnim();
    }

    private void emojiRisingAnim() {

        if (emojiRisingTimer == null) {
            emojiRisingTimer = new Timer();
            emojiRisingTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    riseEmoji();
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            invalidate();
                        }
                    });
                }
            }, 5, 50);
        }

    }

    private void riseEmoji() {
        vanished += new Random().nextInt(2);

        for (int i = 0; i < risingEmojis.size(); i++) {
            RisingEmoji re = risingEmojis.get(i);
            re.setRect(calculateNewRect(re.getRect(), re.getRect().centerX(), re.getRect().centerY() - re.getSpeed(), re.getHalfSide()));
            if (startDisappear || re.getRect().top <= re.getMaxHeight()) {
                startDisappear = true;
                if (vanished > risingEmojis.size()) vanished = risingEmojis.size();
                if (re.getPaint() == null) re.setPaint(new Paint());
                if (i <= vanished)
                    re.getPaint().setAlpha(re.getPaint().getAlpha() / 2);

                if (re.getPaint().getAlpha() < 10) {
                    risingEmojis.remove(risingEmojis.get(i));
                    i--;
                }

            }
            if (risingEmojis.size() == 0) {
                emojiRising = false;
                if (emojiRisingTimer != null) {
                    emojiRisingTimer.cancel();
                    emojiRisingTimer.purge();
                    emojiRisingTimer = null;
                }
                coverEmojiVisible = true;
                vanished = 0;
                startDisappear = false;
                break;
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("point 600", "onTouchEvent" + event.getX() + " y " + event.getY());

        for (int i = 0; i < numberOfEmojis; i++) {
            if (circleAnimWorking && emojiRect[i].contains((int) event.getX(), (int) event.getY())) {

                if (clickedEmojiNumber == i) {
                    mClickInterface.onEmojiUnclicked(i, (int) event.getX(), (int) event.getY());
                    startUnclickingAnim(clickedEmojiNumber);
                    return false;
                } else if (clickedEmojiNumber != -1) {
                    mClickInterface.onEmojiUnclicked(clickedEmojiNumber, (int) event.getX(), (int) event.getY());
                }

                mClickInterface.onEmojiClicked(i, (int) event.getX(), (int) event.getY());
                startClickingAnim(i);
                return false;
            }
        }
        if (circleAnimWorking && clickedEmojiNumber != -1 && clickedOnRing(event.getX(), event.getY(), clickedEmojiNumber)) {
            mClickInterface.onEmojiUnclicked(clickedEmojiNumber, (int) event.getX(), (int) event.getY());
            startUnclickingAnim(clickedEmojiNumber);
            return false;
        }

        if (coverEmojiVisible && coverRect.contains((int) event.getX(), (int) event.getY())) {
            coverEmojiVisible = false;
            circleAnimWorking = true;
            startCircleAnim();
            return false;
        }

        if (circleAnimWorking) {
            circleAnimWorking = false;
            coverEmojiVisible = true;
            setColorFilter(Color.rgb(255, 255, 255), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
        return super.onTouchEvent(event);
    }

    private boolean clickedOnRing(float x, float y, int clickedEmojiNumber) {
        return (Math.pow(x - emojiMovingPoint[clickedEmojiNumber][0], 2) + Math.pow(y - emojiMovingPoint[clickedEmojiNumber][1], 2) <= Math.pow(clickedRadius, 2));
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

    private Bitmap getBitmapFromId(int id) {
        int side = (coverSide > emojiReactSide) ? coverSide : emojiReactSide;
//        Log.i("point 696", "getBitmapFromId");
        Drawable drawable = getResources().getDrawable(id);
        if (drawable == null) {
            return null;
        }
        try {
            Bitmap bitmap;
            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888);
            } else {
                bitmap = Bitmap.createBitmap(side, side, Bitmap.Config.ARGB_8888);
            }

            drawable.setBounds(0, 0, side, side);
            drawable.draw(new Canvas(bitmap));
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Rect calculateNewRect(Rect initialRect, int x, int y, int halfSide) {

        initialRect.left = x - halfSide;
        initialRect.right = x + halfSide;
        initialRect.top = y - halfSide;
        initialRect.bottom = y + halfSide;
        return initialRect;
    }
}
