package com.android.be_gain.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.be_gain.R;
import com.android.be_gain.databinding.ActivityQuizBinding;
import com.android.be_gain.models.ModelQuiz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.android.be_gain.activities.SplashActivity.listOfQ;

public class QuizActivity extends AppCompatActivity {

    private ActivityQuizBinding binding;
    private int CurrentProgress = 0;
    private ProgressBar progressBar;
    private LinearLayout nextBtn;
    CardView cardOA, cardOB, cardOC, cardOD;
    private TextView exitTv, card_question, optiona, optionb, optionc, optiond;

    List<ModelQuiz> allQuestionsList;
    ModelQuiz modelQuiz;
    int index = 0;

    int correctCount = 0;
    int wrongCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Hooks();

        allQuestionsList = listOfQ;
        Collections.shuffle(allQuestionsList);
        modelQuiz = listOfQ.get(index);

        cardOA.setBackgroundColor(getResources().getColor(R.color.white));
        cardOB.setBackgroundColor(getResources().getColor(R.color.white));
        cardOC.setBackgroundColor(getResources().getColor(R.color.white));
        cardOD.setBackgroundColor(getResources().getColor(R.color.white));

        nextBtn.setClickable(false);

        setAllData();
        
        progressBar = binding.progressBar;
        //nextBtn = findViewById(R.id.nextBtn);
        exitTv = findViewById(R.id.exitQuiz);


        binding.quizBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.exitQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void setAllData() {

        card_question.setText(modelQuiz.getQuestion());
        optiona.setText(modelQuiz.getQA());
        optionb.setText(modelQuiz.getQB());
        optionc.setText(modelQuiz.getQC());
        optiond.setText(modelQuiz.getQD());


    }

    private void Hooks() {

        card_question = findViewById(R.id.card_question);
        optiona = findViewById(R.id.card_optionA);
        optionb = findViewById(R.id.card_optionB);
        optionc = findViewById(R.id.card_optionC);
        optiond = findViewById(R.id.card_optionD);

        cardOA = findViewById(R.id.card2);
        cardOB = findViewById(R.id.card3);
        cardOC = findViewById(R.id.card4);
        cardOD = findViewById(R.id.card5);
        nextBtn = findViewById(R.id.nextBtn);

    }

    public void Correct(CardView cardView)
    {
        cardView.setCardBackgroundColor(getResources().getColor(R.color.green));

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(QuizActivity.this,"Score", Toast.LENGTH_SHORT).show();

                correctCount++;
                index++;
                modelQuiz = listOfQ.get(index);
                resetColor();
                setAllData();

                CurrentProgress = CurrentProgress + 10;
                progressBar.setProgress(CurrentProgress);
                progressBar.setMax(100);

                if (CurrentProgress == 100)
                {

                    AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
                    builder.setTitle("Score");
                    builder.setMessage("0pts");
                    builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();

                        }
                    });
                    builder.create().show();
                }

            }
        });

    }

    public void Wrong(CardView cardOA)
    {
        cardOA.setCardBackgroundColor(getResources().getColor(R.color.red));

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wrongCount++;

                CurrentProgress = CurrentProgress + 10;
                progressBar.setProgress(CurrentProgress);
                progressBar.setMax(100);
                if (index < listOfQ.size()-1)
                {
                    index++;
                    modelQuiz = listOfQ.get(index);
                    resetColor();
                    setAllData();


                }
                else
                {
                    GameWon();
                }

                if (CurrentProgress == 100)
                {

                    AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);
                    builder.setTitle("Score");
                    builder.setMessage("0pts");
                    builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();

                        }
                    });
                    builder.create().show();
                }


            }
        });

    }

    private void GameWon() {

//        Intent intent = new Intent(QuizActivity.this, WonActivity.class);
//        startActivity(intent);
    }

    public void enableButton()
    {
        cardOA.setEnabled(true);
        cardOB.setEnabled(true);
        cardOC.setEnabled(true);
        cardOD.setEnabled(true);

    }
    public void disableButton()
    {
        cardOA.setEnabled(false);
        cardOB.setEnabled(false);
        cardOC.setEnabled(false);
        cardOD.setEnabled(false);

    }

    public void resetColor()
    {
        cardOA.setBackgroundColor(getResources().getColor(R.color.white));
        cardOB.setBackgroundColor(getResources().getColor(R.color.white));
        cardOC.setBackgroundColor(getResources().getColor(R.color.white));
        cardOD.setBackgroundColor(getResources().getColor(R.color.white));

    }


    public void OptionAClick(View view) {
        nextBtn.setClickable(true);

        if (modelQuiz.getQA().equals(modelQuiz.getAns()))
        {
            cardOA.setCardBackgroundColor(getResources().getColor(R.color.green));

            if (index<listOfQ.size()-1)
            {
                Correct(cardOA);
            }
            else
            {
                GameWon();
            }

        }
        else
        {
            Wrong(cardOA);
        }

    }

    public void OptionBClick(View view) {
        nextBtn.setClickable(true);

        if (modelQuiz.getQB().equals(modelQuiz.getAns()))
        {
            cardOB.setCardBackgroundColor(getResources().getColor(R.color.green));

            if (index<listOfQ.size()-1)
            {
//                index++;
//                modelQuiz=listOfQ.get(index);
//                setAllData();
//                resetColor();
                Correct(cardOB);
            }
            else
            {
                GameWon();
            }

        }
        else
        {
            Wrong(cardOB);
        }

    }

    public void OptionCClick(View view) {nextBtn.setClickable(true);

        if (modelQuiz.getQC().equals(modelQuiz.getAns()))
        {
            cardOC.setCardBackgroundColor(getResources().getColor(R.color.green));

            if (index<listOfQ.size()-1)
            {
                Correct(cardOC);
                //resetColor();
            }
            else
            {
                GameWon();
            }

        }
        else
        {
            Wrong(cardOC);
        }

    }
    public void OptionDClick(View view) {

        nextBtn.setClickable(true);
        if (modelQuiz.getQD().equals(modelQuiz.getAns()))
        {
            cardOD.setCardBackgroundColor(getResources().getColor(R.color.green));

            if (index<listOfQ.size()-1)
            {
                Correct(cardOD);
            }
            else
            {
                GameWon();
            }

        }
        else
        {
            Wrong(cardOD);
        }

    }

}