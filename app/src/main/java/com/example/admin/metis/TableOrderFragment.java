package com.example.admin.metis;

import android.app.LauncherActivity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

import static com.example.admin.metis.MenuActivity.BAR_NAME;
import static com.example.admin.metis.TableActivity.TABLE;


public class TableOrderFragment extends Fragment {


    private static final String TABLE_NODE = "Tables";
    private static final String USERS_NODE = "Users";
    private static final String ORDERS_NODE = "Orders";
    private final static String TAG = "Metis-Application: ";

    private ListView listView;
    private ListItemAdapter listItemAdapter;

    ArrayList<Product> productsList;

    //Firebase Variables
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private Uri userPhotoUrl;


    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public TableOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_table_order, container, false);
        listView = view.findViewById(R.id.Table_Oreders_ListView);
        productsList = new ArrayList<>();

        getItemsFromDB();


        return view;

    }

    public void getItemsFromDB() {
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase.getReference().child(BAR_NAME).child(TABLE_NODE).child(TABLE)
                .child(USERS_NODE);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String userId = dataSnapshot.getKey();
                if(!dataSnapshot.hasChild(ORDERS_NODE))
                {
                    Toast.makeText(getContext(),"No orders by user made",Toast.LENGTH_LONG).show();
                    return;
                }
                Map<String, Map<String, Object>> currentUserNodesMap = (Map) dataSnapshot.getValue();

                //Node For - Details/Orders
                for (String node : currentUserNodesMap.keySet()) {

                    Map<String, Object> currentUserDataMap = currentUserNodesMap.get(node);

                    if (node.equals("Details")) {
                        userPhotoUrl = Uri.parse(currentUserDataMap.get("image").toString());
                    } else {
                        //Current User Qrders For
                        for (String attr : currentUserDataMap.keySet()) {
                            String[] values = new String[2];
                            int i = 0;

                            String itemName = attr;

                            Map<String, String> currentUserOrderMap = (Map) currentUserDataMap.get(attr);

                            //Current Item attributes : amount/price
                            for (String valueName : currentUserOrderMap.keySet()) {
                                values[i] = currentUserOrderMap.get(valueName);
                                i++;
                            }

                            Product currentProduct = new Product(itemName, values[1], Product.PRODUCT_TYPE.ITEM, values[0]);
                            productsList.add(currentProduct);

                        }    //end - Current User Qrders For
                    }
                }// end - Node For

                listItemAdapter = new ListItemAdapter(getActivity().getApplicationContext(),productsList ,
                        ListItemAdapter.VIEW_SOURCE.ALL_ORDERS_SOURCE, (TableActivity) getActivity(), userPhotoUrl);
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


    }
}
