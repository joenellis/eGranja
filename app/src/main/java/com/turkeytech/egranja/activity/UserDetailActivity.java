package com.turkeytech.egranja.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.turkeytech.egranja.R;
import com.turkeytech.egranja.model.User;
import com.turkeytech.egranja.util.NetworkHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.turkeytech.egranja.session.Constants.USERS_NODE;

public class UserDetailActivity extends AppCompatActivity {

    private static final String TAG = "xix: UserDetail";

    @BindView(R.id.userDetail_root)
    CoordinatorLayout rootLayout;

    @BindView(R.id.userDetail_appBarLayout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.userDetail_nestedScrollView)
    NestedScrollView mNestedScrollView;

    @BindView(R.id.userDetail_progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.userDetail_fullName)
    TextView mName;

    @BindView(R.id.userDetail_number)
    TextView mNumber;

    @BindView(R.id.userDetail_email)
    TextView mEmail;

    @BindView(R.id.userDetail_fab)
    FloatingActionButton mFab;

    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        ButterKnife.bind(this);

        start();
    }

    private void start() {
        if (NetworkHelper.hasNetwork(this)) {

            mAppBarLayout.setVisibility(View.VISIBLE);
            mNestedScrollView.setVisibility(View.VISIBLE);
            mFab.setVisibility(View.VISIBLE);
            findViewById(R.id.userDetail_noData).setVisibility(View.GONE);

            initFirebase();

            fillUiFromDatabase();
        } else {
            mAppBarLayout.setVisibility(View.GONE);
            mNestedScrollView.setVisibility(View.GONE);
            mFab.setVisibility(View.GONE);
            findViewById(R.id.userDetail_noData).setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.retry_button)
    public void retry(){
        start();
    }

    private void initFirebase() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabaseUser = mDatabase.child(USERS_NODE).child(mCurrentUser.getUid());
    }

    public void fillUiFromDatabase() {

        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                mName.setText(mCurrentUser.getDisplayName());
                mEmail.setText(mCurrentUser.getEmail());
                mNumber.setText(user.getNumber());

                mProgressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                showMessage(databaseError.getMessage());
                Log.e(TAG, "onCancelled: " + databaseError.getDetails(), databaseError.toException());

            }
        });
    }


    @OnClick(R.id.userDetail_btnToolbarBack)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @OnClick(R.id.userDetail_fab)
    public void editUser() {
        startActivity(new Intent(this, EditUserActivity.class));
    }

    private void showMessage(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG).show();
    }

}
