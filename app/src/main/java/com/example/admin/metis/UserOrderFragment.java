package com.example.admin.metis;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;


public class UserOrderFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String userName, providerId;
    private Uri userPhotoUrl;
    private TextView userNameTxt;
    private CircleImageView userProfilePic;
    private  View view;
    private FirebaseAuth firebaseAuth;

    public UserOrderFragment() {
        // Required empty public constructor
    }

    public static UserOrderFragment newInstance(String param1, String param2) {
        UserOrderFragment fragment = new UserOrderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_order, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        getUserInfo();
        bindUI();

        //user name & profile image from facebook account
        userNameTxt.setText(userName);
        Picasso.with(getActivity().getApplicationContext()).load(userPhotoUrl).into(userProfilePic);

        return view;
    }

    public void getUserInfo()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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

    public void bindUI(){
        userNameTxt = view.findViewById(R.id.UserNameTxtView);
        userProfilePic = view.findViewById(R.id.profile_image);
    }

}
