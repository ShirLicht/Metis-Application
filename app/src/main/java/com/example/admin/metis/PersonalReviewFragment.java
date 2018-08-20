package com.example.admin.metis;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static com.example.admin.metis.MenuActivity.BAR_NAME;


public class PersonalReviewFragment extends Fragment {

    private static final String REVIEW_NODE = "Reviews";
    private static final String REVIEWS_COUNTER_NODE = "ReviewsCounter";
    private final static String TAG = "Metis-Application: ";


    private String review, userId, userName, providerId;
    private Uri userPhotoUrl;
    private EditText reviewEditText, rateEditText;
    private int rate;
    private View view;
    private Button submitBtn;
    private int currentReviewNum;

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
        firebaseDatabase = FirebaseDatabase.getInstance();
        bindUI();
        setButtonsActions();
        getUserInfo();
        return view;
    }

    private void bindUI(){
        reviewEditText = view.findViewById(R.id.reviewEditText);
        rateEditText = view.findViewById(R.id.starsEditText);
        submitBtn = view.findViewById(R.id.submitBtn);
    }

    private void setButtonsActions(){
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    review = reviewEditText.getText().toString();
                    if(review.equals("")){
                        Toast.makeText(getActivity(), "Please fill the review before pressed submit", Toast.LENGTH_LONG).show();
                        return;
                    }
                    rate = Integer.parseInt(rateEditText.getText().toString());
                    if(rate > 5 || rate < 0 || (rateEditText.getText().toString().equals(""))){
                        Toast.makeText(getActivity(), "Please enter a rate low than 5 and greater than 0", Toast.LENGTH_LONG).show();
                        rateEditText.setText("");
                        return;
                    }
                    saveReviewToDB();
                }
                catch(Exception ex) {
                    Toast.makeText(getActivity(), "Please enter number between 0 to 5", Toast.LENGTH_LONG).show();
                    rateEditText.setText("");
                }
            }
        });
    }




    private void saveReview(String reviewTitle){

        //Create and save a new review in to the DB
        DatabaseReference ref = firebaseDatabase.getReference().child(BAR_NAME).child(REVIEW_NODE).child(reviewTitle);

        //Data to save to DB
        HashMap<String, String> userMapData = new HashMap<>();
        userMapData.put("user name", userName);
        userMapData.put("image", userPhotoUrl.toString());
        userMapData.put("review", review + "");
        userMapData.put("rate", rate + "");

        ref.setValue(userMapData);
    }

    private void saveReviewToDB(){
        databaseReference = firebaseDatabase.getReference().child(BAR_NAME).child(REVIEW_NODE);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               if(dataSnapshot.getKey().equals(REVIEWS_COUNTER_NODE))
               {
                   String reviewsCounter = dataSnapshot.getValue().toString();
                   currentReviewNum = Integer.parseInt(reviewsCounter) + 1;
                   databaseReference.child(REVIEWS_COUNTER_NODE).setValue(currentReviewNum+ "");
                   saveReview("Review " + currentReviewNum);
                   reviewEditText.setText("");
                   rateEditText.setText("");
               }
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
