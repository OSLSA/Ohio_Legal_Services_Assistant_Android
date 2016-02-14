package org.seols.ohiolegalservicesassistant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by joshuagoodwin on 2/13/16.
 */
public class RulesAllTitlesFragment extends Fragment {

    private TableLayout tl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.rules_all_books_layout, container, false);
        tl = (TableLayout) rootView.findViewById(R.id.rules_titles);
        setTitles(inflater, rootView);
        return rootView;
    }

    private void setTitles(LayoutInflater inflater, View v) {
        String[] titles = getResources().getStringArray(R.array.Rules);
        String[] tags = getResources().getStringArray(R.array.rules_tags);
        for (int i = 0; i < titles.length; i++){
            View tr = inflater.inflate(R.layout.rules_row, null);
            tr.setTag(tags[i]);
            ((TextView)tr.findViewById(R.id.row_text)).setText(titles[i]);
            tr.setOnClickListener(myClickListener);
            tl.addView(tr);
        }
    }

    private View.OnClickListener myClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Toast toast = Toast.makeText(getContext(), v.getTag().toString(), Toast.LENGTH_LONG);
            toast.show();
        }
    };

}
