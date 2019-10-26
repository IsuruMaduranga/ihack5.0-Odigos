package com.cwhq.odigos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.cwhq.odigos.Models.User;
import com.cwhq.odigos.Models.UserLocation;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "odigosdebug";

    public static String UID ;
    public static User CurrentUser;
    public static String UserType = "User";

    public static UserLocation CurrentUserLcoation;

    private FirebaseFirestore fireDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fireDb = FirebaseFirestore.getInstance();
        getThisUser();
    }


    /* get user from the firebase database */

    public void getThisUser(){

        DocumentReference userRef = fireDb.collection("users").document(UID);
        userRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d(TAG, e.getMessage());
                    showMsg("There was an error. Please login again");
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    CurrentUser = snapshot.toObject(User.class);
                    UserType = CurrentUser.getType();
                    Log.d(TAG, "Current User Updated");
                    Log.d(TAG, "Current User Type: " + UserType);
                    Intent intent = UserType.equals("Guide") ? new Intent(getApplicationContext(), GuideActivity.class) : new Intent(getApplicationContext(), UserActivity.class);
                    startActivity(intent);
                } else {
                    if(CurrentUser==null) {
                        Log.d(TAG, "No Current User");
                        showMsg("This Account is not  available. Please register again");
                        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
            }
        });

    }

    // Method for display toast message
    public void showMsg(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}
