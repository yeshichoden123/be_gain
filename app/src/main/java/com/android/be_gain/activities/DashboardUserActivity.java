package com.android.be_gain.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.be_gain.MyApplication;
import com.android.be_gain.R;
import com.android.be_gain.adapters.AdapterCategoryUser;
import com.android.be_gain.databinding.ActivityDashboardUserBinding;
import com.android.be_gain.models.ModelCategoryUser;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboardUserActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // view binding
    private ActivityDashboardUserBinding binding;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Menu menu;
    Toolbar toolbar;
    TextView user, userEmail;
    ImageView profileIv;

    // firebase auth
    private FirebaseAuth firebaseAuth;

    // arraylist to store category
    private ArrayList<ModelCategoryUser> categoryArrayList;

    // adapter
    private AdapterCategoryUser adapterCategoryUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        NavigationView mNavigationView = findViewById(R.id.nav_view);
        View headerView = mNavigationView.getHeaderView(0);
        // get user name and email textViews
        user = headerView.findViewById(R.id.user);
        userEmail = headerView.findViewById(R.id.userEmail);
        profileIv = headerView.findViewById(R.id.profileIv);


        // navigation drawer menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_home);

        // init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        // get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null)
        {
            checkUser();
        }
                        ////////////// uncomment this if you want to check the user
        loadCategories();

        // edit text change listener, search
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // called as an when use type each letter
                try {
                    adapterCategoryUser.getFilter().filter(s);
                }
                catch (Exception e)
                {

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // handle click, logout
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(DashboardUserActivity.this);
                builder.setTitle("Logout");
                builder.setMessage("Are you sure you want to logout?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        firebaseAuth.signOut();
                        checkUser();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        //checkUser();
                    }
                });
                builder.create().show();

            }
        });

//        // hancle click, open profile
//        binding.profileBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(DashboardUserActivity.this, ProfileActivity.class));
//            }
//        });
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    private void loadCategories() {
        //init arraylist
        categoryArrayList = new ArrayList<>();

        // get all categories from firebase > Categories
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // clear arraylist before adding data into it
                categoryArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren())
                {
                    // get data
                    ModelCategoryUser model = ds.getValue(ModelCategoryUser.class);

                    // add to arraylist
                    categoryArrayList.add(model);
                }
                // setup adapter
                adapterCategoryUser = new AdapterCategoryUser(DashboardUserActivity.this, categoryArrayList);

                // set adapter to recyclerview
                binding.categoriesRv.setAdapter(adapterCategoryUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkUser()
    {
        // get current user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null)
        {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            // get all info of user here from snapshot
                            String email = ""+snapshot.child("email").getValue();
                            String name = ""+snapshot.child("name").getValue();
                            String profileImage = ""+snapshot.child("profileImage").getValue();

//                            // set image, using glide
                            Glide.with(DashboardUserActivity.this)
                                    .load(profileImage)
                                    .placeholder(R.drawable.ic_person_gray)
                                    .into(profileIv);

                            // set in textView of toolbar
                            binding.subTitleTv.setText(email);
                            user.setText(name);
                            userEmail.setText(email);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
        else
        {
            // not logged in, go to main screen
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {

            case R.id.nav_home:
                break;

            case R.id.nav_profile:
                Intent intentProfile = new Intent(DashboardUserActivity.this, ProfileActivity.class);
                startActivity(intentProfile);
                break;

//            case R.id.nav_quiz:
//                Intent intentQuiz = new Intent(DashboardUserActivity.this, QuizActivity.class);
//                startActivity(intentQuiz);
//                break;

//            case R.id.nav_score:
//                Toast.makeText(DashboardUserActivity.this,"Score", Toast.LENGTH_SHORT).show();
//                break;

            case R.id.nav_about:
                Intent intentAbout = new Intent(DashboardUserActivity.this, AboutActivity.class);
                startActivity(intentAbout);
                break;

            case R.id.nav_feedback:
                Intent intentFeedback = new Intent(DashboardUserActivity.this, FeedbackActivity.class);
                startActivity(intentFeedback);
                break;

            default:
                return false;

        }
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }
}
