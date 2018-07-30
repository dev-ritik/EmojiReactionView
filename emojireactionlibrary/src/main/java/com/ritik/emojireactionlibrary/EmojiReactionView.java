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
import android.graphics.Matrix;
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
    private ArrayList<Integer> emojiId = new ArrayList<>();
    private Context context;
    private float densityFactor = getResources().getDisplayMetrics().density;
    private int availableHeight = 0;
    private int availableWidth = 0;

    //coverEmoji
    private Rect coverRect = new Rect();
    private int coverSide = 70;
    private float[] coverCenter = new float[2];
    private Bitmap coverBitmap;
    private boolean coverEmojiVisible;

    //circleAnim
    private float[] circleCentre = new float[2];
    private float circleRadius = 150;
    private int emojiReactSide = 70;
    private int[][] emojiMovingPoint;
    private PathMeasure[] pms;
    private boolean circleAnimWorking;
    private ArrayList<Matrix> emojiMatrix = new ArrayList<>();

    //clicking/unclicking
    private int clickedRadius = 50;
    private Paint clickedPaint = new Paint();
    int iCurStep = 1;// current step
    private boolean clickingAnimWorking;

    //risingEmoji
    private int emojisRisingSpeed = 2;
    private float emojisRisingHeight = 350;
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
                    circleCentre[0] = arr.getDimensionPixelSize(attr, -1);
                else {
                    circleCentre[0] = checkFraction(arr.getFraction(attr, 1, 1, -1));
                }
                Log.i("point 107", "here" + circleCentre[0]);

            } else if (attr == R.styleable.EmojiReactionView_center_Y) {
                if (arr.peekValue(attr).type == TYPE_DIMENSION)
                    circleCentre[1] = arr.getDimensionPixelSize(attr, -1);
                else
                    circleCentre[1] = checkFraction(arr.getFraction(attr, 1, 1, -1));
                Log.i("point 114", "here" + circleCentre[1]);
            } else if (attr == R.styleable.EmojiReactionView_radius) {
                if (arr.peekValue(attr).type == TYPE_DIMENSION)
                    circleRadius = arr.getDimensionPixelSize(attr, -1);
                else
                    circleRadius = checkFraction(arr.getFraction(attr, 1, 1, -1));
//                Log.i("point mi117", circleRadius + "");

            } else if (attr == R.styleable.EmojiReactionView_cover_Center_X) {
                if (arr.peekValue(attr).type == TYPE_DIMENSION)
                    coverCenter[0] = arr.getDimensionPixelSize(attr, -1);
                else
                    coverCenter[0] = checkFraction(arr.getFraction(attr, 1, 1, -1));
//                Log.i("point mi117", circleRadius + "");

            } else if (attr == R.styleable.EmojiReactionView_cover_Center_Y) {
                if (arr.peekValue(attr).type == TYPE_DIMENSION)
                    coverCenter[1] = arr.getDimensionPixelSize(attr, -1);
                else
                    coverCenter[1] = checkFraction(arr.getFraction(attr, 1, 1, -1));
//                Log.i("point mi117", circleRadius + "");

            } else if (attr == R.styleable.EmojiReactionView_cover_side) {
                if (arr.peekValue(attr).type == TYPE_DIMENSION)
                    coverSide = arr.getDimensionPixelSize(attr, -1);
//                Log.i("point mi117", circleRadius + "");

            } else if (attr == R.styleable.EmojiReactionView_emoji_react_side) {
                if (arr.peekValue(attr).type == TYPE_DIMENSION)
                    emojiReactSide = arr.getDimensionPixelSize(attr, -1);
//                Log.i("point mi117", circleRadius + "");

            } else if (attr == R.styleable.EmojiReactionView_emojis_rising_speed) {
                if (arr.peekValue(attr).type == TYPE_DIMENSION)
                    emojisRisingSpeed = arr.getDimensionPixelSize(attr, -1);
//                Log.i("point mi117", circleRadius + "");

            } else if (attr == R.styleable.EmojiReactionView_emojis_rising_height) {
                if (arr.peekValue(attr).type == TYPE_DIMENSION)
                    emojisRisingHeight = arr.getDimensionPixelSize(attr, -1);
                else
                    emojisRisingHeight = checkFraction(arr.getFraction(attr, 1, 1, -1));
            } else if (attr == R.styleable.EmojiReactionView_emojis_rising_number) {
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
        return circleCentre;
    }

    public void setCentre(float[] circleCentre) {
        this.circleCentre = circleCentre;
        setPathCircle();
    }

    public float getRadius() {
        return circleRadius;
    }

    public void setRadius(float circleRadius) {
        this.circleRadius = circleRadius;
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
        return coverCenter[0];
    }

    public void setCoverCenterX(float coverCenterX) {
        coverCenter[0] = coverCenterX;
        setCoverRect();
        invalidate();
    }

    public float getCoverCenterY() {
        return coverCenter[1];
    }

    public void setCoverCenterY(float coverCenterY) {
        coverCenter[1] = coverCenterY;
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
        clickedPaint.setColor(Color.argb(125, 185, 185, 185));
        if (clickedEmojiNumber != -1) {
            emojiBitmap[clickedEmojiNumber] = getBitmapFromId(emojiId.get(clickedEmojiNumber), emojiReactSide);
        }

        multiplyDensity();

        setup();
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

        if (emojiId == null) {
            return;
        }

        Log.i("point mi262", "setup" + getWidth() + " ");

        if (getWidth() == 0 && getHeight() == 0) {
            return;
        } else {
            initDimensions();
        }
        Log.i("point mi273", "here" + getHeight() + " " + densityFactor);
//        Log.i("point 204", "here" + coverCenter[0] + " " + coverCenter[1] + " " + coverSide);

        if (emojisRisingHeight == -1 || emojisRisingHeight == 0) {
            emojisRisingHeight = 400;
        } else if (emojisRisingHeight > 0 && emojisRisingHeight < 1) {
            emojisRisingHeight *= getHeight();
        }
        setCoverRect();
        setPathCircle();

        coverEmojiVisible = true;
    }

    private void setCoverRect() {
        if (coverCenter[0] == -1 || coverCenter[0] == 0) {
            coverCenter[0] = 10 + coverSide / 2;
        } else if (coverCenter[0] > 0 && coverCenter[0] < 1) {
            coverCenter[0] *= getWidth();
        }
        if (coverCenter[1] == -1 || coverCenter[1] == 0) {
            coverCenter[1] = getHeight() - coverSide / 2 - 10;
        } else if (coverCenter[1] > 0 && coverCenter[1] < 1) {
            coverCenter[1] *= getHeight();
        }

        coverBitmap = getBitmapFromId(R.drawable.cover_min, coverSide);
        coverRect = new Rect((int) (coverCenter[0] - coverSide / 2), (int) (coverCenter[1] - coverSide / 2), (int) (coverCenter[0] + coverSide / 2), (int) (coverCenter[1] + coverSide / 2));
    }

    private void setPathCircle() {
        if (circleCentre[0] == 0 || circleCentre[0] == -1)
            circleCentre[0] = getWidth() / 2;
        else if (circleCentre[0] <= 1 && circleCentre[0] > 0) {
            circleCentre[0] *= getWidth();
        }
        if (circleCentre[1] == -1 || circleCentre[1] == 0)
            circleCentre[1] = getHeight() - emojiReactSide / 2;
        else if (circleCentre[1] <= 1 && circleCentre[1] > 0) {
            circleCentre[1] *= getHeight();
        }

        if (circleRadius == -1)
            circleRadius = 150;
        else if (circleRadius <= 1 && circleRadius >= 0) {
            circleRadius *= getWidth();
        }

        double angle = Math.PI / (numberOfEmojis + 1);

        Path emojiPath1;
        pms = new PathMeasure[numberOfEmojis];
        for (int i = 1; i <= numberOfEmojis; i++) {
            emojiPath1 = new Path();
            emojiPath1.moveTo(circleCentre[0], circleCentre[1]);
            emojiPath1.lineTo((float) (circleCentre[0] + circleRadius * Math.cos(i * angle + Math.PI)), (float) (circleCentre[1] + circleRadius * Math.sin(i * angle + Math.PI)));
            pms[i - 1] = new PathMeasure(emojiPath1, false);
        }

    }

