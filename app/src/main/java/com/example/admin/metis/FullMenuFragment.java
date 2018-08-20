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


public class FullMenuFragment extends Fragment {

    private final static String TAG = "Metis-Application: ";
    private final static String DB_URL = "https://metis-application.firebaseio.com/";
    private final static String MENU_PASS = "/Menu";

    private DatabaseReference databaseReference;
    private ListView listView;
    private ListItemAdapter listItemAdapter;

    ArrayList<Item> itemsList;

    public FullMenuFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_URL + BAR_NAME + MENU_PASS);
        itemsList = new ArrayList<>();
        Log.i(TAG,"FullMenuFragment: onCreate()");
        importMenu();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_full_menu, container, false);
        listView = view.findViewById(R.id.Table_Menu_ListView);
        initListView();

        return view;
    }

    private void importMenu() {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                //added items topic header to the view
                String menuTopicName = dataSnapshot.getKey().replaceAll("_"," ");
                Item currentTopicProducts = new  Item(menuTopicName, "",  Item.ITEM_TYPE.TOPIC,"0");
                itemsList.add(currentTopicProducts);

                try {
                    Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) dataSnapshot.getValue();
                    for (String header : map.keySet()) {

                        //added type of items header to the view
                       Item currentHeaderProducts = new Item(header, " ", Item.ITEM_TYPE.HEADER,"0");
                        itemsList.add(currentHeaderProducts);

                        Map<String, Object> infoProductMap = map.get(header);

                        //added each exist item under the current type header to the view
                        for (String itemName : infoProductMap.keySet()) {

                            //added item name to TableActivity's list
                            String currentItemName = itemName;
                            ((TableActivity)getActivity()).addNameToProductsNames(currentItemName);

                            String currentItemPrice = (String) (infoProductMap.get(itemName));
                            itemsList.add(new Item(currentItemName, currentItemPrice, Item.ITEM_TYPE.PRODUCT,"0"));
                        }

                    }

                    initListView();
                }
                catch (Exception ex) {
                    Log.e(TAG, "FullMenuFragment: import menu error :" + ex.getMessage());
                }
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

    }

    private void initListView(){
        //Updated the view
        listItemAdapter = new ListItemAdapter(getActivity().getApplicationContext(),
               itemsList, ListItemAdapter.VIEW_SOURCE.TABLE_MENU_SOURCE, (TableActivity)getActivity(), null);
        listView.setAdapter(listItemAdapter);
        listItemAdapter.notifyDataSetChanged();
    }

}
