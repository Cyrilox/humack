package com.universio.humack;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReleasenoteDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReleasenoteDialog extends Fragment implements Animation.AnimationListener{
    private String appversionChanges = null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ReleasenoteDialog.
     */
    public static ReleasenoteDialog newInstance(){
        return new ReleasenoteDialog();
    }

    public ReleasenoteDialog() {
        // Required empty public constructor
    }

    public void setAppversionChanges(String appversionChanges){
        this.appversionChanges = appversionChanges;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_releasenote, container, false);

        //Textview Html.fromHtml fix
        if (rootView != null) {
            Tools.fromHtml((ViewGroup) rootView);

            //Changements
            if (appversionChanges != null) {
                TextView changesTextview = (TextView) rootView.findViewById(R.id.releasenote_changes);
                if (changesTextview != null)
                    changesTextview.setText(appversionChanges);
            }
            //Fermer le dialogue
            Button buttonOK = (Button) rootView.findViewById(R.id.releasenote_ok);
            buttonOK.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    closeDialog();
                }
            });
        }

        return rootView;
    }

    public void showDialog(boolean showAppversion){
        View rootView = getView();
        if (rootView != null){
            //Version
            if(showAppversion) {
                TextView releasenoteTitle = (TextView) rootView.findViewById(R.id.releasenote_title);
                String appversionName = getResources().getString(R.string.app_version);
                releasenoteTitle.setText(appversionName);
            }

            rootView.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.dialog_fadein));
        }
    }

    public void closeDialog(){
        View view = this.getView();
        if(view != null) {
            Animation fadeOutAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.dialog_fadeout);
            fadeOutAnimation.setAnimationListener(this);
            view.startAnimation(fadeOutAnimation);
        }
    }

    /**
     * <p>Notifies the start of the animation.</p>
     *
     * @param animation The started animation.
     */
    @Override
    public void onAnimationStart(Animation animation) {

    }

    /**
     * <p>Notifies the end of the animation. This callback is not invoked
     * for animations with repeat count set to INFINITE.</p>
     *
     * @param animation The animation which reached its end.
     */
    @Override
    public void onAnimationEnd(Animation animation) {
        getFragmentManager().beginTransaction().remove(this).commit();
    }

    /**
     * <p>Notifies the repetition of the animation.</p>
     *
     * @param animation The animation which was repeated.
     */
    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
