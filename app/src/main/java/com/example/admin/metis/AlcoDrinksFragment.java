package com.example.admin.metis;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

import static com.example.admin.metis.MenuActivity.BAR_NAME;


public class AlcoDrinksFragment extends Fragment {

    private final static String TAG = "Metis-Application: ";
    private final static String DB_URL= "https://metis-application.firebaseio.com/";
    private final static String FRAGMENT_NAME = "/Menu/Alcoholic_Drinks";

    private DatabaseReference mRef;
    private ListView listView;
    private ListItemAdapter listItemAdapter;
    ArrayList<Item> itemsList;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Log.i(TAG,"url : " + DB_URL + BAR_NAME + FRAGMENT_NAME);
        mRef  = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_URL + BAR_NAME + FRAGMENT_NAME );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

       itemsList = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_alco_drinks, container, false);
        listView =  view.findViewById(R.id.listView);

        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String foodType = dataSnapshot.getKey();
                itemsList.add(new Item(foodType," ", Item.ITEM_TYPE.HEADER, "0"));
                Map<String,Object> map = (Map<String,Object>) dataSnapshot.getValue();

                for(String key : map.keySet())
                    itemsList.add(new Item(key, (String)map.get(key), Item.ITEM_TYPE.PRODUCT, "0"));

                listItemAdapter = new ListItemAdapter(getActivity().getApplicationContext()
                        ,itemsList, ListItemAdapter.VIEW_SOURCE.MENU_SOURCE, null, null);
                listView.setAdapter(listItemAdapter);
                listItemAdapter.notifyDataSetChanged();
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

        return view;
    }
}