//    Matrix m = new Matrix();

    @Override
    protected void onDraw(Canvas canvas) {
//        Log.i("point mi113", "draw" + coverEmojiVisible + circleAnimWorking + clickingAnimWorking + emojiRising);
//        Log.i("point 386", "here" + emojiBitmap.length + " " + emojiId.size());
        Log.i("point mi113", "draw");
        super.onDraw(canvas);

//        circleAnimWorking = false;
//        coverEmojiVisible = false;
//        clickingAnimWorking = false;
//        emojiRising = false;
//        m.setScale(2,2);
//        m.postTranslate(200 - 2*35, 200 - 2*35);
//        m.postRotate(98, 200, 200);
//        canvas.drawBitmap(coverBitmap, m, null);
//        canvas.drawCircle(getPaddingLeft(), 300, 100, clickedPaint);

        if (coverEmojiVisible)
            if (clickedEmojiNumber == -1)
                canvas.drawBitmap(coverBitmap, null, coverRect, null);
            else canvas.drawBitmap(emojiBitmap[clickedEmojiNumber], null, coverRect, null);

        if (circleAnimWorking) {
            if (clickedEmojiNumber != -1) {
                canvas.drawCircle(emojiMovingPoint[clickedEmojiNumber][0], emojiMovingPoint[clickedEmojiNumber][1], clickedRadius, clickedPaint);
            }
            for (int i = 0; i < numberOfEmojis; i++) {
                canvas.drawBitmap(emojiBitmap[i], emojiMatrix.get(i), null);
            }
            startCircleAnim();
        }
        if (clickingAnimWorking) {
            if (clickedEmojiNumber != -1) {
                canvas.drawCircle(emojiMovingPoint[clickedEmojiNumber][0], emojiMovingPoint[clickedEmojiNumber][1], clickedRadius, clickedPaint);
            }
            for (int i = 0; i < numberOfEmojis; i++) {
                canvas.drawBitmap(emojiBitmap[i], emojiMatrix.get(i), null);
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
        float[] emojiMovingPointFloat = new float[2];
        int[] emojiMovingPointInt;
        Matrix matrix;
        for (int i = 0; i < numberOfEmojis; i++) {
            if (emojiBitmap[i] == null) {
                emojiBitmap[i] = getBitmapFromId(emojiId.get(i), emojiReactSide);
            }
            matrix = new Matrix();
            emojiMatrix.add(matrix);
        }

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
//                Log.i("point 510", emojiMovingPointFloat[0] + " " + emojiMovingPointFloat[1]+"i");
                emojiMovingPoint[i] = convertFloatArrayToIntArray(emojiMovingPointFloat, emojiMovingPoint[i]);
                emojiMatrix.set(i, new Matrix());
//                Log.i("point 506", emojiReactSide + " " + emojiBitmap[0].getHeight());

//                int scale = emojiReactSide / emojiBitmap[0].getHeight();
//                emojiMatrix.get(i).setScale(scale, scale);
                emojiMatrix.get(i).postTranslate(emojiMovingPoint[i][0] - emojiReactSide / 2, emojiMovingPoint[i][1] - emojiReactSide / 2);
                emojiMatrix.get(i).postRotate((20 - iCurStep) * (80 - 160 * (i + 1) / (numberOfEmojis + 1)) / 20, emojiMovingPoint[i][0], emojiMovingPoint[i][1]);
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
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0.4f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
//                Log.i("Point 240", "va" + valueAnimator.getAnimatedValue());
                emojiMatrix.get(clickedIndex).setScale((float) valueAnimator.getAnimatedValue(), (float) valueAnimator.getAnimatedValue());
                emojiMatrix.get(clickedIndex).postTranslate(emojiMovingPoint[clickedIndex][0] - (float) valueAnimator.getAnimatedValue() * emojiReactSide / 2, emojiMovingPoint[clickedIndex][1] - (float) valueAnimator.getAnimatedValue() * emojiReactSide / 2);
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
        animator.setDuration(600);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
    }

    private void startUnclickingAnim(final int clickedIndex) {
        circleAnimWorking = false;
        clickingAnimWorking = true;
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0.4f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                emojiMatrix.get(clickedIndex).setScale((float) valueAnimator.getAnimatedValue(), (float) valueAnimator.getAnimatedValue());
                emojiMatrix.get(clickedIndex).postTranslate(emojiMovingPoint[clickedIndex][0] - (float) valueAnimator.getAnimatedValue() * emojiReactSide / 2, emojiMovingPoint[clickedIndex][1] - (float) valueAnimator.getAnimatedValue() * emojiReactSide / 2);
                //                Log.i("Point 270", "va" + valueAnimator.getAnimatedValue());
                clickedRadius = (int) (50 * (float) valueAnimator.getAnimatedValue());
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
        animator.setDuration(600);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
    }

    private void emojiRisinginit() {
        emojiRising = true;
        for (int i = 0; i < numberOfRisers; i++) {
            Rect risingRect = calculateNewRect(new Rect(), new Random().nextInt(getWidth() + 1), getHeight() + new Random().nextInt(350), new Random().nextInt(25) + 20);
            risingEmojis.add(new RisingEmoji(risingRect, new Random().nextInt(4) + emojisRisingSpeed));
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
            }, 5, 60);
        }

    }

    private void riseEmoji() {
        vanished += new Random().nextInt(2);

        for (int i = 0; i < risingEmojis.size(); i++) {
            RisingEmoji re = risingEmojis.get(i);
            re.setRect(calculateNewRect(re.getRect(), re.getRect().centerX(), re.getRect().centerY() - re.getSpeed(), re.getRect().width() / 2));
            if (startDisappear || re.getRect().top <= emojisRisingHeight) {
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
            if (circleAnimWorking && clickedOnEmojiReact(i, event.getX(), event.getY())) {

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

    private boolean clickedOnEmojiReact(int i, double x, double y) {
        return (Math.abs(x - emojiMovingPoint[i][0]) < emojiReactSide / 2 && Math.abs(y - emojiMovingPoint[i][1]) < emojiReactSide / 2);
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

    private void initDimensions() {
        availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
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

    private Bitmap getBitmapFromId(int id, int side) {
        Log.i("point 696", "getBitmapFromId" + side);
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
