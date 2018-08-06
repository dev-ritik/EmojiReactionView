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
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.util.TypedValue.TYPE_DIMENSION;

/**
 * This class does all the background work related to displaying emoji on the canvas
 */

public class EmojiReactionView extends AppCompatImageView {

    /// General variables

    // Click interface for emoji click
    private ClickInterface mClickInterface;
    // Array of emoji bitmaps
    private Bitmap[] emojiBitmap;
    // Index of the selected bitmap
    private int clickedEmojiNumber = -1;
    // Total number of emoji bitmaps
    private int numberOfEmojis = 0;
    // ArrayList containing all emoji ids to be displayed
    private ArrayList<Integer> emojiId = new ArrayList<>();
    // Activity context
    private Context context;
    // Density factor
    private final float densityFactor = getResources().getDisplayMetrics().density;
    // Variable for getting the dimension height or width, whichever is smaller
    private int smallerDimension;
    // boolean to declare if a touch gesture was a swipe
    private boolean wasSwiping = false;
    // boolean to declare if a touch gesture was a swipe and started from an emoji on circle
    private boolean emojiClicked = false;

    /// Cover emoji variables

    // Cover emoji rect variable
    private Rect coverRect = new Rect();
    // Length of side of cover rect
    private int coverSide = (int) (50 * densityFactor);
    // Cover emoji center
    private int[] coverCenter = new int[]{(int) (30 * densityFactor), 0};
    // Raw cover emoji center Y coordinate given by the user
    private float coverCenterYGiven = -1;
    // Cover emoji bitmap
    private Bitmap coverBitmap;
    // Boolean to make cover emoji visible
    private boolean coverEmojiVisible;

    //TODO: can be improved in landscape mode!!
    /// Circular Animation Variables

    // Circle center for creating circle
    private int[] circleCentre = new int[2];
    // Raw circle center given by user creating circle
    private float[] circleCentreGiven = new float[]{-1, -1};
    // Circle radius variable
    private int circleRadius;
    // Raw circle radius variable given by user
    private int circleRadiusGiven = -1;
    // Emoji icon side length
    private int emojiReactSide = (int) (50 * densityFactor);
    // Coordinates for emoji translation
    private int[][] emojiMovingPoint;
    // Variable used to measure the length of a path, and/or to find the position and tangent along it
    private PathMeasure[] pms;
    // Boolean checks whether animation is working or not
    private boolean circleAnimWorking;
    // Matrix is used emojis that are present in circular reveal as there is a rotating animation for them
    private Matrix[] emojiMatrix;

    /// Variables for clicking and un-clicking gesture detection

    // Radius for defining clicked region radius
    private int clickedRadius;
    // Paint object for creating a white background around the emoji when clicked
    private Paint clickedPaint = new Paint();
    int iCurStep = 20;// current step
    int iCurStepTaken = 0;// current steps total
    // Boolean to check that click animation is working or not
    private boolean clickingAnimWorking;

    /// RisingEmoji

    // speed per frame of the rising emojis
    private int emojisRisingSpeed = (int) (10 * densityFactor);
    // height of the rising emojis(to start disappearing)
    private int emojisRisingHeight;
    // raw height of the rising emojis(to start disappearing) given by user
    private float emojisRisingHeightGiven = -1;
    // Arraylist storing properties of rising emojis
    private ArrayList<RisingEmoji> risingEmojis = new ArrayList<>();
    // Total number of emojis rising
    private int numberOfRisers = 24;
    // Boolean to check if emojis are rising
    private boolean emojiRising;
    // Timer to rise emojis continuously
    private Timer emojiRisingTimer;
    // Variable to count number of emojis started disappearing
    private int fading = 0;
    // is the emojis started disappearing
    private boolean startFading = false;

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
        // extract data from attributes
        this.initBaseXMLAttrs(context, attrs);

