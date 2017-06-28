package ph.edu.up.mp.reperuga.myjournal;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class JournalSingleActivity extends AppCompatActivity {

    private FloatingActionButton mEditBtn;

    private FloatingActionButton mSingleRemoveBtn;

    private String mPost_key = null;

    private DatabaseReference mDatabase;


    private ImageView mJournalSingleImage;
    private TextView mJournalSingleTitle;
    private TextView mJournalSingleDesc;

    private FirebaseAuth mAuth;

    //private Button mSingleRemoveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_single);


        mEditBtn = (FloatingActionButton) findViewById(R.id.fab_edit);
        mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent intent = new Intent(JournalSingleActivity.this, EditJournalActivity.class);
                //startActivity(intent);


            }
        });


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Journal");

        mAuth = FirebaseAuth.getInstance();

        mPost_key = getIntent().getExtras().getString("journal_id");

        mJournalSingleImage = (ImageView) findViewById(R.id.singleJournalImage);
        mJournalSingleTitle = (TextView) findViewById(R.id.singleJournalTitle);
        mJournalSingleDesc = (TextView) findViewById(R.id.singleJournalDesc);

        mSingleRemoveBtn = (FloatingActionButton) findViewById(R.id.singleRemoveBtn);

        //Toast.makeText(JournalSingleActivity.this, post_key, Toast.LENGTH_LONG).show();

        mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String post_title = (String) dataSnapshot.child("title").getValue();
                String post_desc = (String) dataSnapshot.child("desc").getValue();
                String post_image = (String) dataSnapshot.child("image").getValue();
                String post_uid = (String) dataSnapshot.child("uid").getValue();

                mJournalSingleTitle.setText(post_title);
                mJournalSingleDesc.setText(post_desc);

                Picasso.with(JournalSingleActivity.this).load(post_image).into(mJournalSingleImage);

                //Toast.makeText(JournalSingleActivity.this, post_uid, Toast.LENGTH_LONG).show();

                if(mAuth.getCurrentUser().getUid().equals(post_uid)) {

                    mSingleRemoveBtn.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mSingleRemoveBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                alertMessage();

                //mDatabase.child(mPost_key).removeValue();
                //Intent mainIntent = new Intent(JournalSingleActivity.this, MainActivity.class);
                //startActivity(mainIntent);

            }
        });


    }



    public void alertMessage() {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                switch (which) {

                    case DialogInterface.BUTTON_POSITIVE:
                        //YES Button Clicked
                        mDatabase.child(mPost_key).removeValue();
                        Intent mainIntent = new Intent(JournalSingleActivity.this, MainActivity.class);
                        startActivity(mainIntent);

                    case DialogInterface.BUTTON_NEGATIVE:
                        //NO Button Clicked
                        //do nothing


                }

            }

        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete Post?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

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

            startActivity(new Intent(JournalSingleActivity.this, ViewProfileActivity.class));

        }

        if(item.getItemId() == R.id.action_view_home) {

            startActivity(new Intent(JournalSingleActivity.this, MainActivity.class));

        }


        if(item.getItemId() == R.id.action_settings) {

            startActivity(new Intent(JournalSingleActivity.this, AccountSettingsActivity.class));

        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {

        mAuth.signOut();

    }


}
