package com.universio.humack.synergology;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.universio.humack.BaseActivity;
import com.universio.humack.R;
import com.universio.humack.synergology.data.Attitude;

import java.util.ArrayList;

/**
 * Created by Cyril Humbertclaude on 19/05/2015.
 */
public class AttitudeFragment extends Fragment{

    private LayoutInflater inflater;

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

    /**
     * Show a group of attitudes
     * @param attitudes The attitudes
     */
    public void init(ArrayList<Attitude> attitudes, int bandColor){
        LinearLayout attitudeView;
        ImageView micromovementView;
        TextView descriptionView, meaningView;

        //Tables of header and data
        TableLayout attitudeTable, attitudesTableData;
        View thisView = getView();
        if(thisView != null) {
            attitudeTable = (TableLayout) thisView.findViewById(R.id.fragment_attitude_table);
            attitudesTableData = (TableLayout) attitudeTable.findViewById(R.id.fragment_attitude_table_data);
            attitudesTableData.removeAllViews();

            //Manual Fixing of columns sizes
            int rootWidth, spacing, usableSpace, attitudeColumnWidth, meaningColumnWidth;
            thisView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            rootWidth = thisView.getLayoutParams().width;
            spacing = BaseActivity.DEFAULT_SPACING * 3;
            usableSpace = rootWidth - spacing;
            attitudeColumnWidth = (int) Math.floor(usableSpace / 2.5);
            meaningColumnWidth = usableSpace - attitudeColumnWidth;
            attitudeTable.findViewById(R.id.fragment_attitude_header_attitude).getLayoutParams().width = attitudeColumnWidth;
            attitudeTable.findViewById(R.id.fragment_attitude_header_meaning).getLayoutParams().width = meaningColumnWidth;

            //Band of color
            TableRow tableBand;
            tableBand = (TableRow) attitudeTable.findViewById(R.id.fragment_attitude_band);
            tableBand.setBackgroundColor(bandColor);

            //Style
            int oddBackgroundColor = getResources().getColor(R.color.table_row_odd_background);
            boolean odd = false;

            //For all attitudes:
            for (Attitude attitude : attitudes) {
                //Create the row
                TableRow attitudeRow = (TableRow) inflater.inflate(R.layout.fragment_attitude_row, null);
                attitudeRow.setId(attitude.getId());

                //Layout of attitude
                attitudeView = (LinearLayout) attitudeRow.findViewById(R.id.fragment_attitude_data_attitude);
                attitudeView.getLayoutParams().width = attitudeColumnWidth;
                //Micromovement
                if (attitude.hasMicromovement()) {
                    int iconId;
                    if (attitude.getMicromovement().getId() == 1)
                        iconId = R.drawable.icon_micromovement_caress;
                    else if (attitude.getMicromovement().getId() == 2)
                        iconId = R.drawable.icon_micromovement_fixedness;
                    else if (attitude.getMicromovement().getId() == 3)
                        iconId = R.drawable.icon_micromovement_itch;
                    else
                        iconId = R.drawable.icon_micromovement;
                    micromovementView = (ImageView) attitudeRow.findViewById(R.id.fragment_attitude_data_micromovement);
                    micromovementView.setImageResource(iconId);
                    micromovementView.setContentDescription(attitude.getMicromovement().getTitle());
                    micromovementView.setVisibility(View.VISIBLE);
                }
                //Description
                if (attitude.hasDescription()) {
                    descriptionView = (TextView) attitudeRow.findViewById(R.id.fragment_attitude_data_description);
                    descriptionView.setText(attitude.getDescription());
                    descriptionView.setVisibility(View.VISIBLE);
                }
                //Meaning
                meaningView = (TextView) attitudeRow.findViewById(R.id.fragment_attitude_data_meaning);
                meaningView.getLayoutParams().width = meaningColumnWidth;
                meaningView.setText(attitude.getMeaning());

                //Style
                if(odd)
                    attitudeRow.setBackgroundColor(oddBackgroundColor);
                odd = !odd;

                //Add the row to table
                attitudesTableData.addView(attitudeRow);
            }
        }
    }
}
