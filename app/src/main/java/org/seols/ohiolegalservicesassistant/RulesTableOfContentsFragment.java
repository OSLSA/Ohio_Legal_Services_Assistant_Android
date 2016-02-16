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

import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Created by joshuagoodwin on 2/15/16.
 */
public class RulesTableOfContentsFragment extends Fragment {

    TableLayout tl;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
            String tag = null;
            try {
                Double test = NumberFormat.getInstance().parse(titles[i]).doubleValue();
                // if this is an int, should convert then to string otherwise string will end in .0
                tag = (test % 1 == 0) ? Integer.toString(test.intValue()) : Double.toString(test);
                tr.setTag(tag);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tr.setTag(tag);
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
