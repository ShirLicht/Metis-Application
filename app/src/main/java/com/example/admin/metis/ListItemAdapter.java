package com.example.admin.metis;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ListItemAdapter extends ArrayAdapter<Product> {

    enum VIEW_SOURCE {TABLE_MENU_SOURCE, MENU_SOURCE, USER_SOURCE, ALL_ORDERS_SOURCE}

    private static final  String TAG = "Metis-Application: ";
    private static final String USERS_NODE = "Users";
    private static final String ORDERS_NODE = "Orders";

    private static final int NUM_OF_TOPICS = 3;
    private static int Counter = 0;

    private VIEW_SOURCE view_source;
    private Typeface topicFont, headerFont, itemFont;

    private Context context;
    private ArrayList<Product> itemsList;
    private TableActivity uiActivity;
    private static HashMap<String, Integer> productsAmountMap = new HashMap<>();

    private CircleImageView userProfilePic;
    private Uri userPhotoUrl;
    private TextView amountTextView;

    public ListItemAdapter(Context context, ArrayList<Product> itemsList, VIEW_SOURCE view_source, @Nullable TableActivity uiActivity, Uri userPhotoUrl) {
        super(context, 0, itemsList);
        this.context = context;
        this.itemsList = itemsList;
        this.view_source = view_source;
        this.uiActivity = uiActivity;
        this.userPhotoUrl = userPhotoUrl;

        //  Only in the first create of listView to initialize the amountMap -> contains how many time each product has been ordered.
        if (view_source == VIEW_SOURCE.TABLE_MENU_SOURCE && (Counter <= NUM_OF_TOPICS)) {
            initProductsAmountMap();
            Counter++;
        }

    }

    private void initProductsAmountMap() {
        ArrayList<String> productsNames = uiActivity.getProductsNames();

        for (String productName : productsNames) {
            productsAmountMap.put(productName, 0);
        }

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        TextView priceTextView;
        setFont();

        switch (this.view_source) {
            case MENU_SOURCE:
                listItem = LayoutInflater.from(context).inflate(R.layout.menu_list_bar_item, parent, false);
                break;
            case TABLE_MENU_SOURCE:
                listItem = LayoutInflater.from(context).inflate(R.layout.table_list_bar_item, parent, false);
                break;
            case USER_SOURCE:
                listItem = LayoutInflater.from(context).inflate(R.layout.user_list_bar_item, parent, false);
                break;
            case ALL_ORDERS_SOURCE:
                listItem = LayoutInflater.from(context).inflate(R.layout.all_orders_list_bar_item, parent, false);
                break;
        }

        final Product currentItem = itemsList.get(position);
        TextView nameTextView = listItem.findViewById(R.id.list_item_name);
        nameTextView.setText(currentItem.getName());


        switch (currentItem.getProductType()) {

            case ITEM:
                //The current Product is a listItem with price
                listItem.setPadding(0, 0, 0, 0);
                nameTextView.setTextSize(20);
                nameTextView.setTextColor(Color.BLACK);
                nameTextView.setTypeface(itemFont);
                priceTextView = listItem.findViewById(R.id.list_item_price);
                priceTextView.setText(currentItem.getPrice());
                priceTextView.setTextColor(Color.BLACK);
                priceTextView.setTypeface(itemFont);

                if (view_source == VIEW_SOURCE.USER_SOURCE) {
                    amountTextView = listItem.findViewById(R.id.list_item_amount);
                    nameTextView.setTextSize(18);
                    priceTextView.setTextSize(18);
                    amountTextView.setText("X " + currentItem.getAmount());
                    amountTextView.setTextColor(Color.rgb(1, 88, 0));
                    amountTextView.setTypeface(itemFont);

                    Button btn = listItem.findViewById(R.id.deleteItemBtn);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String userId = uiActivity.getUserId();
                            DatabaseReference ref = uiActivity.getTableDatabaseReference().child(USERS_NODE)
                                    .child(userId).child(ORDERS_NODE).child(currentItem.getName());


                            //updated item amount of orders
                            int newAmount = productsAmountMap.get(currentItem.getName()) - 1;
                            String ItemPrice = currentItem.getPrice().split(" ")[0];
                            UserOrderFragment.toatalPrice -= Double.parseDouble(ItemPrice);
                            //Log.i(TAG, "Item name : " + currentItem.getName() + " , amount : " + newAmount);
                            productsAmountMap.put(currentItem.getName(), newAmount);

                            //Updated the the DB
                            HashMap<String, String> userMapData = new HashMap<>();
                            userMapData.put("price", currentItem.getPrice());
                            userMapData.put("amount", newAmount + "");
                            ref.setValue(userMapData);
                        }
                    });
                }

                if (view_source == VIEW_SOURCE.TABLE_MENU_SOURCE) {
                    nameTextView.setTextSize(17);
                    Button btn = listItem.findViewById(R.id.addItemBtn);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String userId = uiActivity.getUserId();
                            DatabaseReference ref = uiActivity.getTableDatabaseReference().child(USERS_NODE)
                                    .child(userId).child(ORDERS_NODE).child(currentItem.getName());


                            //updated item amount of orders
                            int newAmount = productsAmountMap.get(currentItem.getName()) + 1;
                            String ItemPrice = currentItem.getPrice().split(" ")[0];
                            UserOrderFragment.toatalPrice += Double.parseDouble(ItemPrice);
                            //Log.i(TAG, "Item name : " + currentItem.getName() + " , amount : " + newAmount);
                            productsAmountMap.put(currentItem.getName(), newAmount);

                            //Updated the the DB
                            HashMap<String, String> userMapData = new HashMap<>();
                            userMapData.put("price", currentItem.getPrice());
                            userMapData.put("amount", newAmount + "");
                            ref.setValue(userMapData);
                        }
                    });
                }

                if (view_source == VIEW_SOURCE.ALL_ORDERS_SOURCE) {
                    userProfilePic = listItem.findViewById(R.id.profile_image);
                    Picasso.with(uiActivity.getApplicationContext()).load(currentItem.getUserImage()).into(userProfilePic);
                    amountTextView = listItem.findViewById(R.id.list_item_amount);
                    nameTextView.setTextSize(18);
                    priceTextView.setTextSize(15);
                    amountTextView.setText("X " + currentItem.getAmount());
                    amountTextView.setTextColor(Color.rgb(1, 88, 0));
                    amountTextView.setTypeface(itemFont);
                    amountTextView.setTextSize(15);
                }
                break;

            case HEADER:
                //If the Current product is a header
                listItem.setPadding(0, 50, 0, 0);
                nameTextView.setTextSize(40);
                nameTextView.setTextColor(Color.RED);
                nameTextView.setTypeface(headerFont);
                priceTextView = listItem.findViewById(R.id.list_item_price);
                priceTextView.setText(" ");

                if (view_source == VIEW_SOURCE.TABLE_MENU_SOURCE) {
                    listItem.findViewById(R.id.addItemBtn).setVisibility(View.GONE);
                    nameTextView.setTextSize(28);
                }

                break;

            case TOPIC:
                listItem.setPadding(0, 50, 0, 0);
                nameTextView.setTextSize(40);
                nameTextView.setTextColor(Color.BLUE);
                nameTextView.setTypeface(topicFont);
                priceTextView = listItem.findViewById(R.id.list_item_price);
                priceTextView.setText(" ");

                if (view_source == VIEW_SOURCE.TABLE_MENU_SOURCE) {
                    listItem.findViewById(R.id.addItemBtn).setVisibility(View.GONE);
                    nameTextView.setTextSize(32);
                }

                break;
        }

        return listItem;
    }

    private void setFont() {
        topicFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "font/BreeSerif-Regular.ttf");
        headerFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "font/Jua-Regular.ttf");
        itemFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "font/GloriaHallelujah.ttf");
    }

}
