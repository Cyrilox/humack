package com.universio.humack.synergology;

import android.app.Fragment;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.universio.humack.R;
import com.universio.humack.Settings;
import com.universio.humack.Tools;
import com.universio.humack.synergology.data.ImageArea;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Cyril Humbertclaude on 11/05/2015.
 */
public class ImageFragment extends Fragment{
    //Image
    private BodyImageView bodyView;

    //Handlings
    private OnInteractionListener interactionListener = null;
    private GestureDetector gestureDetector;
    private boolean isTouchedRecently = false;
    private long lastTouchTime = 0;

    //Images
    public static final int IMAGE_HEAD_AREAS_ID = 0, IMAGE_BODY_AREAS_ID = 1;
    private static final String IMAGE_BODY = "human_body.jpg", IMAGE_BODY_AREAS = "human_body_areas.jpg", IMAGE_HEAD_AREAS = "human_body_areas_head.jpg";
    private static final int MODE_ANIMATION = 0, MODE_MANUAL = 1, REAL_IMAGE_WIDTH = 4215, REAL_IMAGE_HEIGHT = 6000;
    private static final int AREA_IMAGE_WIDTH = 600, AREA_IMAGE_HEIGHT = 854, AREA_IMAGE_HEAD_WIDTH = 600, AREA_IMAGE_HEAD_HEIGHT = 606;

    private int currentMode = -1;

    //Image areas
    private ArrayList<ImageArea> imageAreas=null;
    private Rect areaImageHeadFrame;

    //Sizes
    private Rect frameFullsize, frameSmallsize;
    private int imageViewWidth, imageViewHeight;
    private HashMap<Integer, Object[]> scalesAndCenters;
    private float scaleHeadForSwitch = 0;

