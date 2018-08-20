package com.example.admin.metis;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.admin.metis.MenuActivity.BAR_NAME;
import static com.example.admin.metis.MenuActivity.TABLE;


public class UserOrderFragment extends Fragment {

    private static final String TABLE_NODE = "Tables";
    private static final String USERS_NODE = "Users";
    private static final String ORDERS_NODE = "Orders";
    private final static String TAG = "Metis-Application: ";


    //UI Variables
    private String userName, providerId, userId;
    private Uri userPhotoUrl;
    private TextView userNameTxt;
    private CircleImageView userProfilePic;
    private View view;
    private TextView totalPriceTextView;

   //List Variables
    private ListView listView;
    private ListItemAdapter listItemAdapter;
    private static ArrayList<Item> itemsList;

    //General Variables
    private static Map<String, Integer> productsIndexMap;
    private static int indexCounter = 0;
    static double toatalPrice = 0;

    //Firebase Variables
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener listener;

    public UserOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_order, container, false);

        listView = view.findViewById(R.id.User_Orders_ListView);
        initVariables();
        getUserInfo();
        bindUI();
        setUIUserInfo();
        getItemsFromDB();

        return view;
    }

    public void getUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();

        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                providerId = profile.getProviderId();

                // Name and profile photo Url
                userName = profile.getDisplayName();
                userPhotoUrl = profile.getPhotoUrl();
            }
        }
    }

    public void initVariables(){
        firebaseAuth = FirebaseAuth.getInstance();
        productsIndexMap = new HashMap<>();
        itemsList = new ArrayList<>();
    }

    public void getItemsFromDB() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child(BAR_NAME).child(TABLE_NODE).child(TABLE)
                .child(USERS_NODE).child(userId).child(ORDERS_NODE);
        toatalPrice = 0;

        listener = databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i(TAG, "UserOrderFragment: getItemsFromDB : onChildAdded");
                String itemName = dataSnapshot.getKey();
                productsIndexMap.put(itemName, indexCounter++);
                String[] values = new String[2];
                int i = 0;

                try {
                    Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();

                    for (String attr : map.keySet()) {
                        values[i] = map.get(attr);
                        i++;
                    }

                    Item currentProduct = new Item(itemName, values[1], Item.ITEM_TYPE.PRODUCT, values[0]);
                    itemsList.add(currentProduct);

                    totalPriceTextView.setText("TOTAL PRICE : " + toatalPrice + " ILS");
                    listItemAdapter = new ListItemAdapter(getActivity().getApplicationContext(),
                            itemsList, ListItemAdapter.VIEW_SOURCE.USER_SOURCE, (TableActivity) getActivity(), null);
                    listView.setAdapter(listItemAdapter);
                    listItemAdapter.notifyDataSetChanged();


                } catch (Exception ex) {
                    Log.e(TAG, "UserOrderFragment-onChildADD: import items error :" + ex.getMessage());
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i(TAG, "UserOrderFragment: getItemsFromDB : onChildChanged");
                String itemName = dataSnapshot.getKey();
                String[] values = new String[2];
                int i = 0;

                try {
                    Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();

                    for (String attr : map.keySet()) {
                        values[i] = map.get(attr);
                        i++;
                    }

                    totalPriceTextView.setText("TOTAL PRICE : " + toatalPrice + " ILS");

                    //checks if to remove the product/item from the listView and database.
                    if (Integer.parseInt(values[0]) == 0) {
                        indexCounter -= 1;
                        Log.i(TAG,"if- before, index: " + productsIndexMap.get(itemName) + " , size : " + itemsList.size());
                        itemsList.remove((int) productsIndexMap.get(itemName));
                        Log.i(TAG,"if - after");
                        databaseReference.child(itemName).removeValue();
                        refreashIndexMap();
                        Log.i(TAG,"if- after after, index: " + productsIndexMap.get(itemName) + " , size : " + itemsList.size());
                    } else {
                        Item currentProduct = new Item(itemName, values[1], Item.ITEM_TYPE.PRODUCT, values[0]);
                        Log.i(TAG,"else - before, index: " + productsIndexMap.get(itemName) + " , size : " + itemsList.size());
                        itemsList.set(productsIndexMap.get(itemName), currentProduct);
                        Log.i(TAG,"else - after");
                    }

                    listItemAdapter = new ListItemAdapter(getActivity().getApplicationContext(),
                            itemsList, ListItemAdapter.VIEW_SOURCE.USER_SOURCE, (TableActivity) getActivity(), null);
                    listView.setAdapter(listItemAdapter);
                    listItemAdapter.notifyDataSetChanged();
                    ;
                } catch (Exception ex) {
                    Log.e(TAG, "UserOrderFragment-onChildChange: import items error :" + ex.getMessage());
                }
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


    public void bindUI() {
        userNameTxt = view.findViewById(R.id.UserNameTxtView);
        userProfilePic = view.findViewById(R.id.profile_image);
        totalPriceTextView = view.findViewById(R.id.table_total_price_txt);
    }

    public void setUIUserInfo() {
        //user name & profile image from facebook account
        userNameTxt.setText(userName);
        Picasso.with(getActivity().getApplicationContext()).load(userPhotoUrl).into(userProfilePic);
    }

    public void refreashIndexMap() {
        int currentIndex = 0;
        for (Item currentItem : itemsList) {
            productsIndexMap.put(currentItem.getName(), currentIndex);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        databaseReference.removeEventListener(listener);
    }

    public void onResume() {
        super.onResume();
        //getItemsFromDB();
    }


}
