package com.universio.humack.synergology;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.universio.humack.MainActivity;
import com.universio.humack.R;
import com.universio.humack.Settings;
import com.universio.humack.Tools;
import com.universio.humack.synergology.data.Attitude;
import com.universio.humack.synergology.data.BodyGroup;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by Cyril Humbertclaude on 19/05/2015.
 */
public class AttitudeFragment extends Fragment{

    private BodyGroup bodyGroup;
    private OnInteractionListener interactionListener;
    private GestureDetector gestureDetector;
    private LayoutInflater inflater;
    private View fragmentAttitude;
    private int splitY=0, layoutHeight=0, visibility=0, animationToVisible=0, attitudeColumnWidth=0, meaningColumnWidth=0, hemisphericalMeaningColumnWidth=0;
    private float lastY=0;
    private AnimatorSet animations=null;

    /**
     * Use this factory method to create a new instance of this fragment
     * @return A new instance of fragment AttitudeFragment.
     */
    public static AttitudeFragment newInstance() {
        return new AttitudeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.inflater = inflater;
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_attitude, container, false);
    }

    public void setListener(OnInteractionListener interactionListener){
        this.interactionListener = interactionListener;
    }

    /**
     * Set the body group and init the view
     * @param bodyGroup The body group
     */
    public void init(BodyGroup bodyGroup){
        //Body group
        this.bodyGroup = bodyGroup;
        //Fragment
        fragmentAttitude = getView();

        if(fragmentAttitude != null) {
            //Liste
            ListView listView = (ListView) fragmentAttitude.findViewById(R.id.fragment_attitude_list);

            //Header
            View header = fragmentAttitude.findViewById(R.id.fragment_attitude_header);

            //Columns sizes
            int rootWidth, spacing, usableSpace, hemisphereIconWidth;
            fragmentAttitude.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            rootWidth = fragmentAttitude.getLayoutParams().width;
            spacing = MainActivity.DEFAULT_SPACING * 3;
            usableSpace = rootWidth - spacing;
            attitudeColumnWidth = (int) Math.floor(usableSpace / 2.5);
            meaningColumnWidth = usableSpace - attitudeColumnWidth;
            hemisphereIconWidth = Tools.getPixelFromDP(getActivity(), 28);
            hemisphericalMeaningColumnWidth = meaningColumnWidth - hemisphereIconWidth;
            header.findViewById(R.id.fragment_attitude_header_attitude).getLayoutParams().width = attitudeColumnWidth;
            header.findViewById(R.id.fragment_attitude_header_meaning).getLayoutParams().width = meaningColumnWidth;

            //Adapter
            AttitudeArrayAdapter adapter = new AttitudeArrayAdapter(getActivity(), bodyGroup.getAttitudes());
            listView.setAdapter(adapter);

            //Listeners
            gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onScroll(MotionEvent firstDownEvent, MotionEvent moveEvent, float distanceX, float distanceY) {
                    //Fragment is following the finger vertically
                    lastY = fragmentAttitude.getY();
                    float newYPos = lastY + moveEvent.getY() - firstDownEvent.getY();
                    if(newYPos < splitY)
                        newYPos = splitY;
                    else if(newYPos > layoutHeight)
                        newYPos = layoutHeight;
                    if(newYPos >= splitY && newYPos <= layoutHeight)
                        fragmentAttitude.setY(newYPos);
                    fragmentAttitude.setY(newYPos);
                    return true;
                }

                @Override
                public boolean onFling(MotionEvent firstDownEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
                    //Rustine de vélocité inversé
                    float velocityYFixed = Math.abs(velocityY);
                    if(lastY > fragmentAttitude.getY())
                        velocityYFixed = -velocityYFixed;
                    //Fragment reopen entirely or close
                    boolean open = velocityYFixed < 0;
                    int distance =  Math.round(open ?  Math.max(fragmentAttitude.getY(), splitY) - splitY : layoutHeight -  Math.min(fragmentAttitude.getY(), layoutHeight));
                    long duration = Math.min(Settings.getAnimationSpeed(), Math.round(distance / Math.abs(velocityYFixed) * 1000));
                    changeVisibility(open, duration);
                    return true;
                }
            });
            fragmentAttitude.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return gestureDetector.onTouchEvent(motionEvent);
                }
            });

            /**
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    interactionListener.onAttitudeClick((int) id);
                }
            });
             */
        }
    }

    public void updateSizes(int splitY, int layoutHeight) {
        this.splitY = splitY;
        this.layoutHeight = layoutHeight;
    }

    /**
     * Slide the fragment to show or hide it
     * @param visible True for showing it, false to hide
     * @param duration Animation speed in millisecond, negative value for default speed
     */
    public void changeVisibility(boolean visible, long duration){
        //Visibilité
        int newVisibility = visible ? 2 : 0;
        //Précédent animation est terminée
        if(animations != null && !animations.isRunning())
            animations = null;
        //Si on est entrain de faire l'animation inverse
        if(animations != null && animationToVisible != newVisibility) {
            // on interrompt l'animation en cours et démarre celle inverse
            animations.cancel();
            animations = null;
        }
        //Animation si on est pas déjà entrain d'en faire une et qu'elle est différente
        updateVisibility();
        if(animations == null && newVisibility != visibility) {
            View view = this.getView();
            if (view != null) {
                //Closing the fragment
                if(!visible)
                    interactionListener.onFragmentClosing(bodyGroup);
                //Animation
                animationToVisible = visible ? 2 : 0;
                animations = new AnimatorSet();
                Interpolator interpolator;
                if(duration >= 0)
                    interpolator = new LinearInterpolator();
                else if(visible)
                    interpolator = new DecelerateInterpolator();
                else
                    interpolator = new AccelerateInterpolator();
                animations.setInterpolator(interpolator);

                //Change visibility
                if (visible)
                    view.bringToFront();
                ObjectAnimator visibilityAnim = ObjectAnimator.ofInt(view, "visibility", visible ? View.VISIBLE : View.GONE);
                visibilityAnim.setDuration(0);

                //Translate
                ObjectAnimator translationY = ObjectAnimator.ofFloat(view, "translationY", visible ? splitY : layoutHeight);
                translationY.setDuration(duration < 0 ? Settings.getAnimationSpeed() : duration);

                //Start animation
                if (visible) {
                    animations.play(visibilityAnim);
                    animations.play(translationY).after(visibilityAnim);
                } else {
                    animations.play(translationY);
                    animations.play(visibilityAnim).after(translationY);
                }

                animations.start();
            }
        }
    }

    /**
     * Met à jour le paramètre visibility
     * 0: Invisible
     * 1: Partiellement visible
     * 2: Totalement visible
     */
    private void updateVisibility() {
        if(fragmentAttitude.getY() >= layoutHeight)
            visibility = 0;
        else if(fragmentAttitude.getY() <= splitY)
            visibility = 2;
        else
            visibility = 1;
    }

    private class AttitudeArrayAdapter extends ArrayAdapter<Attitude> {
        private ArrayList<Attitude> attitudes;
        private Hashtable<Integer, View> views;

        public AttitudeArrayAdapter(Context context, ArrayList<Attitude> attitudes) {
            super(context, -1, attitudes);
            this.attitudes = attitudes;
            this.views = new Hashtable<>();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //Récuperation de la vue
            View row = views.containsKey(position) ? views.get(position) : null;

            //ou création
            if(row == null) {
                row = inflater.inflate(R.layout.fragment_attitude_row, parent, false);
                //Colonne attitude
                View attitudeColumn = row.findViewById(R.id.fragment_attitude_row_attitude);
                attitudeColumn.getLayoutParams().width = attitudeColumnWidth;
                //Micromovement
                Attitude attitude = attitudes.get(position);
                if (attitude.hasMicromovement()) {
                    int iconId;
                    switch (attitude.getMicromovement().getId()) {
                        case 1:
                            iconId = R.drawable.icon_micromovement_caress_24dp;
                            break;
                        case 2:
                            iconId = R.drawable.icon_micromovement_fixedness_24dp;
                            break;
                        case 3:
                            iconId = R.drawable.icon_micromovement_itch_24dp;
                            break;
                        default:
                            iconId = R.drawable.icon_micromovement_24dp;
                    }
                    ImageView micromovement = (ImageView) row.findViewById(R.id.fragment_attitude_row_micromovement);
                    micromovement.setImageResource(iconId);
                    micromovement.setContentDescription(attitude.getMicromovement().getTitle());
                    micromovement.setVisibility(View.VISIBLE);
                }
                //Description
                if (attitude.hasDescription()) {
                    TextView description = (TextView) row.findViewById(R.id.fragment_attitude_row_description);
                    description.setText(attitude.getDescription());
                    description.setVisibility(View.VISIBLE);
                }
                //Meaning
                TextView meaningA = (TextView) row.findViewById(R.id.fragment_attitude_row_meaning_a);
                if(!attitude.hasHemisphere()){
                    meaningA.setText(attitude.getMeaningA());
                    meaningA.setVisibility(View.VISIBLE);
                    meaningA.getLayoutParams().width = meaningColumnWidth;
                }else{
                    if(attitude.getHemisphere().getId() == 1 || attitude.getHemisphere().getId() == 3){
                        ImageView hemisphereLeft = (ImageView) row.findViewById(R.id.fragment_attitude_row_hemisphere_left);
                        hemisphereLeft.setImageResource(R.drawable.icon_hemisphere_left_24dp);
                        hemisphereLeft.setVisibility(View.VISIBLE);

                        meaningA.setText(attitude.getMeaningA());
                        meaningA.setVisibility(View.VISIBLE);
                        meaningA.getLayoutParams().width = hemisphericalMeaningColumnWidth;
                    }
                    if(attitude.getHemisphere().getId() == 2 || attitude.getHemisphere().getId() == 3){
                        ImageView hemisphereRight = (ImageView) row.findViewById(R.id.fragment_attitude_row_hemisphere_right);
                        hemisphereRight.setImageResource(R.drawable.icon_hemisphere_right_24dp);
                        hemisphereRight.setVisibility(View.VISIBLE);

                        TextView meaningB = (TextView) row.findViewById(R.id.fragment_attitude_row_meaning_b);
                        meaningB.setText(attitude.getMeaningB());
                        meaningB.setVisibility(View.VISIBLE);
                        meaningB.getLayoutParams().width = hemisphericalMeaningColumnWidth;
                    }
                }

                //Redimensionement
                row.setId(attitude.getId());

                //Ajoute
                views.put(position, row);
            }

            //Returning the view
            return row;
        }

        @Override
        public long getItemId(int position) {
            return attitudes.get(position).getId();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public int getPosition(Attitude attitude) {
            return attitude.getSuborder();
        }
    }

    /**
     * Interface for interaction from AttitudeFragment to its activity
     */
    public interface OnInteractionListener {
        /**
         * Called when the fragment is closing
         */
        void onFragmentClosing(BodyGroup bodyGroup);
        /**
         * A click on an attitude
         * @param id The attitude id
         */
        void onAttitudeClick(int id);
    }
}
