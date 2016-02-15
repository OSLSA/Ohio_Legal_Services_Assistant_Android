package org.seols.ohiolegalservicesassistant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by joshuagoodwin on 2/15/16.
 */
public class RulesTableOfContentsFragment extends Fragment {

    TableLayout tl;

    public View onViewCreated(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.rules_all_books_layout, container, false);
        tl = (TableLayout) rootView.findViewById(R.id.rules_titles);
        setTitles(inflater);
        return rootView;
    }

    private void setTitles(LayoutInflater inflater) {
        String book = getArguments().getString("data");

        Log.d("tag", book);
        String[] titles = getResources().getStringArray(getResources().getIdentifier(book + "_toc", "array", "org.seols.ohiolegalservicesassistant"));
        for (int i = 0; i < titles.length; i++){
            View tr = inflater.inflate(R.layout.rules_row, null);
            tr.setTag(Integer.parseInt(titles[i]));
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
