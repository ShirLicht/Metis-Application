package com.example.admin.metis;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
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
    private static final String DETAILS_NODE = "Details";
    private final static String TAG = "Metis-Application: ";

    private ListView listView;
    private ListItemAdapter listItemAdapter;

    ArrayList<Product> productsList;

    //Firebase Variables
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private Uri userPhotoUrl;
    private Button waitressBtn, orderBtn, billBtn;
    private TextView totalPriceTextView;
    private static double totalPrice = 0;

    public TableOrderFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_table_order, container, false);
        bindUI(view);
        setButtonsEvents();

        productsList = new ArrayList<>();
        getItemsFromDB();

        return view;

    }

    private void bindUI(View view){
        listView = view.findViewById(R.id.Table_Oreders_ListView);
        waitressBtn = view.findViewById(R.id.waitress_call_button);
        orderBtn = view.findViewById(R.id.orderBtn);
        billBtn = view.findViewById(R.id.bill_button);
        totalPriceTextView = view.findViewById(R.id.table_total_price_txt);
    }

    private void setButtonsEvents(){
        orderBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(getActivity(),"The order was successfully submitted",Toast.LENGTH_LONG).show();
            }
        });

        waitressBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(getActivity(),"The waitress is on her way",Toast.LENGTH_LONG).show();
            }

        });

        billBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(getActivity(),"The bill will arrive soon",Toast.LENGTH_LONG).show();
            }

        });


    }

    public void getItemsFromDB() {
        totalPrice = 0;
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

                Map<String, Object> currentUserDetailsMap = currentUserNodesMap.get(DETAILS_NODE);
                userPhotoUrl = Uri.parse(currentUserDetailsMap.get("image").toString());

                Map<String, Object> currentUserOrdersMap = currentUserNodesMap.get(ORDERS_NODE);
                for (String attr : currentUserOrdersMap.keySet()) {
                    String[] values = new String[2];
                    int i = 0;

                    String itemName = attr;

                    Map<String, String> currentUserOrderMap = (Map) currentUserOrdersMap.get(attr);

                    //Current Item attributes : amount/price
                    for (String valueName : currentUserOrderMap.keySet()) {
                        values[i] = currentUserOrderMap.get(valueName);
                        i++;
                    }

                    //Extract the price of the item
                    String price = values[1].split(" ")[0];
                    //Added the current item price to total price (multiply by the order amount of the specific item)
                    totalPrice += (Integer.parseInt(price)) * (Integer.parseInt(values[0]));

                    Product currentProduct = new Product(itemName, values[1], Product.PRODUCT_TYPE.ITEM, values[0]);
                    currentProduct.setUserImage(userPhotoUrl);
                    productsList.add(currentProduct);

                }    //end - Current Us



//
//                //Node For - Details/Orders
//                for (String node : currentUserNodesMap.keySet()) {
//
//                    Map<String, Object> currentUserDataMap = currentUserNodesMap.get(node);
//
//                    if (node.equals("Details")) {
//                        userPhotoUrl = Uri.parse(currentUserDataMap.get("image").toString());
//                    } else {
//                        //Current User Orders For
//                        for (String attr : currentUserDataMap.keySet()) {
//                            String[] values = new String[2];
//                            int i = 0;
//
//                            String itemName = attr;
//
//                            Map<String, String> currentUserOrderMap = (Map) currentUserDataMap.get(attr);
//
//                            //Current Item attributes : amount/price
//                            for (String valueName : currentUserOrderMap.keySet()) {
//                                values[i] = currentUserOrderMap.get(valueName);
//                                i++;
//                            }
//
//                            //Extract the price of the item
//                            String price = values[1].split(" ")[0];
//                            //Added the current item price to total price (multiply by the order amount of the specific item)
//                            totalPrice += (Integer.parseInt(price)) * (Integer.parseInt(values[0]));
//
//                            Product currentProduct = new Product(itemName, values[1], Product.PRODUCT_TYPE.ITEM, values[0]);
//                            currentProduct.setUserImage(userPhotoUrl);
//                            productsList.add(currentProduct);
//
//                        }    //end - Current User Qrders For
//                    }
//                }// end - Node For

                totalPriceTextView.setText("TOTAL PRICE : " + totalPrice + " ILS");
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
