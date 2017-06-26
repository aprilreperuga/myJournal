package ph.edu.up.mp.reperuga.myjournal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class JournalSingleActivity extends AppCompatActivity {

    private String mPost_key = null;

    private DatabaseReference mDatabase;


    private ImageView mJournalSingleImage;
    private TextView mJournalSingleTitle;
    private TextView mJournalSingleDesc;

    private FirebaseAuth mAuth;

    private Button mSingleRemoveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_single);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Journal");

        mAuth = FirebaseAuth.getInstance();

        mPost_key = getIntent().getExtras().getString("journal_id");

        mJournalSingleImage = (ImageView) findViewById(R.id.singleJournalImage);
        mJournalSingleTitle = (TextView) findViewById(R.id.singleJournalTitle);
        mJournalSingleDesc = (TextView) findViewById(R.id.singleJournalDesc);

        mSingleRemoveBtn = (Button) findViewById(R.id.singleRemoveBtn);

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

                mDatabase.child(mPost_key).removeValue();
                Intent mainIntent = new Intent(JournalSingleActivity.this, MainActivity.class);
                startActivity(mainIntent);

            }
        });


    }
}
