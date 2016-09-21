package org.seols.ohiolegalservicesassistant;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.seols.ohiolegalservicesassistant.ResourceInformation;
import org.seols.ohiolegalservicesassistant.CountyNames;

import java.net.URI;
import java.util.ArrayList;


/**
 * Created by Goodwin on 9/11/2016.
 */

public class LocalResourcesFragment extends Fragment {

    private DatabaseReference mRootRef, mResourceRef, mCountyRef;
    private ProgressBar pb;
    private Spinner spinnerCounty, spinnerCategory;
    private RecyclerView rv;
    private LayoutInflater inflater;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle instanceState) {
        Bundle savedInstanceState = instanceState;
        this.inflater = inflater;
        View rootView = inflater.inflate(R.layout.local_resources_layout, container, false);
        getViews(rootView);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mResourceRef = mRootRef.child("entities");
        mCountyRef = mRootRef.child("counties");
        setCategorySpinners();
        getCounties();
        return rootView;
    }

    private void getViews (View rootView) {
        pb = (ProgressBar) rootView.findViewById(R.id.indeterminate_pb);
        pb.setVisibility(View.INVISIBLE);
        spinnerCounty = (Spinner) rootView.findViewById(R.id.county_spinner);
        spinnerCategory = (Spinner) rootView.findViewById(R.id.category_spinner);
        rv = (RecyclerView) rootView.findViewById(R.id.resource_recycler);
        Button buttonShow = (Button) rootView.findViewById(R.id.show_button);
        buttonShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayResources();
            }
        });
    }

    private void setCategorySpinners() {

        // create array adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.resource_categories));

        // set layout for when dropdown shown
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // apply adapter to spinner
        spinnerCategory.setAdapter(adapter);

    }

    private void setCountySpinner(String[] names) {
        // create array adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, names);

        // set layout for when dropdown shown
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // apply adapter to spinner
        spinnerCounty.setAdapter(adapter);
    }

    private void getCounties() {

        pb.setVisibility(View.VISIBLE);
        Query query = mCountyRef.orderByChild("name");
            query.addListenerForSingleValueEvent(new ValueEventListener() {

                CountDownTimer timer = new CountDownTimer(5000,5000) {

                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        pb.setVisibility(View.INVISIBLE);
                        Toast toast = Toast.makeText(getActivity(), "Sorry, we can't connect to the database. Try again later", Toast.LENGTH_LONG);
                        toast.show();
                        return;
                    }
                }.start();

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayList<String> allNames = new ArrayList<String>();
                    allNames.add("All Counties");
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        CountyNames cn = child.getValue(CountyNames.class);
                        allNames.add(cn.name);
                    }
                    String[] nameArray = new String[allNames.size()];
                    allNames.toArray(nameArray);
                    setCountySpinner(nameArray);
                    pb.setVisibility(View.INVISIBLE);
                    timer.cancel();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    pb.setVisibility(View.INVISIBLE);
                    Toast toast = Toast.makeText(getActivity(), "Sorry, we can't connect to the database. Try again later", Toast.LENGTH_LONG);
                    toast.show();
                }
            });
    }

    private void getLocalResources(String county) {

        pb.setVisibility(View.VISIBLE);
        Query query;
        if (county.equals("All Counties")) {
            query = mResourceRef.orderByChild("county");
        } else {
            query = mResourceRef.orderByChild("county").equalTo(county);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<ResourceInformation> information = new ArrayList<ResourceInformation>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    ResourceInformation ri = child.getValue(ResourceInformation.class);
                    information.add(ri);
                    Log.d("DATABASE", "category: " + ri.category);
                }
                if (spinnerCategory.getSelectedItem().toString().equals(getResources().getStringArray(R.array.resource_categories)[0])) {
                    addToRecylcer(information);
                } else {
                    addToRecylcer(sortByCategory(information));
                }

                pb.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<ResourceInformation> sortByCategory(ArrayList<ResourceInformation> ri) {
        ArrayList<ResourceInformation> sorted = new ArrayList<ResourceInformation>();
        for (ResourceInformation child: ri) {
            if (child.category.equals(spinnerCategory.getSelectedItem().toString())) {
                sorted.add(child);
            }
        }
        if (sorted.size() == 0) {
            ResourceInformation none = new ResourceInformation("Sorry, we have no information for this category in this county.");
            sorted.add(none);
        }
        return sorted;
    }

    private void displayResources() {
        String countySelected = spinnerCounty.getSelectedItem().toString();
        getLocalResources(countySelected);
    }

    private void addToRecylcer(ArrayList<ResourceInformation> ri) {

        myRecyclerAdapter adapter = new myRecyclerAdapter(getContext(), inflater, ri);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    public class myRecyclerAdapter extends RecyclerView.Adapter<myRecyclerViewHolder> {

        Context context;
        LayoutInflater inflater;
        ArrayList<ResourceInformation> ri;

        public myRecyclerAdapter(Context context, LayoutInflater inflater, ArrayList<ResourceInformation> ri) {
            this.context = context;
            this.inflater = inflater;
            this.ri = ri;
        }


        @Override
        public myRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = inflater.inflate(R.layout.resource_details, parent, false);
            myRecyclerViewHolder viewHolder = new myRecyclerViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(myRecyclerViewHolder holder, final int position) {

            holder.tvTitle.setText(ri.get(position).name);
            String csz = ri.get(position).city + ", " + ri.get(position).state + " " + ri.get(position).zip;
            String fullAddress = ri.get(position).address + " " + csz;
            holder.tvAddress.setText(ri.get(position).address);
            holder.tvAddress.setTag(fullAddress);
            holder.tvCsz.setText(csz);
            holder.tvCsz.setTag(fullAddress);
            holder.tvPhone.setText(ri.get(position).phone);
            holder.tvWebsite.setText(ri.get(position).website);
            holder.tvNotes.setText(ri.get(position).notes);
            holder.tvAddress.setOnClickListener(myAdressListener);

        }

        @Override
        public int getItemCount() {
            return ri.size();
        }
    }

    public class myRecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAddress, tvCsz, tvPhone, tvWebsite, tvNotes;

        public myRecyclerViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView)itemView.findViewById(R.id.resource_title);
            tvAddress = (TextView)itemView.findViewById(R.id.resource_address);
            tvCsz = (TextView)itemView.findViewById(R.id.resource_csz);
            tvPhone = (TextView)itemView.findViewById(R.id.resource_phone);
            tvWebsite = (TextView)itemView.findViewById(R.id.resource_website);
            Linkify.addLinks(tvWebsite, Linkify.ALL);
            tvNotes = (TextView)itemView.findViewById(R.id.resource_notes);
        }

    }

    private View.OnClickListener myAdressListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String fullAddress = v.getTag().toString();
            fullAddress.replaceAll("\\s+", "%20");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri myUri = Uri.parse("geo:0,0?q=" + fullAddress);
            intent.setData(myUri);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    };

}
