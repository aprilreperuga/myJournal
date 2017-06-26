package ph.edu.up.mp.reperuga.myjournal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AccountSettingsActivity extends AppCompatActivity {

    private Button mChangeEmailBtn, mChangePassBtn, mSendResetBtn, mRemoveUserBtn,
                    mChangeEmail, mChangePass, mSendEmail, mRemove, mSignOut;

    private EditText mOldEmail, mNewEmail, mPassword, mNewPassword;
    private ProgressBar mProgressBar;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle(getString(R.string.app_name));
        //setSupportActionBar(toolbar);

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();

        //Get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user == null) {

                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(AccountSettingsActivity.this, LoginActivity.class));
                    finish();

                }

            }
        };


        mChangeEmailBtn = (Button) findViewById(R.id.change_email_button);
        mChangePassBtn = (Button) findViewById(R.id.change_password_button);
        mSendResetBtn = (Button) findViewById(R.id.sending_pass_reset_button);
        mRemoveUserBtn = (Button) findViewById(R.id.remove_user_button);
        mChangeEmail = (Button) findViewById(R.id.changeEmail);
        mChangePass = (Button) findViewById(R.id.changePass);
        mSendEmail = (Button) findViewById(R.id.send);
        mRemove = (Button) findViewById(R.id.remove);
        mSignOut = (Button) findViewById(R.id.sign_out);


        mOldEmail = (EditText) findViewById(R.id.old_email);
        mNewEmail = (EditText) findViewById(R.id.new_email);
        mPassword = (EditText) findViewById(R.id.password);
        mNewPassword = (EditText) findViewById(R.id.newPassword);

        mOldEmail.setVisibility(View.GONE);
        mNewEmail.setVisibility(View.GONE);
        mPassword.setVisibility(View.GONE);
        mNewPassword.setVisibility(View.GONE);
        mChangeEmail.setVisibility(View.GONE);
        mChangePass.setVisibility(View.GONE);
        mSendEmail.setVisibility(View.GONE);
        mRemove.setVisibility(View.GONE);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (mProgressBar != null) {

            mProgressBar.setVisibility(View.GONE);

        }


        mChangeEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOldEmail.setVisibility(View.GONE);
                mNewEmail.setVisibility(View.VISIBLE);

                mPassword.setVisibility(View.GONE);
                mNewPassword.setVisibility(View.GONE);

                mChangeEmail.setVisibility(View.VISIBLE);
                mChangePass.setVisibility(View.GONE);

                mSendEmail.setVisibility(View.GONE);
                mRemove.setVisibility(View.GONE);

            }
        });

        mChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressBar.setVisibility(View.VISIBLE);

                if (user != null && !mNewEmail.getText().toString().trim().equals("")) {
                    user.updateEmail(mNewEmail.getText().toString().trim())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {

                                        Toast.makeText(AccountSettingsActivity.this, "Email Address is updated. Please sign in with the new email address", Toast.LENGTH_LONG).show();
                                        signOut();
                                        mProgressBar.setVisibility(View.GONE);

                                    } else {

                                        Toast.makeText(AccountSettingsActivity.this, "Failed to update Email!", Toast.LENGTH_LONG).show();
                                        mProgressBar.setVisibility(View.GONE);

                                    }

                                }
                            });

                } else if (mNewEmail.getText().toString().trim().equals("")) {

                    mNewEmail.setError("Enter Email");
                    mProgressBar.setVisibility(View.GONE);

                }

            }
        });

        mChangePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOldEmail.setVisibility(View.GONE);
                mNewEmail.setVisibility(View.GONE);

                mPassword.setVisibility(View.GONE);
                mNewPassword.setVisibility(View.VISIBLE);

                mChangeEmail.setVisibility(View.GONE);
                mChangePass.setVisibility(View.VISIBLE);

                mSendEmail.setVisibility(View.GONE);
                mRemove.setVisibility(View.GONE);

            }
        });

        mChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressBar.setVisibility(View.VISIBLE);

                if (user != null && !mNewPassword.getText().toString().trim().equals("")) {

                    if (mNewPassword.getText().toString().trim().length() < 6) {
                        mNewPassword.setError("Password too short, Enter minimum 6 characters");
                        mProgressBar.setVisibility(View.GONE);

                    } else {

                        user.updatePassword(mNewPassword.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(AccountSettingsActivity.this, "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
                                        signOut();
                                        mProgressBar.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(AccountSettingsActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
                                        mProgressBar.setVisibility(View.GONE);
                                    }

                                }
                            });

                        }

                    } else if (mNewPassword.getText().toString().trim().equals("")) {

                        mNewPassword.setError("Enter Password");
                        mProgressBar.setVisibility(View.GONE);

                }

            }
        });

        mSendResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOldEmail.setVisibility(View.VISIBLE);
                mNewEmail.setVisibility(View.GONE);

                mPassword.setVisibility(View.GONE);
                mNewPassword.setVisibility(View.GONE);

                mChangeEmail.setVisibility(View.GONE);
                mChangePass.setVisibility(View.GONE);

                mSendEmail.setVisibility(View.VISIBLE);
                mRemove.setVisibility(View.GONE);

            }
        });

        mSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressBar.setVisibility(View.VISIBLE);

                if (!mOldEmail.getText().toString().trim().equals("")) {
                    mAuth.sendPasswordResetEmail(mOldEmail.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                Toast.makeText(AccountSettingsActivity.this, "Reset password email is sent!", Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.GONE);

                            } else {

                                Toast.makeText(AccountSettingsActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.GONE);

                            }

                        }
                    });


                } else {

                    mOldEmail.setError("Enter Email");
                    mProgressBar.setVisibility(View.GONE);

                }

            }
        });


        mRemoveUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressBar.setVisibility(View.VISIBLE);

                if (user != null) {
                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                Toast.makeText(AccountSettingsActivity.this, "Your profile is deleted:( Create an account now!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(AccountSettingsActivity.this, RegisterActivity.class));
                                finish();
                                mProgressBar.setVisibility(View.GONE);

                            } else {

                                Toast.makeText(AccountSettingsActivity.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();
                                mProgressBar.setVisibility(View.GONE);

                            }

                        }
                    });


                }

            }
        });


        mSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

    }

    //sign out method
    private void signOut() {

        mAuth.signOut();

    }

    @Override
    protected void onResume() {

        super.onResume();
        mProgressBar.setVisibility(View.GONE);

    }

    @Override
    public void onStart() {

        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onStop() {

        super.onStop();

        if (mAuthListener != null ) {

            mAuth.removeAuthStateListener(mAuthListener);

        }


    }

}
