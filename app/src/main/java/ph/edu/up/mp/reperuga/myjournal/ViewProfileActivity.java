package ph.edu.up.mp.reperuga.myjournal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ViewProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabase;

    private ImageView mViewProfileImg;
    private TextView mViewUsername;
    private TextView mViewEmail;
    private TextView mViewUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        mViewProfileImg = (ImageView) findViewById(R.id.viewProfileImage);
        mViewUsername = (TextView) findViewById(R.id.viewUsername);
        mViewEmail = (TextView) findViewById(R.id.viewEmail);
        mViewUserId = (TextView) findViewById(R.id.viewUserId);

        //get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        //get current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        setDataToView(user);

        //add an auth listener
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                Log.d("ViewProfileActivity", "onAuthStateChanged");
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    setDataToView(user);

                    //loading image by Picasso
                    if(user.getPhotoUrl() != null) {

                        Log.d("ViewProfileActivity", "photoURL: " + user.getPhotoUrl());
                        Picasso.with(ViewProfileActivity.this).load(user.getPhotoUrl())
                                .transform(new CropCircleTransformation()).into(mViewProfileImg);

                    }
                } else {

                    //user auth state is not existed or closed, return to Main activity
                    startActivity(new Intent(ViewProfileActivity.this, MainActivity.class));
                    finish();

                }

            }
        };

    }

    @SuppressLint("SetTextI18n")
    private void setDataToView(FirebaseUser user) {

        mViewUsername.setText(user.getDisplayName());
        mViewEmail.setText("USER EMAIL: " + user.getEmail());
        mViewUserId.setText("USER ID: " + user.getUid());

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(item.getItemId() == R.id.action_logout) {

            logout();

        }

        if(item.getItemId() == R.id.action_view_profile) {

            startActivity(new Intent(ViewProfileActivity.this, ViewProfileActivity.class));

        }

        if(item.getItemId() == R.id.action_view_home) {

            startActivity(new Intent(ViewProfileActivity.this, MainActivity.class));

        }


        if(item.getItemId() == R.id.action_settings) {

            startActivity(new Intent(ViewProfileActivity.this, AccountSettingsActivity.class));

        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {

        mAuth.signOut();

    }

}
