package edu.wit.mobileapp.spotmebro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.Contacts;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sign_up extends AppCompatActivity
{
    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Database stuff:
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("");
    DatabaseReference myPref = database.getReference("");
    DatabaseReference myList = database.getReference("");
    private FirebaseAuth firebaseAuth;
    //
    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Widget Declarations
    private EditText mEmail_input;
    private EditText mPassword_input;
    private EditText mName_input;
    private EditText mVerify_input;
    private Spinner mSecurity_input;
    private EditText mAnswer_input;
    private Spinner mStyle_input;
    private Spinner mGender_input;
    private Spinner mPref_gender_input;

    private boolean isthere;


    private Button mSubmit_button;
    //
    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // Variable Declarations
    private static final String TAG = "Sign_up";
    private Spinner security1, security2;
    //
    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------



    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //message = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();

        mName_input = findViewById(R.id.Name_Input);
        mEmail_input = findViewById(R.id.Email_input);
        mEmail_input = findViewById(R.id.Email_input);
        mPassword_input = findViewById(R.id.Password_input);
        mVerify_input = findViewById(R.id.Verify_input);
        mSecurity_input = findViewById(R.id.Security_input);
        mAnswer_input = findViewById(R.id.Answer_input);
        mStyle_input = findViewById(R.id.Style_Input);
        mGender_input = findViewById(R.id.my_gender);
        mPref_gender_input = findViewById(R.id.Preferred_Gender);


        addListenerOnButton();
        addListenerSecurityItemSelection();

        //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        // Buttons:


        mSubmit_button = findViewById(R.id.Submit_button);
        mSubmit_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                userRegister();
            }
        });




        //
        //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    }
    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void addListenerSecurityItemSelection(){
        security1 = findViewById(R.id.Security_input);
    }

    public void addListenerOnButton(){
        security1 = findViewById(R.id.Security_input);
    }

    private void userRegister()
    {
        final String Email = mEmail_input.getText().toString();
        final String Password = mPassword_input.getText().toString();
        final String Verify = mVerify_input.getText().toString();
        final String Security = mSecurity_input.getSelectedItem().toString();
        final String Answer = mAnswer_input.getText().toString();
        final String Style = mStyle_input.getSelectedItem().toString();
        final String Gender = mGender_input.getSelectedItem().toString();
        final String Preferred_Gender = mPref_gender_input.getSelectedItem().toString();
        final String name = mName_input.getText().toString();



        isthere = false;
        myRef = database.getReference("Users");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    try
                    {
                        if( ds.child("Name").getValue().toString().equalsIgnoreCase(name))
                        {
                            isthere = true;
                        }
                    }
                   catch (NullPointerException e)
                   {

                   }
                }

                if (isthere == true)
                {
                    Toast.makeText(Sign_up.this, "Name is already used", Toast.LENGTH_LONG).show();
                }
                else
                {
                    if(TextUtils.isEmpty(Email))
                    {
                        Toast.makeText(Sign_up.this, "Please enter email", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(TextUtils.isEmpty(Password))
                    {
                        Toast.makeText(Sign_up.this, "Please enter password", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(TextUtils.isEmpty(Verify))
                    {
                        Toast.makeText(Sign_up.this, "Please enter password verification", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(TextUtils.isEmpty(Security))
                    {
                        Toast.makeText(Sign_up.this, "Please enter security question", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(TextUtils.isEmpty(Answer))
                    {
                        Toast.makeText(Sign_up.this, "Please enter answer to security question", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(TextUtils.isEmpty(Style))
                    {
                        Toast.makeText(Sign_up.this, "Please enter working out style (cardio or powerlifting)", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(TextUtils.equals(Password, Verify))
                    {
                        //see if this works
                        firebaseAuth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener(Sign_up.this, new OnCompleteListener<AuthResult>()
                        {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task)
                            {
                                if(task.isSuccessful())
                                {
                                    Toast.makeText(Sign_up.this, "Successful Registration", Toast.LENGTH_LONG).show();
                                    //message.hide();

                                    String user = firebaseAuth.getCurrentUser().getUid();

                                    myRef = database.getReference("Users").child(user);
                                    myRef.child("Email").setValue(Email);
                                    myRef.child("Security").setValue(Security);
                                    myRef.child("Answer").setValue(Answer);
                                    myRef.child("Gender").setValue(Gender);
                                    myRef.child("Name").setValue(name);

                                    myPref = database.getReference("Users").child(user).child("Preferences");
                                    myPref.child("Preferred_Gender").setValue(Preferred_Gender);
                                    myPref.child("Style").setValue(Style);

                                    Intent signedup = new Intent(Sign_up.this, Login.class);
                                    startActivity(signedup);
                                }
                                if(!task.isSuccessful())
                                {
                                    //Toast.makeText(Sign_up.this, "Failed Registration", Toast.LENGTH_LONG).show();
                                    //message.hide();
                                    return;
                                }
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(Sign_up.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                        return;
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });









    }

}

