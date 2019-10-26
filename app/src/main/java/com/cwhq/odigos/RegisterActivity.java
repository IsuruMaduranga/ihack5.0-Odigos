package com.cwhq.odigos;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.cwhq.odigos.Models.User;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


public class RegisterActivity extends AppCompatActivity {

    private EditText etname;
    private EditText etemail;
    private EditText etphoneno;
    private RadioButton rbtypeadmin;
    private RadioButton rbtypeuser;
    private RadioButton rbgendermale;
    private RadioButton rbgenderfemale;
    private EditText etpassword;
    private Button btReg;
    private TextView tverrname,tverrmail,tverrphone,tverrpassword,tvtyperr,tvgendererr;
    private String name;
    private String mail;
    private String phone;
    private String type;
    private String gender;
    private String password;
    private ProgressBar pbr;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fireDB;
    private ArrayAdapter<String> adapter;
    private String[] items;
    private Spinner dropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup);

        etname = (EditText)findViewById(R.id.etname);
        etemail = (EditText)findViewById(R.id.etmail);
        etphoneno = (EditText)findViewById(R.id.etphone);
        rbtypeadmin = (RadioButton) findViewById(R.id.radio_admin);
        rbtypeuser = (RadioButton) findViewById(R.id.radio_user);
        rbgendermale = (RadioButton)findViewById(R.id.radio_male);
        rbgenderfemale = (RadioButton)findViewById(R.id.radio_female);
        etpassword = (EditText)findViewById(R.id.etpassword);
        tverrname = (TextView)findViewById(R.id.tvnameerr);
        tverrmail = (TextView)findViewById(R.id.tvmailerr);
        tverrphone = (TextView)findViewById(R.id.tvphoneerr);
        tvtyperr =(TextView)findViewById(R.id.tvtypeerr);
        tvgendererr =(TextView)findViewById(R.id.tvgendererr);
        tverrpassword = (TextView)findViewById(R.id.tvpassworderr);
        btReg = (Button)findViewById(R.id.bReg);
        pbr =(ProgressBar)findViewById(R.id.pbrreg);
        dropdown = findViewById(R.id.lang_spinner);


        items = new String[]{"English", "Chinese"};
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        fireDB = FirebaseFirestore.getInstance();

        etname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etname.setBackgroundResource(R.drawable.edittext);
            }
        });

        etemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etemail.setBackgroundResource(R.drawable.edittext);
            }
        });
        etphoneno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etphoneno.setBackgroundResource(R.drawable.edittext);
            }
        });
        etpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etpassword.setBackgroundResource(R.drawable.edittext);
            }
        });


        btReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name=etname.getText().toString();
                mail=etemail.getText().toString();
                phone=etphoneno.getText().toString();
                type = rbtypeadmin.isChecked() ? "User" : "Guide";
                gender =rbgendermale.isChecked() ? "Male" : "Female";
                password=etpassword.getText().toString();
                etname.setBackgroundResource(R.drawable.edittext);
                etemail.setBackgroundResource(R.drawable.edittext);
                etphoneno.setBackgroundResource(R.drawable.edittext);
                etpassword.setBackgroundResource(R.drawable.edittext);
                validate();
            }
        });
    }

    /* validate user inputs */

    public void validate(){

        boolean Valid=true;
        tverrname.setText("");
        tverrmail.setText("");
        tverrphone.setText("");
        tvtyperr.setText("");
        tvgendererr.setText("");
        tverrpassword.setText("");

        if (name.equals("")){

            tverrname.setText("First Name is required field");
            etname.setBackgroundResource(R.drawable.edittextred);
            Valid=false;
        }
        if (mail.equals("")){

            tverrmail.setText("Email is required field");
            etemail.setBackgroundResource(R.drawable.edittextred);
            Valid=false;

        }else if(!mail.matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")){

            tverrmail.setText("Invalid Email format");
            etemail.setBackgroundResource(R.drawable.edittextred);
            Valid=false;
        }
        if (phone.equals("")){

            tverrphone.setText("Phone number is required field");
            etphoneno.setBackgroundResource(R.drawable.edittextred);
            Valid=false;

        }else if(!phone.matches("^\\+(?:[0-9] ?){6,14}[0-9]$")){

            tverrphone.setText("Check phone number format. ex:+94123456789");
            etphoneno.setBackgroundResource(R.drawable.edittextred);
            Valid=false;
        }
        if(!rbtypeadmin.isChecked() && !rbtypeuser.isChecked())
        {
            tvtyperr.setText("User Type is required");
            Valid=false;
        }
        if(!rbgendermale.isChecked() && !rbgenderfemale.isChecked())
        {
            tvgendererr.setText("Gender is required");
            Valid = false;
        }
        if (password.equals("")){

            tverrpassword.setText("PassWord is required field");
            etpassword.setBackgroundResource(R.drawable.edittextred);
            Valid=false;

        }else if(password.length()<6){

            tverrpassword.setText("PassWord must contains at a least 6 letters");
            etpassword.setBackgroundResource(R.drawable.edittextred);
            Valid=false;
        }
        if (Valid){
            createUser();
        }

    }


    /* clear input fields*/

    public void clearFields(){
        etname.setText("");
        etemail.setText("");
        etphoneno.setText("");
        tvtyperr.setText("");
        tvgendererr.setText("");
        etpassword.setText("");
    }



    /* add user to the real time database */

    public void createUser(){

        btReg.setVisibility(View.GONE);
        pbr.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(mail,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            //Account created.. Need to store additional data

                            User user = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(),name,mail,phone,type,gender,null,"");

                            DocumentReference userRef = fireDB.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            userRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        btReg.setVisibility(View.VISIBLE);
                                        pbr.setVisibility(View.GONE);
                                        clearFields();
                                        showMsg("Successfully Registered");
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);

                                    }else{
                                        //Error occurred while adding data
                                        showMsg("Account created but "+task.getException().getMessage());
                                        btReg.setVisibility(View.VISIBLE);
                                        pbr.setVisibility(View.GONE);
                                    }
                                }
                            });


                        }else{
                            showMsg(task.getException().getMessage());
                            btReg.setVisibility(View.VISIBLE);
                            pbr.setVisibility(View.GONE);
                        }
                    }
                });
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


    //Method for checking internet availability
    public boolean isNetworkAvailable(Context ctx)
    {
        ConnectivityManager cm = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()&& cm.getActiveNetworkInfo().isAvailable()&& cm.getActiveNetworkInfo().isConnected())
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /* show msg to user */
    public void showMsg(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}


