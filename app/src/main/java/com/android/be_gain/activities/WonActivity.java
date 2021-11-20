package com.android.be_gain.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.android.be_gain.databinding.ActivityQuizBinding;
import com.android.be_gain.databinding.ActivityWonBinding;

public class WonActivity extends AppCompatActivity {

    private ActivityWonBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}