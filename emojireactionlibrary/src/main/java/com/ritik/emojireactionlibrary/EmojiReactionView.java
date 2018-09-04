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

/**
 * This class does all the background work related to displaying emoji on the canvas
 */

public class EmojiReactionView extends android.support.v7.widget.AppCompatImageView {

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
    // boolean to declare if a touch gesture was a swipe and started from an emoji on panel
    private boolean emojiClicked = false;

    /// Home emoji variables

    // Home emoji rect variable
    private Rect homeRect = new Rect();
    // Length of side of home rect
    private int homeSide = (int) (50 * densityFactor);
    // Home emoji center
    private int[] homeCenter = new int[]{(int) (30 * densityFactor), 0};
    // Raw home emoji center Y coordinate given by the user
    private float homeCenterYGiven = -1;
    // Home emoji bitmap
    private Bitmap homeBitmap;
    // Boolean to make home emoji visible
    private boolean homeEmojiVisible;

    //TODO: can be improved in landscape mode!!
    /// Circular Animation Variables

    // Panel center
    private int[] panelCentre = new int[2];
    // Raw panel center given by user
    private float[] panelCentreGiven = new float[]{-2, -2};
    // Panel radius variable
    private int panelRadius;
    // Raw panel radius variable given by user
    private int panelRadiusGiven = -1;
    // Emoji icon side length
    private int panelEmojiSide = (int) (50 * densityFactor);
    // Coordinates for emoji translation
    private int[][] emojiMovingPoint;
    // Variable used to measure the length of a path, and/or to find the position and tangent along it
    private PathMeasure[] pms;
    // Boolean checks whether animation is working or not
    private boolean panelAnimWorking;
    // Matrix is used emojis that are present in circular reveal as there is a rotating animation for them
    private Matrix[] emojiMatrix;
    int iCurStep = 20;// current step
    int iCurStepTaken = 0;// current steps total

    /// Variables for clicking and un-clicking gesture detection

    // Radius for defining clicked region radius
    private int clickedRadius;
    // Paint object for creating a white background around the emoji when clicked
    private Paint clickedPaint = new Paint();
    // Boolean to check that click animation is working or not
    private boolean clickingAnimWorking;

    /// RisingEmoji

