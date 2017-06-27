package ph.edu.up.mp.reperuga.myjournal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fab;

    private RecyclerView mJournalList;

    private DatabaseReference mDatabase;

    private DatabaseReference mDatabaseLike;

    private DatabaseReference mDatabaseUsers;

    private DatabaseReference mDatabaseCurrentUser;

    private Query mQueryCurrentUser;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private boolean mProcessLike = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                startActivity(intent);
            }
        });


        // For Disk Persistance (cached data is synced)
        // Enabling offline capabilities
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser() == null) {

                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);

                }

            }
        };

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Journal");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");

        String currentUserId = mAuth.getCurrentUser().getUid();

        mDatabaseCurrentUser = FirebaseDatabase.getInstance().getReference().child("Journal");

        mQueryCurrentUser = mDatabaseCurrentUser.orderByChild("uid").equalTo(currentUserId);

        mDatabaseUsers.keepSynced(true);
        mDatabaseLike.keepSynced(true);
        mDatabase.keepSynced(true);

        mJournalList = (RecyclerView) findViewById(R.id.journal_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mJournalList.setHasFixedSize(true);
        mJournalList.setLayoutManager(layoutManager);

        checkUserExist();

    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<Journal, JournalViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Journal, JournalViewHolder>(

                Journal.class,
                R.layout.journal_row,
                JournalViewHolder.class,
                mQueryCurrentUser

        ) {
            @Override
            protected void populateViewHolder(JournalViewHolder viewHolder, Journal model, int position) {

                final String post_key = getRef(position).getKey();

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setUsername(model.getUsername());

                viewHolder.setLikeBtn(post_key);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //Toast.makeText(MainActivity.this, post_key, Toast.LENGTH_LONG).show();

                        Intent singleJournalIntent = new Intent(MainActivity.this, JournalSingleActivity.class);
                        singleJournalIntent.putExtra("journal_id", post_key);
                        startActivity(singleJournalIntent);



                    }
                });

                viewHolder.mLikeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mProcessLike = true;

                            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(mProcessLike) {

                                        if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                                            mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();

                                            mProcessLike = false;

                                        } else {

                                            mDatabaseLike.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("RandomValue");

                                            mProcessLike = false;

                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }


                });

            }
        };

        mJournalList.setAdapter(firebaseRecyclerAdapter);

    }

    private void checkUserExist() {

        if(mAuth.getCurrentUser() != null) {

            final String user_id = mAuth.getCurrentUser().getUid();

            mDatabaseUsers.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(!dataSnapshot.hasChild(user_id)) {

                        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(setupIntent);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {



                }
            });

        }


    }

    public static class JournalViewHolder extends RecyclerView.ViewHolder {

        View mView;

        ImageButton mLikeBtn;

        DatabaseReference mDatabaseLike;
        FirebaseAuth mAuth;


        public JournalViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            mLikeBtn = (ImageButton) mView.findViewById(R.id.like_btn);

            mDatabaseLike = FirebaseDatabase.getInstance().getReference().child("Likes");
            mAuth = FirebaseAuth.getInstance();

            mDatabaseLike.keepSynced(true);

        }

        public void setLikeBtn(final String post_key) {

            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {

                        mLikeBtn.setImageResource(R.drawable.yellow_star);

                    } else {

                        mLikeBtn.setImageResource(R.drawable.gray_star);

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        public void setTitle(String title) {

            TextView post_title = (TextView) mView.findViewById(R.id.post_title);
            post_title.setText(title);

        }

        public void setDesc(String desc) {

            TextView post_desc = (TextView) mView.findViewById(R.id.post_desc);
            post_desc.setText(desc);

        }

        public void setUsername (String username) {

            TextView post_username = (TextView) mView.findViewById(R.id.post_username);
            post_username.setText(username);

        }

        public void setImage(Context ctx, String image) {

            ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image);

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

            startActivity(new Intent(MainActivity.this, ViewProfileActivity.class));

        }

        if(item.getItemId() == R.id.action_view_journals) {

            startActivity(new Intent(MainActivity.this, MainActivity.class));

        }


        if(item.getItemId() == R.id.action_settings) {

            startActivity(new Intent(MainActivity.this, AccountSettingsActivity.class));

        }
        
        return super.onOptionsItemSelected(item);
    }

    private void logout() {

        mAuth.signOut();

    }


}
