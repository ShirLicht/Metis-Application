package com.example.admin.metis;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

import static com.example.admin.metis.MenuActivity.BAR_NAME;

public class PeopleReviewsFragment extends Fragment {

    private static final String REVIEW_NODE = "Reviews";
    private static final String REVIEWS_COUNTER_NODE = "ReviewsCounter";
    private final static String TAG = "Metis-Application: ";

    ArrayList<Review> reviewsList;
    private ListView listView;
    private ReviewListItemAdapter listItemAdapter;

    //Firebase Variables
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private View view;

    public PeopleReviewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_pepole_reviews, container, false);
        listView = view.findViewById(R.id.People_Reviews_ListView);
        firebaseDatabase = FirebaseDatabase.getInstance();
        reviewsList = new ArrayList<>();
        readReviewsFromDB();

        return view;
    }

    private void readReviewsFromDB(){
        databaseReference = firebaseDatabase.getReference().child(BAR_NAME).child(REVIEW_NODE);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(!dataSnapshot.getKey().equals(REVIEWS_COUNTER_NODE))
                {
                    Uri image;
                    String user_name, review;
                    int rate;


                    Map<String, String> currentReviewDataMap = (Map) dataSnapshot.getValue();

                    user_name = currentReviewDataMap.get("user name").toString();
                    image = Uri.parse(currentReviewDataMap.get("image").toString());
                    review = currentReviewDataMap.get("review").toString();
                    rate = Integer.parseInt(currentReviewDataMap.get("rate").toString());

                    Review currentReview = new Review(user_name, image, review, rate);
                    reviewsList.add(currentReview);
                }

                listItemAdapter = new ReviewListItemAdapter(getActivity().getApplicationContext(),reviewsList ,(ChatActivity) getActivity());
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