    public static ImageFragment newInstance() {
        return new ImageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bodyView = (BodyImageView)getView();
        if(bodyView != null) {
            //Image
            bodyView.setImage(ImageSource.asset(IMAGE_BODY));
            //Image transformations
            bodyView.setPanEnabled(true);
            bodyView.setZoomEnabled(true);
            bodyView.setQuickScaleEnabled(false);
            bodyView.setMinimumTileDpi(160);//OutOfMemoryError si supérieur sur device très HD
            setMode(MODE_MANUAL);
            //Image Events
            bodyView.setOnImageEventListener(new SubsamplingScaleImageView.OnImageEventListener() {
                @Override
                public void onReady() {
                }

                @Override
                public void onImageLoaded() {
                    interactionListener.onImageLoaded();
                }

                @Override
                public void onPreviewLoadError(Exception e) {
                }

                @Override
                public void onImageLoadError(Exception e) {
                }

                @Override
                public void onTileLoadError(Exception e) {
                }
            });
            //Gesture Events
            gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapConfirmed(MotionEvent event){
                    if (bodyView.isReady()) {
                        //On click transfer
                        PointF coord = bodyView.viewToSourceCoord(event.getX(), event.getY());
                        onSingleTap((int)coord.x, (int)coord.y);
                    }
                    return true;
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    notifyListenerOfGesture();
                    return super.onScroll(e1, e2, distanceX, distanceY);
                }

                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    notifyListenerOfGesture();
                    return true;
                }

                @Override
                public boolean onDown(MotionEvent event) {
                    if (bodyView.isReady()) {
                        //On down feedback
                        PointF sourceCoord = bodyView.viewToSourceCoord(event.getX(), event.getY());
                        bodyView.addDownFeedback(sourceCoord);
                    }
                    return super.onDown(event);
                }
            });
            gestureDetector.setIsLongpressEnabled(false);
            bodyView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    //ON UP Event
                    if(motionEvent.getAction() == MotionEvent.ACTION_UP)
                        bodyView.removeFeedbacks();//Suppression des feedback
                    //Other events
                    return gestureDetector.onTouchEvent(motionEvent);
                }
            });
        }
        //! Valeur en dur !
        areaImageHeadFrame = new Rect(1574, 0, 2680, 1118);
    }

    private void notifyListenerOfGesture(){
        //Not very often
        long time = System.currentTimeMillis();
        if(time - lastTouchTime > 100)
            isTouchedRecently = false;
        if(!isTouchedRecently){
            isTouchedRecently = true;
            lastTouchTime = time;
            //Change to manual mode to restrict pan & scale
            setMode(MODE_MANUAL);
            //Notify the activity to close current attitude
            interactionListener.onTouched();
        }
    }

    public void setImageAreas(ArrayList<ImageArea> imageAreas) {
        this.imageAreas = imageAreas;
    }

    public void setListener(OnInteractionListener interactionListener){
        this.interactionListener = interactionListener;
    }

    /**
     * Switch the SubsamplingScaleImageView parameters
     * @param mode MODE_ANIMATION is flexible in order to do animations, MODE_MANUAL is narrowed for manual gestures
     */
    private void setMode(int mode){
        if(mode != currentMode) {
            switch (mode) {
                case MODE_ANIMATION://Flexible
                    currentMode = MODE_ANIMATION;
                    bodyView.setPanLimit(SubsamplingScaleImageView.PAN_LIMIT_OUTSIDE);
                    bodyView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
                    bodyView.setMinimumDpi(20);//Zoom in max
                    bodyView.setMaximumDpi(10000);//Zoom out max
                    break;
                case MODE_MANUAL://Narrowed
                    currentMode = MODE_MANUAL;
                    bodyView.setPanLimit(SubsamplingScaleImageView.PAN_LIMIT_CENTER);
                    bodyView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_INSIDE);
                    bodyView.setMinimumDpi(20);//Zoom in max
                    break;
                default://Wrong mode
                    Log.w("_ImageFragment", ".setMode| Mode " + mode + " is not existing, nothing has been done.");
            }
        }
    }

    public void updateSizes(int splitY) {
        View view = getView();
        if(view != null) {
            //Frames
            imageViewWidth = view.getWidth();
            imageViewHeight = view.getHeight();
            frameFullsize = new Rect(0, 0, imageViewWidth, imageViewHeight);
            frameSmallsize = new Rect(0, 0, imageViewWidth, splitY);
            //Scales and centers
            if(imageAreas != null){
                scalesAndCenters = new HashMap<>();
                for(ImageArea imageArea : imageAreas) {
                    Object[] scaleAndCenter = getScaleAndCenter(imageArea);
                    scalesAndCenters.put(imageArea.getId(), scaleAndCenter);
                    if(imageArea.getBodyGroup().getId() == 21)
                        scaleHeadForSwitch = (float)scaleAndCenter[0]*70/100;//Larger zoom for head switching
                }
            }
        }
    }

    /**
     * Triggered when clicking on the images fragment
     * Search the corresponding ImageArea and send it to the listener Activity
     * @param xCoord The X coordinate of the real image
     * @param yCoord The Y coordinate of the real image
     */
    private void onSingleTap(int xCoord, int yCoord) {
        if (interactionListener != null && xCoord >= 0 && xCoord < REAL_IMAGE_WIDTH && yCoord >= 0 && yCoord < REAL_IMAGE_HEIGHT) {
            //Search the area clicked into body area image
            int realXCoord, realYCoord;
            realXCoord = Math.round(AREA_IMAGE_WIDTH * xCoord / (float)REAL_IMAGE_WIDTH);
            realYCoord = Math.round(AREA_IMAGE_HEIGHT * yCoord / (float)REAL_IMAGE_HEIGHT);
            if(realXCoord < 0 || realXCoord >= AREA_IMAGE_WIDTH || realYCoord < 0 || realYCoord >= AREA_IMAGE_HEIGHT)
                return;

            int pixelColor = Tools.getPixelColor(IMAGE_BODY_AREAS, realXCoord, realYCoord);

            ImageArea imageAreaClicked = getImageArea(pixelColor, IMAGE_BODY_AREAS_ID);

            //Only if an area is found
            if (imageAreaClicked != null) {
                //Zoom suffisant si le zoom actuelle est supérieur ou égale au zoom de la tête avec marge
                boolean isZoomEnough = bodyView.getScale() >= scaleHeadForSwitch;

                //If the head is clicked and the image is enough zoomed in
                if (imageAreaClicked.getBodyGroup().getId() == 21 && isZoomEnough){
                    //Search again into the head area
                    int realHeadXCoord, realHeadYCoord;
                    realHeadXCoord = Math.round(AREA_IMAGE_HEAD_WIDTH * ((xCoord-areaImageHeadFrame.left) * (REAL_IMAGE_WIDTH/(float)areaImageHeadFrame.width())) / (float)REAL_IMAGE_WIDTH);
                    realHeadYCoord = Math.round(AREA_IMAGE_HEAD_HEIGHT * ((yCoord-areaImageHeadFrame.top) * (REAL_IMAGE_HEIGHT/(float)areaImageHeadFrame.height())) / (float)REAL_IMAGE_HEIGHT);
                    if(realHeadXCoord < 0 || realHeadXCoord >= AREA_IMAGE_HEAD_WIDTH || realHeadYCoord < 0 || realHeadYCoord >= AREA_IMAGE_HEAD_HEIGHT)
                        return;

                    int pixelColorHead = Tools.getPixelColor(IMAGE_HEAD_AREAS, realHeadXCoord, realHeadYCoord);

                    ImageArea imageAreaHeadClicked = getImageArea(pixelColorHead, IMAGE_HEAD_AREAS_ID);

                    if(imageAreaHeadClicked != null)
                        imageAreaClicked = imageAreaHeadClicked;
                }
                //Zone valide cliqué
                //Visual feedback filling the area
                bodyView.addClickFeedback(imageAreaClicked.getRectangle());
                //Transfere
                interactionListener.onImageAreaClick(imageAreaClicked);
            }
        }
    }

    /**
     * Search the area corresponding to a pixel color
     * @param pixelColor The pixel color to search
     * @param areaImageId The image id where to search
     * @return The area or null
     */
    private ImageArea getImageArea(int pixelColor, int areaImageId){
        for (ImageArea imageArea : imageAreas)
            if(imageArea.getImage() == areaImageId)
                if (Tools.isCloseColor(pixelColor, imageArea.getColor(), 25))
                    return imageArea;
        return null;
    }

    /**
     * Zoom and translate image in order to see the ImageArea
     * @param imageArea The destination area
     */
    public void showImageArea(ImageArea imageArea){
        Object[] scaleAndCenter = scalesAndCenters.get(imageArea.getId());

        setMode(MODE_ANIMATION);

        //Zoom to this rectangle frame
        bodyView.animateScaleAndCenter((float)scaleAndCenter[0], (PointF)scaleAndCenter[1])
                .withDuration(Settings.getAnimationSpeed())
                .withEasing(SubsamplingScaleImageView.EASE_IN_OUT_QUAD)
                .withInterruptible(false)
                .start();
    }

    private Object[] getScaleAndCenter(ImageArea imageArea){
        Rect toframe, imageFrame;
        float toFrameWidth, toframeHeight, imageFrameWidth, imageFrameHeight, imageFrameHeightVisible, imageFrameCenterX, imageFrameCenterY;
        float toCenterX, toCenterY, toScale, toScaleX, toScaleY, visibleWidth, xShift, yShift;
        boolean scaleFromHeight;

        //Destination frame on the screen
        if(imageArea.getBodyGroup().hasAttitudes())
            toframe = frameSmallsize;//Small on top if has attitudes
        else //Else small size with attitudes
            toframe = frameFullsize;//Full size instead
        toFrameWidth = toframe.right - toframe.left;
        toframeHeight = toframe.bottom - toframe.top;

        //Areas with the full size
        imageFrame = imageArea.getRectangle();
        imageFrameWidth = imageFrame.right - imageFrame.left;
        imageFrameHeight = imageFrame.bottom - imageFrame.top;
        imageFrameCenterX = (int)Math.floor(imageFrame.left + imageFrameWidth / 2);
        imageFrameCenterY = (int)Math.floor(imageFrame.top + imageFrameHeight / 2);

        //Scale
        visibleWidth = imageFrameHeight * toFrameWidth / toframeHeight;
        scaleFromHeight = visibleWidth > imageFrameWidth;
        toScaleX = toFrameWidth / imageFrameWidth;
        toScaleY = toframeHeight / imageFrameHeight;

        toScale = scaleFromHeight ? toScaleY : toScaleX;

        imageFrameHeightVisible = imageFrameHeight;
        if(!scaleFromHeight)
            imageFrameHeightVisible *= toScaleY / toScaleX;

        //Translate
        xShift = toFrameWidth == imageViewWidth ? 0 : ImageFragment.REAL_IMAGE_WIDTH * imageFrameWidth * 1.5f / ImageFragment.REAL_IMAGE_WIDTH;
        yShift = toframeHeight == imageViewHeight ? 0 : ImageFragment.REAL_IMAGE_HEIGHT * imageFrameHeightVisible * 1.5f / ImageFragment.REAL_IMAGE_HEIGHT;
        toCenterX = (int)Math.floor(imageFrameCenterX + xShift);
        toCenterY = (int)Math.floor(imageFrameCenterY + yShift);

        PointF toCenter = new PointF(toCenterX, toCenterY);

        return new Object[]{toScale, toCenter};
    }

    /**
     * Interface for interaction from ImageFragment to its activity
     */
    public interface OnInteractionListener {
        /**
         * A click on an image part
         * @param imageArea The ImageArea clicked
         */
        void onImageAreaClick(ImageArea imageArea);

        /**
         * Image is loaded
         */
        void onImageLoaded();

        /**
         * Image is touched
         */
        void onTouched();
    }
}
