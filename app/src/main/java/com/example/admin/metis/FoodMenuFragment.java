package com.example.admin.metis;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class FoodMenuFragment extends Fragment {

   private  final String DB_Url = "https://metis-application.firebaseio.com/";
   private DatabaseReference mRef;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mRef  = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_Url);
        DatabaseReference mRefChild = mRef.child("maor");
        mRefChild.setValue("pitta");

        return inflater.inflate(R.layout.fragment_food_menu, container, false);
    }
}
