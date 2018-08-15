package com.example.admin.metis;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListItemAdapter extends ArrayAdapter<Product>{

    enum VIEW_SOURCE {TABLE_SOURCE, MENU_SOURCE}

    private final static String TAG = "Metis-Application: ";
    private VIEW_SOURCE view_source;

    private Context context;
    private List<Product> itemsList;

    public ListItemAdapter(Context context,ArrayList<Product> itemsList, VIEW_SOURCE view_source) {
        super(context, 0 , itemsList);
        this.context = context;
        this.itemsList = itemsList;
        this.view_source = view_source;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        TextView priceTextView;

        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.list_bar_item,parent,false);

        Product currentItem = itemsList.get(position);
        TextView nameTextView = listItem.findViewById(R.id.list_item_name);
        nameTextView.setText(currentItem.getName());

        switch(currentItem.getProductType()){

            case ITEM:
                //The current Product is a listItem with price
                listItem.setPadding(0,0,0,0);
                nameTextView.setTextSize(20);
                nameTextView.setTextColor(Color.BLACK);
                priceTextView = listItem.findViewById(R.id.list_item_price);
                priceTextView.setText(currentItem.getPrice());
                priceTextView.setTextColor(Color.BLACK);
                break;

            case HEADER:
                //If the Current product is a header
                listItem.setPadding(0,50,0,0);
                nameTextView.setTextSize(40);
                nameTextView.setTextColor(Color.RED);
                priceTextView = listItem.findViewById(R.id.list_item_price);
                priceTextView.setText(" ");
                break;

            case TOPIC:
                listItem.setPadding(0,50,0,0);
                nameTextView.setTextSize(40);
                nameTextView.setTextColor(Color.BLUE);
                priceTextView = listItem.findViewById(R.id.list_item_price);
                priceTextView.setText(" ");
                break;
        }

        return listItem;
    }
}
