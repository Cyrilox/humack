package com.universio.humack.synergology;

import android.app.Fragment;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.universio.humack.R;
import com.universio.humack.Tools;

import java.util.ArrayList;

/**
 * Created by Cyril Humbertclaude on 11/05/2015.
 */
public class ImageFragment extends Fragment{

    //Activity handling clicks
    private OnInteractionListener interactionListener;
    private SubsamplingScaleImageView bodyView;
    private GestureDetector gestureDetector;

    //Images
    public static final String IMAGE_BODY_AREAS = "human_body_areas.jpg", IMAGE_HEAD_AREAS = "human_body_areas_head.jpg";
    private String currentAreaImage;
    public static final int REAL_IMAGE_WIDTH = 4215, REAL_IMAGE_HEIGHT = 6000;
    private static final int AREA_IMAGE_WIDTH = 200, AREA_IMAGE_HEIGHT = 285;

    //Image areas
    private ArrayList<ImageArea> imageAreasHead, imageAreasBody, currentImageAreas;

    public static ImageFragment newInstance() {
        return new ImageFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bodyView = (SubsamplingScaleImageView)getView();
        if(bodyView != null) {
            bodyView.setImage(ImageSource.asset("human_body.jpg"));
            bodyView.setPanLimit(SubsamplingScaleImageView.PAN_LIMIT_OUTSIDE);
            bodyView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CUSTOM);
            bodyView.setMinimumDpi(20);//Zoom minimum
            bodyView.setPanEnabled(false);
            bodyView.setZoomEnabled(false);
            bodyView.setQuickScaleEnabled(false);
            bodyView.setOnImageEventListener(new SubsamplingScaleImageView.OnImageEventListener() {
                @Override
                public void onReady() {

                }

                @Override
                public void onImageLoaded() {
                    bodyView.setMaximumDpi(10000);//Zoom large
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

            gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapConfirmed(MotionEvent event){
                    if (bodyView.isReady()) {
                        PointF coord = bodyView.viewToSourceCoord(event.getX(), event.getY());
                        onSingleTap((int)coord.x, (int)coord.y);
                    }
                    return true;
                }
            });
            bodyView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return gestureDetector.onTouchEvent(motionEvent);
                }
            });
        }
    }

    public void setListener(OnInteractionListener interactionListener){
        this.interactionListener = interactionListener;
    }

    /**
     * Init imageAreasHead and imageAreasBody lists
     */
    private void init(){
        interactionListener = null;
        if(imageAreasHead == null) {
            imageAreasHead = new ArrayList<>();
            //Ids come from the Excel file in the "listes" tab
            int[] headAreasIds = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
            //Unique colors from the "head_areas" image
            int[] headAreasColors = new int[]{-16776961, -65281, -65536, -16711936, -16711681, -8454017, -256, -8454144, -16744577, -16777216, -12910533};
            //Frames from the "body_frames" image
            Rect[] headAreasFrames = new Rect[]{R(1848, 15, 2585, 473), R(2309, 268, 2584, 441), R(2268, 411, 2584, 505), R(2274, 465, 2585, 544), R(2421, 481, 2577, 723), R(1910, 433, 2080, 690), R(2374, 723, 2547, 836), R(2292, 808, 2508, 941), R(1892, 780, 2352, 1058), R(1660, 681, 2112, 1076), R(1390, 982, 2901, 1980)};
            int id, color;
            Rect imageFrame;
            for (int i = 0; i < headAreasIds.length; i++) {
                id = headAreasIds[i];
                color = headAreasColors[i];
                imageFrame = headAreasFrames[i];
                ImageArea imageArea = new ImageArea(id, color, imageFrame);
                imageAreasHead.add(imageArea);
            }
            imageAreasBody = new ArrayList<>();
            //Ids come from the Excel file in the "listes" tab, except for 100 / 101 / 102 / 103 corresponding to head / handshake / seated / body
            int[] bodyAreasIds = new int[]{10, 11, 12, 13, 14, 15, 15, 16, 16, 17, 18, 18, 100, 101, 102, 103};
            //Unique colors from the "body_areas" image
            int[] bodyAreaColors = new int[]{-16777216, -16711681, -256, -12910533, -6710887, -16711936, -16741632, -8224001, -16776961, -65281, -26215, -65536, -8454144, -16744577, -4354746, -1111};
            //Frames from the "body_frames" image
            Rect[] bodyAreaFrames = new Rect[]{R(1662, 680, 2114, 1078), R(1390, 982, 2901, 1980), R(1527, 1821, 2730, 3072), R(2943, 1341, 3966, 2421), R(2637, 1035, 3144, 1491), R(748, 1176, 1556, 2944), R(2700, 1176, 3508, 2944), R(572, 2864, 992, 3404), R(3268, 2860, 3684, 3404), R(1364, 3008, 2884, 5440), R(1132, 5404, 1612, 5980), R(2632, 5400, 3120, 5976), R(1668, 6, 2592, 1059), R(148, 3984, 1000, 4432), R(3412, 4396, 4076, 5306), R(0, 0, REAL_IMAGE_WIDTH, REAL_IMAGE_HEIGHT)};
            for (int i = 0; i < bodyAreasIds.length; i++) {
                id = bodyAreasIds[i];
                color = bodyAreaColors[i];
                imageFrame = bodyAreaFrames[i];
                ImageArea imageArea = new ImageArea(id, color, imageFrame);
                imageAreasBody.add(imageArea);
            }
            this.setCurrentAreaImage(IMAGE_BODY_AREAS);
        }
    }

    private Rect R(int left, int top, int right, int bottom){
        return new Rect(left, top, right, bottom);
    }

    /**
     *
     * @param event The MotionEvent object containing full information about
     *              the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    /**
     * Triggered when clicking on the images fragment
     * Search the corresponding ImageArea and send it to the listener Activity
     * @param xCoord The X coordinate of the real image
     * @param yCoord The Y coordinate of the real image
     */
    public void onSingleTap(int xCoord, int yCoord) {
        if (interactionListener != null) {
            //Coordinates
            int realXCoord, realYCoord;
            realXCoord = AREA_IMAGE_WIDTH * xCoord / REAL_IMAGE_WIDTH;
            realYCoord = AREA_IMAGE_HEIGHT * yCoord / REAL_IMAGE_HEIGHT;
            //Pixel color
            int pixelColor = Tools.getPixelColor(currentAreaImage, realXCoord, realYCoord);
            //Search the area clicked
            ImageArea imageAreaClicked = null;
            for (ImageArea imageArea : currentImageAreas){
                int areaColor = imageArea.getColor();
                if (Tools.isCloseColor(pixelColor, areaColor, 25)) {
                    imageAreaClicked = imageArea;
                    break;
                }
            }
            //Only if an area is found
            if (imageAreaClicked != null)
                interactionListener.onImageAreaClick(imageAreaClicked);
        }
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
    }

    /**
     * Change the current area image
     * @param newAreaImage ImageFragment.IMAGE_BODY_AREAS or ImageFragment.IMAGE_HEAD_AREAS
     */
    public void setCurrentAreaImage(String newAreaImage) {
        if(newAreaImage.equals(IMAGE_BODY_AREAS) || newAreaImage.equals(IMAGE_HEAD_AREAS)) {
            this.currentAreaImage = newAreaImage;
            if(newAreaImage.equals(IMAGE_BODY_AREAS))
                currentImageAreas = imageAreasBody;
            else
                currentImageAreas = imageAreasHead;
        }
    }

    public String getCurrentAreaImage() {
        return currentAreaImage;
    }

    /**
     * Return the ImageArea
     * @param id Id of the ImageArea
     * @return The ImageArea
     */
    public ImageArea getImageArea(int id){
        for(ImageArea imageArea : imageAreasBody)
            if(imageArea.getId() == id)
                return imageArea;
        for(ImageArea imageArea : imageAreasHead)
            if(imageArea.getId() == id)
                return imageArea;

        return null;
    }
}