        init();
    }

    final void initBaseXMLAttrs(Context context, AttributeSet attrs) {
        final TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.EmojiReactionView);

        Log.i("point mi88", "attrs" + densityFactor);
        final int N = arr.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = arr.getIndex(i);

            if (attr == R.styleable.EmojiReactionView_emojis) {
                final TypedArray resourceArray = context.getResources().obtainTypedArray(arr.getResourceId(R.styleable.EmojiReactionView_emojis, 0));
                // Get emojis id from attribute and store it in arraylist
                for (int j = 0; j < resourceArray.length(); j++) {
                    emojiId.add(resourceArray.getResourceId(j, 0));
                }
                numberOfEmojis = emojiId.size();
                resourceArray.recycle();

            } else if (attr == R.styleable.EmojiReactionView_set_emoji) {
                clickedEmojiNumber = arr.getInt(attr, clickedEmojiNumber);
            } else if (attr == R.styleable.EmojiReactionView_cover_Center_X) {
                coverCenter[0] = arr.getDimensionPixelSize(attr, coverCenter[0]);

            } else if (attr == R.styleable.EmojiReactionView_cover_Center_Y) {
                coverCenterYGiven = arr.getDimensionPixelSize(attr, -1);

            } else if (attr == R.styleable.EmojiReactionView_cover_side) {
                coverSide = arr.getDimensionPixelSize(attr, coverSide);

            } else if (attr == R.styleable.EmojiReactionView_circle_center_X) {
                if (arr.peekValue(attr).type == TYPE_DIMENSION)
                    circleCentreGiven[0] = arr.getDimensionPixelSize(attr, -1);
                else {
                    circleCentreGiven[0] = checkFraction(arr.getFraction(attr, 1, 1, -1));
                }
            } else if (attr == R.styleable.EmojiReactionView_circle_center_Y) {
                if (arr.peekValue(attr).type == TYPE_DIMENSION)
                    circleCentreGiven[1] = arr.getDimensionPixelSize(attr, -1);
                else
                    circleCentreGiven[1] = checkFraction(arr.getFraction(attr, 1, 1, -1));
            } else if (attr == R.styleable.EmojiReactionView_circle_radius) {
                circleRadiusGiven = arr.getDimensionPixelSize(attr, -1);

            } else if (attr == R.styleable.EmojiReactionView_emoji_react_side) {
                emojiReactSide = arr.getDimensionPixelSize(attr, emojiReactSide);

            } else if (attr == R.styleable.EmojiReactionView_emojis_rising_height) {
                emojisRisingHeightGiven = checkFraction(arr.getFraction(attr, 1, 1, -1));
            } else if (attr == R.styleable.EmojiReactionView_emojis_rising_speed) {
                emojisRisingSpeed = arr.getDimensionPixelSize(attr, -1);

            } else if (attr == R.styleable.EmojiReactionView_emojis_rising_number) {
                numberOfRisers = arr.getInt(attr, numberOfRisers);
            }
        }
        if (clickedEmojiNumber >= numberOfEmojis || clickedEmojiNumber < -1) {
            throw new IllegalArgumentException("set_emoji can't be more than number of emojis!");
        }

        arr.recycle();
        numberOfEmojis = emojiId.size();
        emojiBitmap = new Bitmap[numberOfEmojis];

    }

    private float checkFraction(float input) {
        // Check that percents entered is within [0% 100%]
        if (input == -1 || (input <= 1 && input >= 0)) {
            return input;
        } else throw new IllegalArgumentException();
    }

    public int[] getCentre() {
        return circleCentre;
    }

    public float getRadius() {
        return circleRadius;
    }

    public int getClickedEmojiNumber() {
        return clickedEmojiNumber;
    }

    public void setClickedEmojiNumber(int clickedEmojiNumber) {
        // Set the currently selected emoji with immediate effect
        if (mClickInterface != null)

            if (clickedEmojiNumber == -1) {
                mClickInterface.onEmojiUnclicked(clickedEmojiNumber, -1, -1);
            } else if (this.clickedEmojiNumber == clickedEmojiNumber) {
                mClickInterface.onEmojiUnclicked(this.clickedEmojiNumber, -1, -1);

            } else if (clickedEmojiNumber >= 0 && clickedEmojiNumber < numberOfEmojis) {
                mClickInterface.onEmojiUnclicked(this.clickedEmojiNumber, -1, -1);
                mClickInterface.onEmojiClicked(clickedEmojiNumber, -1, -1);
            } else throw new IllegalArgumentException("clickedEmojiNumber out of range");

        this.clickedEmojiNumber = clickedEmojiNumber;
        // Get the Emoji to be displayed in Bitmap
        if (emojiBitmap == null) emojiBitmap = new Bitmap[numberOfEmojis];
        if (clickedEmojiNumber != -1 && emojiBitmap[clickedEmojiNumber] == null)
            emojiBitmap[clickedEmojiNumber] = getBitmapFromId(emojiId.get(clickedEmojiNumber), emojiReactSide);
        invalidate();
    }

    public int getNumberOfEmojis() {
        return numberOfEmojis;
    }

    public int getEmojisRisingSpeed() {
        return emojisRisingSpeed;
    }

    public Rect getCoverRect() {
        return coverRect;
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

    public boolean isCircleAnimWorking() {
        return circleAnimWorking;
    }

    public boolean isClickingAnimWorking() {
        return clickingAnimWorking;
    }

    public boolean isEmojiRising() {
        return emojiRising;
    }

    public void setCoverEmojiVisible() {
        // Make the cover emoji visible
        coverEmojiVisible = true;
        circleAnimWorking = false;
        clickingAnimWorking = false;
        emojiRising = false;
        setColorFilter(Color.rgb(255, 255, 255), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    public void setCircleAnimWorking() {
        // Start Circular animation
        circleAnimWorking = true;
        coverEmojiVisible = false;
        clickingAnimWorking = false;
        emojiRising = false;
        invalidate();
    }

    public void setOnEmojiClickListener(@Nullable ClickInterface l) {
        // Set the listener to the clickedEmojiNumber
        this.mClickInterface = l;
    }

    private void init() {
        Log.i("point mi73", "init" + densityFactor + " " + getWidth());

        // The paint which dims the background on circular animation
        clickedPaint.setColor(Color.argb(150, 185, 185, 185));

        // Get the Emoji to be displayed in Bitmap
        if (clickedEmojiNumber != -1) {
            emojiBitmap[clickedEmojiNumber] = getBitmapFromId(emojiId.get(clickedEmojiNumber), emojiReactSide);
        }

        setup();
    }

    private void setup() {
        Log.i("point 158", "setup" + getWidth() + " " + densityFactor);
        if (emojiId == null) {
            return;
        }
        if (numberOfEmojis == 0) {
            coverEmojiVisible = false;
            circleAnimWorking = false;
            clickingAnimWorking = false;
            emojiRising = false;
            return;
        }

        if (getWidth() == 0 && getHeight() == 0) {
            return;
        } else {
            smallerDimension = getHeight() > getWidth() ? getWidth() : getHeight();
        }
        Log.i("point mi273", "here" + getHeight() + " " + densityFactor);

        // set emojisRisingHeight based on user data
        if (emojisRisingHeightGiven == -1) {
            emojisRisingHeight = getHeight() / 2;
        } else if (emojisRisingHeightGiven >= 0 && emojisRisingHeightGiven <= 1) {
            emojisRisingHeight = (int) (emojisRisingHeightGiven * getHeight());
        }
        setCoverRect();
        setPathCircle();

        coverEmojiVisible = true;
    }

    private void setCoverRect() {
        // Set the coverRect y coordinate based on user data
        if (coverCenterYGiven == -1) {
            coverCenter[1] = getHeight() - (int) (30 * densityFactor);
        } else {
            coverCenter[1] = (int) (getHeight() - coverCenterYGiven);
        }

        // Set the coverBitmap with the default Bitmap
        // use R.drawable.cover to access or modify it
        coverBitmap = getBitmapFromId(R.drawable.cover, coverSide);
        // Set the cover Rect
        coverRect = new Rect((coverCenter[0] - coverSide / 2), (coverCenter[1] - coverSide / 2), (coverCenter[0] + coverSide / 2), (coverCenter[1] + coverSide / 2));
    }

    private void setPathCircle() {
        // Set the coordinates for circular animation
        if (circleCentreGiven[0] == -1)
            circleCentre[0] = getWidth() / 2;
        else if (circleCentreGiven[0] <= 1 && circleCentreGiven[0] >= 0) {
            circleCentre[0] = (int) (circleCentreGiven[0] * getWidth());
        }
        if (circleCentreGiven[1] == -1 )
            circleCentre[1] = getHeight() - emojiReactSide / 2;
        else if (circleCentreGiven[1] <= 1 && circleCentreGiven[1] >= 0) {
            circleCentre[1] = (int) (circleCentreGiven[1] * getHeight());
        }
        //TODO: need to look somewhat flatter

        // Set the radius of circular animation
        if (circleRadiusGiven == -1) {
            circleRadius = (int) (smallerDimension / 2 - 20 * densityFactor);
        } else {
            circleRadius = smallerDimension / 2 > circleRadiusGiven ? circleRadiusGiven : smallerDimension / 2;
        }

        clickedRadius = (int) (emojiReactSide * 0.7);
        // angle between successive emojis
        double angle = Math.PI / (numberOfEmojis + 1);

        // set up PathMeasure object between initial and final coordinates
        Path emojiPath1;
        pms = new PathMeasure[numberOfEmojis];
        for (int i = 1; i <= numberOfEmojis; i++) {
            emojiPath1 = new Path();
            emojiPath1.moveTo(circleCentre[0], circleCentre[1]);
            emojiPath1.lineTo((float) (circleCentre[0] + circleRadius * Math.cos(i * angle + Math.PI)), (float) (circleCentre[1] + circleRadius * Math.sin(i * angle + Math.PI)));
            pms[i - 1] = new PathMeasure(emojiPath1, false);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
//        Log.i("point mi113", "draw");
        super.onDraw(canvas);

        if (coverEmojiVisible)
            // To make cover emoji visible
            if (clickedEmojiNumber == -1)
                canvas.drawBitmap(coverBitmap, null, coverRect, null);
            else canvas.drawBitmap(emojiBitmap[clickedEmojiNumber], null, coverRect, null);

        if (circleAnimWorking) {
            // to start circular animation
            // to display clicked circle on clicked emoji
            if (clickedEmojiNumber != -1) {
                canvas.drawCircle(emojiMovingPoint[clickedEmojiNumber][0], emojiMovingPoint[clickedEmojiNumber][1], clickedRadius, clickedPaint);
            }
            for (int i = 0; i < numberOfEmojis; i++) {
                canvas.drawBitmap(emojiBitmap[i], emojiMatrix[i], null);
            }
            startCircleAnim();
        }

        if (clickingAnimWorking) {
            // to start clicking animation
            // Display clicked circle
            if (clickedEmojiNumber != -1) {
                canvas.drawCircle(emojiMovingPoint[clickedEmojiNumber][0], emojiMovingPoint[clickedEmojiNumber][1], clickedRadius, clickedPaint);
            }
            for (int i = 0; i < numberOfEmojis; i++) {
                canvas.drawBitmap(emojiBitmap[i], emojiMatrix[i], null);
            }
        }

        if (emojiRising) {
            // display rising emojis at their new position
            for (RisingEmoji re : risingEmojis) {
                canvas.drawBitmap(emojiBitmap[clickedEmojiNumber], null, re.getRect(), re.getPaint());
            }
        }
    }

    public void test() {
    }

    private void startCircleAnim() {
        // start circular animation

        // float array to store new coordinates
        float[] emojiMovingPointFloat = new float[2];

        // initialise matrix array
        if (emojiMatrix == null) {
            emojiMatrix = new Matrix[numberOfEmojis];
        }

        // get emojibitmap from their ids
        for (int i = 0; i < numberOfEmojis; i++) {
            if (emojiBitmap[i] == null) {
                emojiBitmap[i] = getBitmapFromId(emojiId.get(i), emojiReactSide);
            }
        }

        // initialise array to hold emoji coordinates
        if (emojiMovingPoint == null) {
            emojiMovingPoint = new int[numberOfEmojis][2];
            for (int i = 0; i < numberOfEmojis; i++) {
                emojiMovingPoint[i] = new int[2];
            }
        }

        float fSegmentLen = pms[0].getLength() / 210;//210 animation steps

        if (iCurStep >= 0) {
            for (int i = 0; i < numberOfEmojis; i++) {
                pms[i].getPosTan(fSegmentLen * iCurStepTaken, emojiMovingPointFloat, null);
                emojiMovingPoint[i] = convertFloatArrayToIntArray(emojiMovingPointFloat, emojiMovingPoint[i]);
                emojiMatrix[i] = new Matrix();
//                int scale = emojiReactSide / emojiBitmap[0].getHeight();
//                emojiMatrix.get(i).setScale(scale, scale);
                // move bitmap to the coordinate
                emojiMatrix[i].postTranslate(emojiMovingPoint[i][0] - emojiReactSide / 2, emojiMovingPoint[i][1] - emojiReactSide / 2);
                // rotate it with required effect
                emojiMatrix[i].postRotate((iCurStep) * (75 - 150 * (i + 1) / (numberOfEmojis + 1)) / 20, emojiMovingPoint[i][0], emojiMovingPoint[i][1]);
            }
            // to get deceleration effect
            iCurStepTaken += iCurStep;
            iCurStep--;
            // gradually dim background
            setColorFilter(Color.rgb(255 - 6 * (20 - iCurStep), 255 - 6 * (20 - iCurStep), 255 - 6 * (20 - iCurStep)), android.graphics.PorterDuff.Mode.MULTIPLY);
        } else {
            iCurStep = 20;
            iCurStepTaken = 0;
        }
    }

    private int[] convertFloatArrayToIntArray(float[] emojiMovingPointFloat, int[] ints) {
        ints[0] = (int) emojiMovingPointFloat[0];
        ints[1] = (int) emojiMovingPointFloat[1];
        return ints;
    }

    private void startClickingAnim(final int clickedIndex) {
        // start clicking animation

        circleAnimWorking = false;
        clickingAnimWorking = true;
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0.4f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                // reduce clicked emoji by reducing its scale and translating it to the required position
                emojiMatrix[clickedIndex].setScale((float) valueAnimator.getAnimatedValue(), (float) valueAnimator.getAnimatedValue());
                emojiMatrix[clickedIndex].postTranslate(emojiMovingPoint[clickedIndex][0] - (float) valueAnimator.getAnimatedValue() * emojiReactSide / 2, emojiMovingPoint[clickedIndex][1] - (float) valueAnimator.getAnimatedValue() * emojiReactSide / 2);
                invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // restore background dimness
                setColorFilter(Color.rgb(255, 255, 255), android.graphics.PorterDuff.Mode.MULTIPLY);
                clickingAnimWorking = false;
                clickedEmojiNumber = clickedIndex;

                // start emoji rising animation
                emojiRisinginit();
            }
        });
        animator.setDuration(600);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
    }

    private void startUnclickingAnim(final int clickedIndex) {
        // start unclicking animation

        circleAnimWorking = false;
        clickingAnimWorking = true;
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0.4f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                // reduce clicked emoji by reducing its scale and translating it to the required position
                emojiMatrix[clickedIndex].setScale((float) valueAnimator.getAnimatedValue(), (float) valueAnimator.getAnimatedValue());
                emojiMatrix[clickedIndex].postTranslate(emojiMovingPoint[clickedIndex][0] - (float) valueAnimator.getAnimatedValue() * emojiReactSide / 2, emojiMovingPoint[clickedIndex][1] - (float) valueAnimator.getAnimatedValue() * emojiReactSide / 2);

                // reduce the size of background circle radius
                clickedRadius = (int) (emojiReactSide * (float) valueAnimator.getAnimatedValue() * 0.7);
                invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                clickingAnimWorking = false;
                clickedEmojiNumber = -1;
                coverEmojiVisible = true;
                // restore background dimness
                setColorFilter(Color.rgb(255, 255, 255), android.graphics.PorterDuff.Mode.MULTIPLY);
                clickedRadius = (int) (emojiReactSide * 0.7);
            }
        });
        animator.setDuration(600);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
    }

    private void emojiRisinginit() {
        // set up rising emojis

        // directly invalidate if numberOfRisers<=0
        if (numberOfRisers <= 0) {
            coverEmojiVisible = true;
            invalidate();
            return;
        }

        emojiRising = true;

        //set up emojis with random behaviour at random position
        for (int i = 0; i < numberOfRisers; i++) {
            Rect risingRect = calculateNewRect(new Rect(), new Random().nextInt(getWidth() + 1), getHeight() + new Random().nextInt(2 * getHeight() / 5), new Random().nextInt(emojiReactSide / 4) + emojiReactSide / 3);
            risingEmojis.add(new RisingEmoji(risingRect, new Random().nextInt(6) + emojisRisingSpeed));
        }
        // start animation
        emojiRisingAnim();
    }

    private void emojiRisingAnim() {
        // timer to continuously rise emojis

        if (emojiRisingTimer == null) {
            emojiRisingTimer = new Timer();
            emojiRisingTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    // calculate new coordinates in non ui thread
                    riseEmoji();
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // display the emojis at their new position
                            invalidate();
                        }
                    });
                }
            }, 5, 40);
        }

    }

    private void riseEmoji() {
        // calculate new coordinates

        fading += new Random().nextInt(2);
        // currently index to which emojis will be faded
// TODO change above position
        for (int i = 0; i < risingEmojis.size(); i++) {
            RisingEmoji re = risingEmojis.get(i);
            // update rects to their new positions
            re.setRect(calculateNewRect(re.getRect(), re.getRect().centerX(), re.getRect().centerY() - re.getSpeed(), re.getRect().width() / 2));
            
            //if any of the emoji crossed the threshold height, start fading emojis
            if (startFading || re.getRect().top <= emojisRisingHeight) {
                startFading = true;
                if (fading > risingEmojis.size()) fading = risingEmojis.size();
                if (re.getPaint() == null) re.setPaint(new Paint());
                
                // if emojis index is smaller than currently fading index
                if (i <= fading) {
                    // fade
                    re.getPaint().setAlpha(re.getPaint().getAlpha() / 2);
                    // increase size
                    re.setRect(calculateNewRect(re.getRect(), re.getRect().centerX(), re.getRect().centerY(), (int) (re.getRect().width() * 0.55)));
                }

                // remove if size almost completely faded
                if (re.getPaint().getAlpha() < 10) {
                    risingEmojis.remove(risingEmojis.get(i));
                    i--;
                }

            }

            // stop animation if every emoji has faded
            if (risingEmojis.size() == 0) {
                emojiRising = false;
                if (emojiRisingTimer != null) {
                    emojiRisingTimer.cancel();
                    emojiRisingTimer.purge();
                    emojiRisingTimer = null;
                }
                coverEmojiVisible = true;
                fading = 0;
                startFading = false;
                break;
            }
        }
    }
    //TODO set emoji before anim starts
    //TODO hover over emojis effect

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // respond to touch events

        Log.i("point 596", "onTouchEvent " + event.getAction() + wasSwiping + emojiClicked + ((!wasSwiping || emojiClicked) && (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)));
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // prepare for new gesture
            wasSwiping = false;
            emojiClicked = false;
            if (circleAnimWorking) {
                for (int i = numberOfEmojis - 1; i >= 0; i--) {
                    if (clickedOnEmojiReact(i, event.getX(), event.getY())) {
                        emojiClicked = true;
                        break;
                    }
                }
                if (clickedEmojiNumber != -1 && clickedOnRing(event.getX(), event.getY(), clickedEmojiNumber)) {
                    emojiClicked = true;
                }
            }
            return super.onTouchEvent(event);
        } else if (!wasSwiping && event.getAction() == MotionEvent.ACTION_MOVE) {
            // swiping gesture detected
            wasSwiping = true;

        } else if (wasSwiping && !emojiClicked && (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)) {
            // gesture ends with coverEmoji visible
            circleAnimWorking = false;
            coverEmojiVisible = true;
            setColorFilter(Color.rgb(255, 255, 255), android.graphics.PorterDuff.Mode.MULTIPLY);

        } else if ((!wasSwiping || emojiClicked) && (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)) {
            // time to respond to clicks on emojiRect
            if (circleAnimWorking) {
                // detect if clicked on EmojiReact
                for (int i = numberOfEmojis - 1; i >= 0; i--) {
                    if (clickedOnEmojiReact(i, event.getX(), event.getY())) {

                        if (clickedEmojiNumber == i) {
                            if (mClickInterface != null)
                                mClickInterface.onEmojiUnclicked(i, (int) event.getX(), (int) event.getY());
                            startUnclickingAnim(clickedEmojiNumber);
                            return false;
                        } else if (clickedEmojiNumber != -1) {
                            if (mClickInterface != null)
                                mClickInterface.onEmojiUnclicked(clickedEmojiNumber, (int) event.getX(), (int) event.getY());
                        }

                        if (mClickInterface != null)
                            mClickInterface.onEmojiClicked(i, (int) event.getX(), (int) event.getY());
                        startClickingAnim(i);
                        return false;
                    }
                }
                // detect if clicked on circle surrounding clicked Emoji
                if (clickedEmojiNumber != -1 && clickedOnRing(event.getX(), event.getY(), clickedEmojiNumber)) {
                    if (mClickInterface != null)
                        mClickInterface.onEmojiUnclicked(clickedEmojiNumber, (int) event.getX(), (int) event.getY());
                    startUnclickingAnim(clickedEmojiNumber);
                    return false;
                }

                // restore background dimness
                circleAnimWorking = false;
                coverEmojiVisible = true;
                setColorFilter(Color.rgb(255, 255, 255), android.graphics.PorterDuff.Mode.MULTIPLY);

            } else if (coverEmojiVisible && coverRect.contains((int) event.getX(), (int) event.getY())) {
                // detect if clicked on coverEmoji
                coverEmojiVisible = false;
                circleAnimWorking = true;
                startCircleAnim();
                return false;
            }
            return super.onTouchEvent(event);

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

    private Bitmap getBitmapFromId(int id, int side) {
        // generate bitmap from id of defined size
        if (side <= 0)
            throw new IllegalArgumentException("Emoji can't have 0 or negative side");

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
        // return rect with center x,y and side of length 2*halfside

        initialRect.left = x - halfSide;
        initialRect.right = x + halfSide;
        initialRect.top = y - halfSide;
        initialRect.bottom = y + halfSide;
        return initialRect;
    }
}
