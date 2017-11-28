package com.universio.humack.synergology;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.universio.humack.R;
import com.universio.humack.Settings;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Cyril Humbertclaude on 31/12/2015.
 */
public class BodyImageView extends SubsamplingScaleImageView {
    private long downFeedbackDuration, downFeedbackFadeout, clickFeedbackMinDuration, clickFeedbackDuration, clickFeedbackFadeout;
    private float downFeedbackRadius, downFeedbackDotRadius, clickFeedbackCorner;
    private ArrayList<Feedback> feedbacks;
    private Paint feedbackPaint, downDotPaint;
    private RectF clickFeedbackRect;

    public BodyImageView(Context context, AttributeSet attr) {
        super(context, attr);

        Resources resources = getResources();

        feedbacks = new ArrayList<>();

        downFeedbackDuration = resources.getInteger(R.integer.feedback_down_duration);
        downFeedbackFadeout = resources.getInteger(R.integer.feedback_down_fadeout);
        downFeedbackRadius = resources.getDimension(R.dimen.feedback_down_radius);
        downFeedbackDotRadius = resources.getDimension(R.dimen.feedback_down_dot_radius);

        clickFeedbackMinDuration = resources.getInteger(R.integer.feedback_click_minduration);
        clickFeedbackFadeout = resources.getInteger(R.integer.feedback_click_fadeout);
        clickFeedbackCorner = resources.getDimension(R.dimen.feedback_click_corner);
        clickFeedbackRect = new RectF();

        feedbackPaint = new Paint();
        feedbackPaint.setColor(resources.getColor(R.color.accent_color));
        downDotPaint = new Paint();
        downDotPaint.setColor(resources.getColor(R.color.dark_light));
    }

    public BodyImageView(Context context) {
        this(context, null);
    }

    public void addDownFeedback(PointF sourceCoord){
        feedbacks.add(new DownFeedback(sourceCoord));
        invalidate();
    }

    public void addClickFeedback(Rect rectangle){
        feedbacks.add(new ClickFeedback(rectangle));
        invalidate();
    }

    public void removeFeedbacks(){
        for(Feedback feedback : feedbacks)
            if(!feedback.isRemoved())
                feedback.remove();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!feedbacks.isEmpty() && isReady()){
            //Duration
            clickFeedbackDuration = Math.max(clickFeedbackMinDuration, Settings.getAnimationSpeed() - clickFeedbackFadeout);

            //Feedback for every down and click
            Iterator<Feedback> it = feedbacks.iterator();
            while (it.hasNext()) {
                //Le feedback
                Feedback feedback = it.next();

                //Stay then remove
                if(!feedback.isRemoved()) {
                    boolean toRemove = false;
                    long stayTime = System.currentTimeMillis() - feedback.getTimeCreated();
                    if(feedback instanceof DownFeedback){
                        if (stayTime > downFeedbackDuration)
                            toRemove = true;
                    }else if(feedback instanceof ClickFeedback){
                        if (stayTime > clickFeedbackDuration)
                            toRemove = true;
                    }else
                        toRemove = true;

                    if(toRemove)
                        feedback.remove();
                }

                //Progression
                float fadeoutProgress;
                if(!feedback.isRemoved())//Stay
                    fadeoutProgress = 0;
                else{//Fadeout
                    //Temps déjà animé
                    long timeElapsed = System.currentTimeMillis() - feedback.getTimeRemoved();
                    //Progession relatively to duration
                    if(feedback instanceof DownFeedback)
                        fadeoutProgress = timeElapsed * 100 / downFeedbackFadeout;
                    else if(feedback instanceof ClickFeedback)
                        fadeoutProgress = timeElapsed * 100 / clickFeedbackFadeout;
                    else
                        fadeoutProgress = 100;
                }
                if (fadeoutProgress >= 100) {
                    //Delete completed feedback
                    it.remove();
                    feedback = null;
                }
                //Draw the feedback
                if(feedback != null) {
                    //Alpha décroissant
                    int alpha = Math.round((100 - Math.min(100, fadeoutProgress)) * 150 / 100);
                    feedbackPaint.setAlpha(alpha);

                    if(feedback instanceof DownFeedback){
                        //Circle
                        PointF sourcePoint, viewPoint;
                        sourcePoint = ((DownFeedback)feedback).getSourcePoint();
                        viewPoint = this.sourceToViewCoord(sourcePoint);
                        //Drawing
                        canvas.drawCircle(viewPoint.x, viewPoint.y, downFeedbackRadius, feedbackPaint);
                        downDotPaint.setAlpha(alpha);
                        canvas.drawCircle(viewPoint.x, viewPoint.y, downFeedbackDotRadius, downDotPaint);
                    }else if(feedback instanceof ClickFeedback) {
                        //Rectangle
                        Rect sourceRectangle = ((ClickFeedback)feedback).getSourceRectangle();
                        PointF topLeft, bottomRight;
                        topLeft = this.sourceToViewCoord(sourceRectangle.left, sourceRectangle.top);
                        bottomRight = this.sourceToViewCoord(sourceRectangle.right, sourceRectangle.bottom);
                        clickFeedbackRect.set(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y);
                        //Drawing
                        canvas.drawRoundRect(clickFeedbackRect, clickFeedbackCorner, clickFeedbackCorner, feedbackPaint);
                    }
                }
            }
            if(!feedbacks.isEmpty())
                invalidate();
        }
    }

    abstract class Feedback{
        private boolean removed;
        private long timeCreated, timeRemoved;

        public Feedback(){
            this.timeCreated = System.currentTimeMillis();
            this.removed = false;
            this.timeRemoved = 0;
        }

        public long getTimeCreated() {
            return timeCreated;
        }

        public void remove() {
            if(!this.removed) {
                this.removed = true;
                this.timeRemoved = System.currentTimeMillis();
            }
        }

        public boolean isRemoved() {
            return removed;
        }

        public long getTimeRemoved() {
            return timeRemoved;
        }
    }

    private class DownFeedback extends Feedback{
        private PointF sourcePoint;

        public DownFeedback(PointF sourcePoint) {
            super();
            this.sourcePoint = sourcePoint;
        }

        public PointF getSourcePoint() {
            return sourcePoint;
        }
    }

    private class ClickFeedback extends Feedback{
        private Rect sourceRectangle;

        public ClickFeedback(Rect sourceRectangle) {
            super();
            this.sourceRectangle = sourceRectangle;
        }

        public Rect getSourceRectangle() {
            return sourceRectangle;
        }
    }
}
