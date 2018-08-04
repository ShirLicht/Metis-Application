package com.example.admin.metis;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
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

    private Context context;
    private List<Product> itemsList = new ArrayList<>();

    public ListItemAdapter(Context context,ArrayList<Product> itemsList) {
        super(context, 0 , itemsList);
        this.context = context;
        this.itemsList = itemsList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);

        Product currentItem = itemsList.get(position);
        boolean isHeader = currentItem.getPrice() == " " ? true : false;

        TextView nameTextView = (TextView)listItem.findViewById(R.id.list_item_name);
        nameTextView.setText(currentItem.getName());

        if(isHeader){
            //If the Current product is a header
            listItem.setPadding(0,50,0,0);
            nameTextView.setTextSize(40);
            nameTextView.setTextColor(Color.RED);
        }
        else{
            //The current Product is a listItem with price
            listItem.setPadding(0,0,0,0);
            nameTextView.setTextSize(20);
            nameTextView.setTextColor(Color.BLACK);
            TextView priceTextView = (TextView)listItem.findViewById(R.id.list_item_price);
            priceTextView.setText(currentItem.getPrice());
        }

        return listItem;
    }
}
