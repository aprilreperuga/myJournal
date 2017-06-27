package ph.edu.up.mp.reperuga.myjournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ViewProfileActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseUsers;
    private DatabaseReference mDatabase;

    private FirebaseAuth mAuth;

    private ImageView mViewProfileImg;
    private TextView mViewUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        mAuth = FirebaseAuth.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Journal");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        mViewProfileImg = (ImageView) findViewById(R.id.viewProfileImage);
        mViewUsername = (TextView) findViewById(R.id.viewUsername);

        mDatabaseUsers.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String user_profileimg = (String) dataSnapshot.child("image").getValue();
                String user_name = (String) dataSnapshot.child("name").getValue();

                mViewUsername.setText(user_name);

                Picasso.with(ViewProfileActivity.this).load(user_profileimg).into(mViewProfileImg);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
