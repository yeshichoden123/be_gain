package com.android.be_gain.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.be_gain.R;
import com.android.be_gain.models.ModelQuiz;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity
{
    //Variable for
    private static int SPLASH_SCREEN = 2000;
    //Variables for splash
    Animation topAnim, bottomAnim;
    ImageView logoImage;
    TextView logoTxt1;
    TextView logoTxt2;
    TextView btn_splash;

    // firebase auth
    private FirebaseAuth firebaseAuth;

    public static ArrayList<ModelQuiz> listOfQ;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        listOfQ = new ArrayList<>();

        listOfQ.add(new ModelQuiz("Which of the following is correct Python syntax? ", "A", "B", "C", "D", "A"));
        listOfQ.add(new ModelQuiz("What is Python variable? ", "A", "B", "C", "D", "A"));
        listOfQ.add(new ModelQuiz("What is function? ", "A", "B", "C", "D", "A"));
        listOfQ.add(new ModelQuiz("Why do we use loops? ", "A", "B", "C", "D", "A"));
        listOfQ.add(new ModelQuiz("What are the Python datatypes? ", "A", "B", "C", "D", "A"));
        listOfQ.add(new ModelQuiz("What do the following code print? ", "A", "B", "C", "D", "A"));
        listOfQ.add(new ModelQuiz("Question6? ", "A", "B", "C", "D", "A"));
        listOfQ.add(new ModelQuiz("Question7? ", "A", "B", "C", "D", "A"));
        listOfQ.add(new ModelQuiz("Question 8? ", "A", "B", "C", "D", "A"));
        listOfQ.add(new ModelQuiz("Question 9? ", "A", "B", "C", "D", "A"));


        //Animations for splash
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animatiion);
        logoImage = findViewById(R.id.image_logo);
        logoTxt1 = findViewById(R.id.logo_text1);
        logoTxt2 = findViewById(R.id.logo_text2);
        btn_splash = findViewById(R.id.btn_splash);
        logoImage.setAnimation(topAnim);
        logoTxt1.setAnimation(bottomAnim);
        logoTxt2.setAnimation(bottomAnim);
        btn_splash.setAnimation(bottomAnim);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                checkUser();
//            }
//        },2000);

        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        // start main screen after 2 seconds
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
//                startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                finish();
                checkUser();
            }
        }, SPLASH_SCREEN); // 2000 means 2 seconds
    }

    private void checkUser()
    {
        //get current user, if logged in
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null)
        {
            // user not logged in
            // start main screen
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish(); // finish this activity

            /////////////////////////// for user
//            startActivity(new Intent(SplashActivity.this, DashboardUserActivity.class));
//            finish(); // finish this activity
        }
        else
        {
            // user logged in check user type, same as with login
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.child(firebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot)
                        {
                            // get user type
                            String userType = ""+snapshot.child("userType").getValue();
                            // check user type
                            if (userType.equals("user"))
                            {
                                // this is simple user, open user dashboard
                                startActivity(new Intent(SplashActivity.this, DashboardUserActivity.class));
                                finish();
                            }
                            else if (userType.equals("admin"))
                            {
                                // this is admin, open admin dashboard
                                startActivity(new Intent(SplashActivity.this, DashboardAdminActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }
}