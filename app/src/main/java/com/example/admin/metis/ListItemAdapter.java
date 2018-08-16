package com.example.admin.metis;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ListItemAdapter extends ArrayAdapter<Product>{

    enum VIEW_SOURCE {TABLE_SOURCE, MENU_SOURCE}

    private final static String TAG = "Metis-Application: ";
    private static final String USERS_NODE = "Users";
    private static final String ORDERS_NODE = "Orders";

    private VIEW_SOURCE view_source;
    private Typeface topicFont, headerFont, itemFont;

    private Context context;
    private List<Product> itemsList;
    private TableActivity uiActivity;
    private HashMap<String,Integer> productsAmountMap;

    public ListItemAdapter(Context context, ArrayList<Product> itemsList, VIEW_SOURCE view_source, @Nullable TableActivity uiActivity) {
        super(context, 0 , itemsList);
        this.context = context;
        this.itemsList = itemsList;
        this.view_source = view_source;
        this.uiActivity = uiActivity;

        if(view_source == VIEW_SOURCE.TABLE_SOURCE){
            initProductsAmountMap();
        }

    }

    private void initProductsAmountMap(){
        productsAmountMap = new HashMap<>();
        ArrayList<String> productsNames = uiActivity.getProductsNames();

        for(String productName: productsNames)
            productsAmountMap.put(productName, 0);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        TextView priceTextView;
        setFont();

       // if(listItem == null)
            //listItem = LayoutInflater.from(context).inflate(R.layout.menu_list_bar_item,parent,false);
        switch(this.view_source){
            case MENU_SOURCE:
                listItem = LayoutInflater.from(context).inflate(R.layout.menu_list_bar_item,parent,false);
                break;
            case TABLE_SOURCE:
                listItem = LayoutInflater.from(context).inflate(R.layout.table_list_bar_item,parent,false);
                break;
        }
        final Product currentItem = itemsList.get(position);
        TextView nameTextView = listItem.findViewById(R.id.list_item_name);
        nameTextView.setText(currentItem.getName());

        switch(currentItem.getProductType()){

            case ITEM:
                //The current Product is a listItem with price
                listItem.setPadding(0,0,0,0);
                nameTextView.setTextSize(20);
                nameTextView.setTextColor(Color.BLACK);
                nameTextView.setTypeface(itemFont);
                priceTextView = listItem.findViewById(R.id.list_item_price);
                priceTextView.setText(currentItem.getPrice());
                priceTextView.setTextColor(Color.BLACK);
                priceTextView.setTypeface(itemFont);

                if(view_source == VIEW_SOURCE.TABLE_SOURCE){
                    nameTextView.setTextSize(17);
                    Button btn = listItem.findViewById(R.id.addItemBtn);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String userId = uiActivity.getUserId();
                            DatabaseReference ref = uiActivity.getDatabaseReference().child(USERS_NODE)
                                    .child(userId).child(ORDERS_NODE).child(currentItem.getName());
                            productsAmountMap.put(currentItem.getName(),productsAmountMap.get(currentItem.getName())+1);
                            HashMap<String,String> userMapData = new HashMap<>();
                            userMapData.put("price",currentItem.getPrice());
                            userMapData.put("amount",productsAmountMap.get(currentItem.getName()).toString());
                            ref.setValue(userMapData);
                        }
                    });
                }
                break;

            case HEADER:
                //If the Current product is a header
                listItem.setPadding(0,50,0,0);
                nameTextView.setTextSize(40);
                nameTextView.setTextColor(Color.RED);
                nameTextView.setTypeface(headerFont);
                priceTextView = listItem.findViewById(R.id.list_item_price);
                priceTextView.setText(" ");

                if(view_source == VIEW_SOURCE.TABLE_SOURCE){
                    listItem.findViewById(R.id.addItemBtn).setVisibility(View.GONE);
                    nameTextView.setTextSize(28);
                }

                break;

            case TOPIC:
                listItem.setPadding(0,50,0,0);
                nameTextView.setTextSize(40);
                nameTextView.setTextColor(Color.BLUE);
                nameTextView.setTypeface(topicFont);
                priceTextView = listItem.findViewById(R.id.list_item_price);
                priceTextView.setText(" ");

                if(view_source == VIEW_SOURCE.TABLE_SOURCE){
                    listItem.findViewById(R.id.addItemBtn).setVisibility(View.GONE);
                    nameTextView.setTextSize(32);
                }

                break;
        }

        return listItem;
    }

    private void setFont(){
        topicFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "font/BreeSerif-Regular.ttf");
        headerFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "font/Jua-Regular.ttf");
        itemFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "font/GloriaHallelujah.ttf");
    }

}
