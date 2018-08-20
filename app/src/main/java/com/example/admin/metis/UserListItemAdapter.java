package com.example.admin.metis;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserListItemAdapter extends ArrayAdapter<User> {

    private Context context;
    private ArrayList<User> itemsList;
    private ReviewActivity uiActivity;


    public UserListItemAdapter(Context context, ArrayList<User> itemsList, @Nullable ReviewActivity uiActivity) {
        super(context, 0, itemsList);
        this.context = context;
        this.itemsList = itemsList;
        this.uiActivity = uiActivity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        listItem = LayoutInflater.from(context).inflate(R.layout.all_users_list_bar_item,parent,false);

        final User currentItem = itemsList.get(position);
        TextView nameTextView = listItem.findViewById(R.id.list_item_name);
        nameTextView.setText(currentItem.getName());

        CircleImageView userProfilePic = listItem.findViewById(R.id.profile_image);
        Picasso.with(uiActivity.getApplicationContext()).load(currentItem.getImage()).into(userProfilePic);

        return listItem;
    }
}
