package ph.edu.up.mp.reperuga.myjournal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditJournalActivity extends AppCompatActivity {

    private FloatingActionButton mFabUpdate;

    private ImageButton mUpdateImage;
    private EditText mUpdateTitle;
    private EditText mUpdateDesc;

    //private Button mSubmitBtn;

    private Uri mImageUri = null;

    private static final int GALLERY_REQUEST = 1;

    private StorageReference mStorage;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgress;

    private FirebaseAuth mAuth;

    private FirebaseUser mCurrentUser;

    private DatabaseReference mDatabaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_journal);

        mFabUpdate = (FloatingActionButton) findViewById(R.id.fab_update);
        mFabUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updatePost();


            }
        });

        mAuth = FirebaseAuth.getInstance();

        mCurrentUser = mAuth.getCurrentUser();


        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Journal");

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());


        mUpdateImage = (ImageButton) findViewById(R.id.updateImageSelect);

        mUpdateTitle = (EditText) findViewById(R.id.updateTitleField);
        mUpdateDesc = (EditText) findViewById(R.id.updateDescField);

        //mSubmitBtn = (Button) findViewById(R.id.submitBtn);

        mProgress = new ProgressDialog(this);

        mUpdateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);

            }
        });

        /*fab_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updatePost();

            }
        }); */
    }

    private void updatePost() {

        mProgress.setMessage("Updating Post ...");

        final String update_title_val = mUpdateTitle.getText().toString().trim();
        final String update_desc_val = mUpdateDesc.getText().toString().trim();

        if(!TextUtils.isEmpty(update_title_val) && !TextUtils.isEmpty(update_desc_val) && mImageUri != null) {

            mProgress.show();

            StorageReference filepath = mStorage.child("Journal_Images").child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    final DatabaseReference updatePost = mDatabase.push();


                    mDatabaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            updatePost.child("title").setValue(update_title_val);
                            updatePost.child("desc").setValue(update_title_val);
                            updatePost.child("image").setValue(downloadUrl.toString());
                            updatePost.child("uid").setValue(mCurrentUser.getUid());
                            updatePost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()) {

                                        startActivity(new Intent(EditJournalActivity.this, JournalSingleActivity.class));

                                    }

                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                    mProgress.dismiss();



                }
            });

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {

            mImageUri = data.getData();

            mUpdateImage.setImageURI(mImageUri);
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

            startActivity(new Intent(EditJournalActivity.this, ViewProfileActivity.class));

        }

        if(item.getItemId() == R.id.action_view_home) {

            startActivity(new Intent(EditJournalActivity.this, MainActivity.class));

        }


        if(item.getItemId() == R.id.action_settings) {

            startActivity(new Intent(EditJournalActivity.this, AccountSettingsActivity.class));

        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {

        mAuth.signOut();

    }
}
