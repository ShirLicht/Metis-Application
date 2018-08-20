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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;

import static com.example.admin.metis.MenuActivity.BAR_NAME;

public class OnlineUsersFragment extends Fragment {

    private View view;

    private static final String USERS_NODE = "Users";
    private final static String TAG = "Metis-Application: ";

    private ListView listView;
    private UserListItemAdapter listItemAdapter;

    ArrayList<User> productsList;

    //Firebase Variables
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    //private Uri userPhotoUrl;
    private String currentUserId;

    public OnlineUsersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat__bar_, container, false);
        listView = view.findViewById(R.id.Online_Users_ListView);
        firebaseAuth = FirebaseAuth.getInstance();
        productsList = new ArrayList<>();
        getOnlineUsers();
        return view;
    }

    private void getOnlineUsers(){

        firebaseDatabase = FirebaseDatabase.getInstance();

        //get all Users
        databaseReference = firebaseDatabase.getReference().child(BAR_NAME).child(USERS_NODE);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //user id
                     // image
                     // name
                String userId = dataSnapshot.getKey();

                Map<String, String> currentUserDataMap = (Map) dataSnapshot.getValue();


                Uri userPhotoUrl = Uri.parse(currentUserDataMap.get("image").toString());
                String userName = currentUserDataMap.get("name").toString();

                User currentUser = new User(userId,userName,userPhotoUrl);
                productsList.add(currentUser);


                listItemAdapter = new UserListItemAdapter(getActivity().getApplicationContext(),productsList, (ReviewActivity) getActivity());
                listView.setAdapter(listItemAdapter);
                listItemAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //added refreash online list (when user disconnected
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

    private void getCurrentUserID() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        currentUserId = user.getUid();
    }

}
