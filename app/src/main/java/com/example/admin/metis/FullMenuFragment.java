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
    private final static String ALCOHOLIC_DRINKS_PASS = "/Alcoholic_Drinks";
    private final static String NON_ALCOHOLIC_DRINKS_PASS = "/Non_Alcoholic_Drinks";
    private final static String FOOD_PASS = "/Food";

    private DatabaseReference mRef;
    private ListView listView;
    private ListItemAdapter listItemAdapter;
    ArrayList<Product> productsList;

    public FullMenuFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRef = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_URL + BAR_NAME + MENU_PASS);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        productsList = new ArrayList<>();
        View view = inflater.inflate(R.layout.fragment_full_menu, container, false);
        listView = view.findViewById(R.id.Table_Menu_ListView);
        importMenu();
        return view;
//
    }

    private void importMenu() {

        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i(TAG,"onChildAdded ");
                String menuTopicName = dataSnapshot.getKey().replaceAll("_"," ");
                productsList.add(new Product(menuTopicName, "", Product.PRODUCT_TYPE.TOPIC));
               // Log.i(TAG, "FullMenuFragment : adding topic : " + menuTopic);

                try {
                    Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) dataSnapshot.getValue();
                    for (String header : map.keySet()) {
                        Product currentHeaderProducts = new Product(header, " ", Product.PRODUCT_TYPE.HEADER);
                        productsList.add(currentHeaderProducts);
                       // Log.i(TAG, "FullMenuFragment : adding header : " + header);
                        Map<String, Object> infoProductMap = map.get(header);

                        for (String itemName : infoProductMap.keySet()) {
                            String currentItemName = itemName;
                            String currentItemPrice = (String) (infoProductMap.get(itemName));
                            productsList.add(new Product(currentItemName, currentItemPrice, Product.PRODUCT_TYPE.ITEM));
                            //Log.i(TAG, "FullMenuFragment : adding product : " + itemName);
                        }

                    }


                    listItemAdapter = new ListItemAdapter(getActivity().getApplicationContext(), productsList, ListItemAdapter.VIEW_SOURCE.TABLE_SOURCE);
                    listView.setAdapter(listItemAdapter);
                    listItemAdapter.notifyDataSetChanged();


                } catch (Exception ex) {
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
}
