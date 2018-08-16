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
    private  View view;

    private ListView listView;
    private ListItemAdapter listItemAdapter;

    Map<String,Integer> productsIndexMap;
    ArrayList<Product> productsList;
    int indexCounter = 0;

    //Firebase Variables
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public UserOrderFragment() {
        // Required empty public constructor
    }

    public static UserOrderFragment newInstance(String param1, String param2) {
        UserOrderFragment fragment = new UserOrderFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productsIndexMap = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        productsList = new ArrayList<>();
        view = inflater.inflate(R.layout.fragment_user_order, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        listView = view.findViewById(R.id.User_Orders_ListView);
        getUserInfo();
        bindUI();
        setUIUserInfo();
        getItemsFromDB();

        return view;
    }

    public void getUserInfo()
    {
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

    public void getItemsFromDB(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child(BAR_NAME).child(TABLE_NODE).child("Table 1")
                .child(USERS_NODE).child(userId).child(ORDERS_NODE);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String itemName = dataSnapshot.getKey();
                productsIndexMap.put(itemName,indexCounter++);
                    String [] values = new String[2];
                    int i=0;

                    try{
                        Map<String,String> map = (Map<String,String>) dataSnapshot.getValue();

                        for (String attr : map.keySet()) {
                            values[i] = map.get(attr);
                            i++;
                        }

                        Product currentProduct = new Product(itemName, values[1], Product.PRODUCT_TYPE.ITEM, values[0]);
                        productsList.add(currentProduct);

                    listItemAdapter = new ListItemAdapter(getActivity().getApplicationContext(),
                            productsList, ListItemAdapter.VIEW_SOURCE.USER_SOURCE, (TableActivity)getActivity());
                    listView.setAdapter(listItemAdapter);
                    listItemAdapter.notifyDataSetChanged();


                }
                catch (Exception ex) {
                    Log.e(TAG, "UserOrderFragment: import items error :" + ex.getMessage());
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String itemName = dataSnapshot.getKey();
                String[] values = new String[2];
                int i = 0;

                try {
                    Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();

                    for (String attr : map.keySet()) {
                        values[i] = map.get(attr);
                        Log.i(TAG, "HERE!!!!!! " + attr + " : " + values[i]);
                        i++;
                    }

                    Product currentProduct = new Product(itemName, values[1], Product.PRODUCT_TYPE.ITEM, values[0]);
                    productsList.set(productsIndexMap.get(itemName),currentProduct);

                    listItemAdapter = new ListItemAdapter(getActivity().getApplicationContext(),
                            productsList, ListItemAdapter.VIEW_SOURCE.USER_SOURCE, (TableActivity) getActivity());
                    listView.setAdapter(listItemAdapter);
                    listItemAdapter.notifyDataSetChanged();
                }
                catch (Exception ex) {
                    Log.e(TAG, "UserOrderFragment: import items error :" + ex.getMessage());
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


    public void bindUI(){
        userNameTxt = view.findViewById(R.id.UserNameTxtView);
        userProfilePic = view.findViewById(R.id.profile_image);
    }

    public void setUIUserInfo(){
        //user name & profile image from facebook account
        userNameTxt.setText(userName);
        Picasso.with(getActivity().getApplicationContext()).load(userPhotoUrl).into(userProfilePic);
    }


}
