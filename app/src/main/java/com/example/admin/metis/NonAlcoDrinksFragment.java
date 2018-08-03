package com.example.admin.metis;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

public class NonAlcoDrinksFragment extends Fragment {

    private  final String DB_Url = "https://metis-application.firebaseio.com/Shame_Bar/Non_Alcoholic_Drinks";
    private DatabaseReference mRef,mref1;
    private ArrayList<String> foodList = new ArrayList<>();
    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mRef  = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_Url);
        // listView = getActivity().findViewById(R.id.listView);
        View view = inflater.inflate(R.layout.fragment_non_alco_drinks, container, false);
        listView = (ListView) view.findViewById(R.id.listView);

        final ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, foodList );
        //final ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, foodList );
        // listView.setAdapter(arrayAdapter);

        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String foodType = dataSnapshot.getKey();
                foodList.add(foodType);

                listView.setAdapter(arrayAdapter1);
                arrayAdapter1.notifyDataSetChanged();

                Map<String,Object> map = (Map<String,Object>)dataSnapshot.getValue();
                String dish;
                listView.setAdapter(arrayAdapter1);

                for(String key : map.keySet()){
                    dish =  key + "           " + map.get(key);
                    foodList.add(dish);
                    arrayAdapter1.notifyDataSetChanged();
                }

                foodList.add(" ");
                arrayAdapter1.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //DatabaseReference mRefChild = mRef.child("maor");
        //mRefChild.setValue("pitta");

        return view;
    }

}
