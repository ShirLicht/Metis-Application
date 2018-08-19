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

public class ReviewListItemAdapter extends ArrayAdapter<Review> {
    private Context context;
    private ArrayList<Review> itemsList;
    private ChatActivity uiActivity;



    public ReviewListItemAdapter(Context context, ArrayList<Review> itemsList, @Nullable ChatActivity uiActivity) {
        super(context, 0, itemsList);
        this.context = context;
        this.itemsList = itemsList;
        this.uiActivity = uiActivity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        int currentStar, starsToFill;

        listItem = LayoutInflater.from(context).inflate(R.layout.all_reviews_list_bar_item,parent,false);

        final Review currentReview = itemsList.get(position);
        TextView nameTextView = listItem.findViewById(R.id.list_item_name);
        nameTextView.setText(currentReview.getName());

        CircleImageView userProfilePic = listItem.findViewById(R.id.profile_image);
        Picasso.with(uiActivity.getApplicationContext()).load(currentReview.getImage()).into(userProfilePic);

        TextView reviewTextView = listItem.findViewById(R.id.list_item_review);
        nameTextView.setText(currentReview.getReview());

        starsToFill = currentReview.getRate();

        com.github.siyamed.shapeimageview.StarImageView starImageView1,starImageView2,starImageView3,starImageView4,starImageView5;

        starImageView1 = listItem.findViewById(R.id.starImageView);
        starImageView2 = listItem.findViewById(R.id.starImageView2);
        starImageView3 = listItem.findViewById(R.id.starImageView3);
        starImageView4 = listItem.findViewById(R.id.starImageView4);
        starImageView5 = listItem.findViewById(R.id.starImageView5);

        if(starsToFill>0) {
            for (currentStar = 0; currentStar < starsToFill; currentStar++) {
                switch (currentStar) {
                    case 1:
                        starImageView1.setImageResource(R.drawable.gold);
                        break;
                    case 2:
                        starImageView2.setImageResource(R.drawable.gold);
                        break;
                    case 3:
                        starImageView3.setImageResource(R.drawable.gold);
                        break;
                    case 4:
                        starImageView4.setImageResource(R.drawable.gold);
                        break;
                    case 5:
                        starImageView5.setImageResource(R.drawable.gold);
                        break;
                }
            }
        }
        return listItem;
    }

}