    // speed per frame of the rising emojis
    private int emojisRisingSpeed = (int) (10 * densityFactor);
    // height of the rising emojis(to start disappearing)
    private int emojisRisingHeight;
    // raw height of the rising emojis(to start disappearing) given by user
    private float emojisRisingHeightGiven = -2;
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

//        Log.i("point 152", "attrs" );
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
            } else if (attr == R.styleable.EmojiReactionView_home_Center_X) {
                homeCenter[0] = arr.getDimensionPixelSize(attr, homeCenter[0]);

            } else if (attr == R.styleable.EmojiReactionView_home_Center_Y) {
                homeCenterYGiven = arr.getDimensionPixelSize(attr, -1);

            } else if (attr == R.styleable.EmojiReactionView_home_side) {
                homeSide = arr.getDimensionPixelSize(attr, homeSide);

            } else if (attr == R.styleable.EmojiReactionView_panel_center_X) {
                if (arr.peekValue(attr).type == TYPE_DIMENSION)
                    panelCentreGiven[0] = arr.getDimensionPixelSize(attr, -2);
                else {
                    panelCentreGiven[0] = checkFraction(arr.getFraction(attr, -1, -1, -2));
                }
            } else if (attr == R.styleable.EmojiReactionView_panel_center_Y) {
                if (arr.peekValue(attr).type == TYPE_DIMENSION)
                    panelCentreGiven[1] = arr.getDimensionPixelSize(attr, -2);
                else
                    panelCentreGiven[1] = checkFraction(arr.getFraction(attr, -1, -1, -2));
            } else if (attr == R.styleable.EmojiReactionView_panel_radius) {
                panelRadiusGiven = arr.getDimensionPixelSize(attr, -1);

            } else if (attr == R.styleable.EmojiReactionView_panel_emoji_side) {
                panelEmojiSide = arr.getDimensionPixelSize(attr, panelEmojiSide);

            } else if (attr == R.styleable.EmojiReactionView_emojis_rising_height) {
                emojisRisingHeightGiven = checkFraction(arr.getFraction(attr, -1, -1, -2));
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
        if (input == -2 || (input >= -1 && input <= 0)) {
            return input;
        } else throw new IllegalArgumentException();
    }

    public int[] getCentre() {
        return panelCentre;
    }

    public float getRadius() {
        return panelRadius;
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
            emojiBitmap[clickedEmojiNumber] = getBitmapFromId(emojiId.get(clickedEmojiNumber), panelEmojiSide);
        invalidate();
    }

    public int getNumberOfEmojis() {
        return numberOfEmojis;
    }

    public int getEmojisRisingSpeed() {
        return emojisRisingSpeed;
    }

    public Rect getHomeRect() {
        return homeRect;
    }

    public int getPanelEmojiSide() {
        return panelEmojiSide;
    }

    public Bitmap getHomeBitmap() {
        return homeBitmap;
    }

    public void setHomeBitmap(Bitmap homeBitmap) {
        this.homeBitmap = homeBitmap;
    }

    public boolean isHomeEmojiVisible() {
        return homeEmojiVisible;
    }

    public boolean isPanelAnimWorking() {
        return panelAnimWorking;
    }

    public boolean isClickingAnimWorking() {
        return clickingAnimWorking;
    }

    public boolean isEmojiRising() {
        return emojiRising;
    }

    public void setHomeEmojiVisible() {
        // Make the home emoji visible
        if (emojiRising) {
            emojiRising = false;
            if (emojiRisingTimer != null) {
                emojiRisingTimer.cancel();
                emojiRisingTimer.purge();
                emojiRisingTimer = null;
            }
            fading = 0;
            startFading = false;
            if (risingEmojis.size() != 0) risingEmojis.clear();
        }
        homeEmojiVisible = true;
        panelAnimWorking = false;
        clickingAnimWorking = false;
        setColorFilter(Color.rgb(255, 255, 255), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    public void setPanelAnimWorking() {
        // Start Circular animation

        if (emojiMatrix == null) return;

        if (emojiRising) {
            emojiRising = false;
            if (emojiRisingTimer != null) {
                emojiRisingTimer.cancel();
                emojiRisingTimer.purge();
                emojiRisingTimer = null;
            }
            fading = 0;
            startFading = false;
            if (risingEmojis.size() != 0) risingEmojis.clear();
        }

        panelAnimWorking = true;
        homeEmojiVisible = false;
        clickingAnimWorking = false;
        invalidate();
    }

    public void setOnEmojiClickListener(ClickInterface l) {
        // Set the listener to the clickedEmojiNumber
        this.mClickInterface = l;
    }

    private void init() {
//        Log.i("point 343", "init " + getWidth());

        // The paint which dims the background on circular animation
        clickedPaint.setColor(Color.argb(150, 185, 185, 185));

        // Get the Emoji to be displayed in Bitmap
        if (clickedEmojiNumber != -1) {
            emojiBitmap[clickedEmojiNumber] = getBitmapFromId(emojiId.get(clickedEmojiNumber), panelEmojiSide);
        }

        setup();
    }

    private void setup() {
//        Log.i("point 357", "setup" + getWidth() + " ");
        if (emojiId == null) {
            return;
        }
        if (numberOfEmojis == 0) {
            homeEmojiVisible = false;
            panelAnimWorking = false;
            clickingAnimWorking = false;
            emojiRising = false;
            return;
        }

        if (getWidth() == 0 && getHeight() == 0) {
            return;
        } else {
            smallerDimension = getHeight() > getWidth() ? getWidth() : getHeight();
        }

        // set emojisRisingHeight based on user data
        if (emojisRisingHeightGiven == -2) {
            emojisRisingHeight = getHeight() / 2;
        } else if (emojisRisingHeightGiven <= 0 && emojisRisingHeightGiven >= -1) {
            emojisRisingHeight = getHeight() - (int) (emojisRisingHeightGiven * getHeight());
        }
        setHomeRect();
        setPathPanel();

        homeEmojiVisible = true;
    }

    private void setHomeRect() {
        // Set the homeRect y coordinate based on user data
        if (homeCenterYGiven == -1) {
            homeCenter[1] = getHeight() - (int) (30 * densityFactor);
        } else {
            homeCenter[1] = (int) (getHeight() - homeCenterYGiven);
        }

        // Set the homeBitmap with the default Bitmap
        // use R.drawable.home to access or modify it
        if (homeBitmap == null)
            homeBitmap = getBitmapFromId(R.drawable.home, homeSide);
        // Set the home Rect
        homeRect = new Rect((homeCenter[0] - homeSide / 2), (homeCenter[1] - homeSide / 2), (homeCenter[0] + homeSide / 2), (homeCenter[1] + homeSide / 2));
    }

    private void setPathPanel() {
        // Set the coordinates for circular animation
        if (panelCentreGiven[0] == -2)
            panelCentre[0] = getWidth() / 2;
        else if (panelCentreGiven[0] >= -1 && panelCentreGiven[0] <= 0) {
            panelCentre[0] = 0 - (int) (panelCentreGiven[0] * getWidth());
        } else if (panelCentreGiven[0] >= 0) {
            panelCentre[0] = (int) (panelCentreGiven[0]);
        }
        if (panelCentreGiven[1] == -2)
            panelCentre[1] = getHeight() - panelEmojiSide / 2;
        else if (panelCentreGiven[1] >= -1 && panelCentreGiven[1] <= 0) {
            panelCentre[1] = getHeight() + (int) (panelCentreGiven[1] * getHeight());
        } else if (panelCentreGiven[1] >= 0) {
            panelCentre[1] = getHeight() - (int) (panelCentreGiven[1]);
        }
        //TODO: need to look somewhat flatter

        // Set the radius of circular animation
        if (panelRadiusGiven == -1) {
            panelRadius = (int) (smallerDimension / 2 - 20 * densityFactor);
        } else {
            panelRadius = smallerDimension / 2 > panelRadiusGiven ? panelRadiusGiven : smallerDimension / 2;
        }

        clickedRadius = (int) (panelEmojiSide * 0.65);
        // angle between successive emojis
        double angle = Math.PI / (numberOfEmojis + 1);

        // set up PathMeasure object between initial and final coordinates
        Path emojiPath1;
        pms = new PathMeasure[numberOfEmojis];
        for (int i = 1; i <= numberOfEmojis; i++) {
            emojiPath1 = new Path();
            emojiPath1.moveTo(panelCentre[0], panelCentre[1]);
            emojiPath1.lineTo((float) (panelCentre[0] + panelRadius * Math.cos(i * angle + Math.PI)), (float) (panelCentre[1] + panelRadius * Math.sin(i * angle + Math.PI)));
            pms[i - 1] = new PathMeasure(emojiPath1, false);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
//        Log.i("point 446", "draw");
        super.onDraw(canvas);

        if (homeEmojiVisible)
            // To make home emoji visible
            if (clickedEmojiNumber == -1)
                canvas.drawBitmap(homeBitmap, null, homeRect, null);
            else canvas.drawBitmap(emojiBitmap[clickedEmojiNumber], null, homeRect, null);

        if (panelAnimWorking) {
            // to start circular animation
            // to display clicked circle on clicked emoji
            if (clickedEmojiNumber != -1) {
                canvas.drawCircle(emojiMovingPoint[clickedEmojiNumber][0], emojiMovingPoint[clickedEmojiNumber][1], clickedRadius, clickedPaint);
            }
            for (int i = 0; i < numberOfEmojis; i++) {
                canvas.drawBitmap(emojiBitmap[i], emojiMatrix[i], null);
            }
            startPanelAnim();
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

    private void startPanelAnim() {
        // start circular animation

        // float array to store new coordinates
        float[] emojiMovingPointFloat = new float[2];

        // initialise matrix array
        if (emojiMatrix == null) {
            emojiMatrix = new Matrix[numberOfEmojis];
        }

        // get emojiBitmap from their ids
        for (int i = 0; i < numberOfEmojis; i++) {
            if (emojiBitmap[i] == null) {
                emojiBitmap[i] = getBitmapFromId(emojiId.get(i), panelEmojiSide);
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
//                int scale = panelEmojiSide / emojiBitmap[0].getHeight();
//                emojiMatrix.get(i).setScale(scale, scale);
                // move bitmap to the coordinate
                emojiMatrix[i].postTranslate(emojiMovingPoint[i][0] - panelEmojiSide / 2, emojiMovingPoint[i][1] - panelEmojiSide / 2);
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

        panelAnimWorking = false;
        clickingAnimWorking = true;
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0.4f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                // reduce clicked emoji by reducing its scale and translating it to the required position
                emojiMatrix[clickedIndex].setScale((float) valueAnimator.getAnimatedValue(), (float) valueAnimator.getAnimatedValue());
                emojiMatrix[clickedIndex].postTranslate(emojiMovingPoint[clickedIndex][0] - (float) valueAnimator.getAnimatedValue() * panelEmojiSide / 2, emojiMovingPoint[clickedIndex][1] - (float) valueAnimator.getAnimatedValue() * panelEmojiSide / 2);
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
        animator.setDuration(500);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
    }

    private void startUnclickingAnim(final int clickedIndex) {
        // start unclicking animation

        panelAnimWorking = false;
        clickingAnimWorking = true;
        ValueAnimator animator = ValueAnimator.ofFloat(1, 0.4f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                // reduce clicked emoji by reducing its scale and translating it to the required position
                emojiMatrix[clickedIndex].setScale((float) valueAnimator.getAnimatedValue(), (float) valueAnimator.getAnimatedValue());
                emojiMatrix[clickedIndex].postTranslate(emojiMovingPoint[clickedIndex][0] - (float) valueAnimator.getAnimatedValue() * panelEmojiSide / 2, emojiMovingPoint[clickedIndex][1] - (float) valueAnimator.getAnimatedValue() * panelEmojiSide / 2);

                // reduce the size of background panel radius
                clickedRadius = (int) (panelEmojiSide * (float) valueAnimator.getAnimatedValue() * 0.65);
                invalidate();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                clickingAnimWorking = false;
                clickedEmojiNumber = -1;
                homeEmojiVisible = true;
                // restore background dimness
                setColorFilter(Color.rgb(255, 255, 255), android.graphics.PorterDuff.Mode.MULTIPLY);
                clickedRadius = (int) (panelEmojiSide * 0.65);
            }
        });
        animator.setDuration(500);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
    }

    private void emojiRisinginit() {
        // set up rising emojis

        // directly invalidate if numberOfRisers<=0
        if (numberOfRisers <= 0) {
            homeEmojiVisible = true;
            invalidate();
            return;
        }

        emojiRising = true;

        //set up emojis with random behaviour at random position
        for (int i = 0; i < numberOfRisers; i++) {
            Rect risingRect = calculateNewRect(new Rect(), new Random().nextInt(getWidth() + 1), getHeight() + new Random().nextInt(2 * getHeight() / 5), new Random().nextInt(panelEmojiSide / 4) + panelEmojiSide / 3);
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

        if (startFading)
            fading += new Random().nextInt(numberOfRisers / 5);
        // currently index to which emojis will be faded
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
                homeEmojiVisible = true;
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

//        Log.i("point 709", "onTouchEvent " + event.getAction() + wasSwiping + emojiClicked );
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            // prepare for new gesture
            wasSwiping = false;
            emojiClicked = false;
            if (homeEmojiVisible && homeRect.contains((int) event.getX(), (int) event.getY())) {
                emojiClicked = true;
            } else if (panelAnimWorking) {
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
            super.onTouchEvent(event);
            return true;
        } else if (!wasSwiping && event.getAction() == MotionEvent.ACTION_MOVE) {
            // swiping gesture detected
            wasSwiping = true;

        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            if (wasSwiping && !emojiClicked) {
                // gesture ends with homeEmoji visible
                panelAnimWorking = false;
                homeEmojiVisible = true;
                setColorFilter(Color.rgb(255, 255, 255), android.graphics.PorterDuff.Mode.MULTIPLY);
            } else {
                // time to respond to clicks on emojiRect
                if (panelAnimWorking) {
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
                    if (!wasSwiping) {
                        // clicked on background

                        // restore background dimness
                        panelAnimWorking = false;
                        homeEmojiVisible = true;
                        setColorFilter(Color.rgb(255, 255, 255), android.graphics.PorterDuff.Mode.MULTIPLY);
                    }
                } else if (homeEmojiVisible && homeRect.contains((int) event.getX(), (int) event.getY())) {
                    // detect if clicked on homeEmoji
                    homeEmojiVisible = false;
                    panelAnimWorking = true;
                    startPanelAnim();
                    return false;
                }
                return super.onTouchEvent(event);

            }
        }
        return super.onTouchEvent(event);
    }

    private boolean clickedOnEmojiReact(int i, double x, double y) {
        return (Math.abs(x - emojiMovingPoint[i][0]) < panelEmojiSide / 2 && Math.abs(y - emojiMovingPoint[i][1]) < panelEmojiSide / 2);
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
