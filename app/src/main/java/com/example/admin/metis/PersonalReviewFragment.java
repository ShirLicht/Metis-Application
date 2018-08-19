package com.example.admin.metis;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import static com.example.admin.metis.MenuActivity.BAR_NAME;


public class PersonalReviewFragment extends Fragment {

    private static final String REVIEW_NODE = "Reviews";
    private static final String REVIEWS_COUNTER_NODE = "ReviewsCounter";

    private String review, reviewTitle, userId, userName, providerId;
    private Uri userPhotoUrl;
    private EditText reviewEditText, rateEditText;
    private int rate, currentReviewNum;
    private View view;

    //Firebase Variables
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public PersonalReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_personal_review, container, false);
        bindUI();
        saveReviewToDB();
        return view;
    }

    private void bindUI(){
        reviewEditText = view.findViewById(R.id.reviewEditText);
        rateEditText = view.findViewById(R.id.starsEditText);

        review = reviewEditText.getText().toString();
        rate = Integer.parseInt(rateEditText.getText().toString());
    }

    private void saveReviewToDB(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference= firebaseDatabase.getReference().child(BAR_NAME).child(REVIEW_NODE).child(REVIEWS_COUNTER_NODE);

        getReviewCounterFromDB();

        //Raise the review number by 1 and update the counter in the DB
        currentReviewNum++;
        databaseReference.setValue(currentReviewNum);

        //Generate a new review title
        reviewTitle = "Review " + currentReviewNum;

        //Create and save a new review in to the DB
        databaseReference= firebaseDatabase.getReference().child(BAR_NAME).child(REVIEW_NODE).child(reviewTitle);


        //Data to save to DB
        HashMap<String, String> userMapData = new HashMap<>();
        userMapData.put("user name", userName);
        userMapData.put("image", userPhotoUrl.toString());
        userMapData.put("review", review + "");
        userMapData.put("rate", rate + "");

        databaseReference.setValue(userMapData);
    }

    private void getReviewCounterFromDB(){
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String reviewsCounter = dataSnapshot.getValue().toString();

                currentReviewNum = Integer.parseInt(reviewsCounter);


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

    public void getUserInfo() {
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
}
