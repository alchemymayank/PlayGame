package com.example.rmit.playgame.quickplay;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.rmit.playgame.BaseActivity;
import com.example.rmit.playgame.R;

import org.jetbrains.annotations.NotNull;

/**
 * Created by RMIT on 02/01/2018.
 */

public class QuickPlayActivity extends BaseActivity implements View.OnClickListener, ViewSwitcher.ViewFactory, QuizFragment.OnFragmentInteractionListener {

    private static final String TAG = "PlayGame";
    ImageButton previousAmount, nextAmount, previousSubject, nextSubject;
    Button leaveRoom;
    TextView textViewAmount, textViewSubject;

    String[] amountList, subjectList, subjectCode;
    int i=-1, j, k=-1, l;
    TextSwitcher textSwitcherCountdown;
    String subject, amount, subCode;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_play);

        // Subjects and Amounts from String.xml
        Resources resources = getResources();
        amountList = resources.getStringArray(R.array.amountList);
        subjectList = resources.getStringArray(R.array.subjectList);
        subjectCode = resources.getStringArray(R.array.subject_code);

        previousAmount  =(ImageButton) findViewById(R.id.imageButtonPreviousAmount);
        nextAmount  =(ImageButton) findViewById(R.id.imageButtonNextAmount);
        previousSubject  =(ImageButton) findViewById(R.id.imageButtonPreviousSubjectQuickMultiScreen);
        nextSubject  =(ImageButton) findViewById(R.id.imageButtonNextSubjectQuickMultiScreen);
        textViewAmount  = (TextView) findViewById(R.id.textViewAmount);
        textViewSubject = (TextView) findViewById(R.id.textViewSubjectQuickMultiScreen);
        leaveRoom = (Button)findViewById(R.id.button_leave_room_quick_multi_screen);
        textSwitcherCountdown = (TextSwitcher) findViewById(R.id.textSwitcherCountdown);

        previousAmount.setOnClickListener(this);
        nextAmount.setOnClickListener(this);
        previousSubject.setOnClickListener(this);
        nextSubject.setOnClickListener(this);
        leaveRoom.setOnClickListener(this);

        textSwitcherCountdown.setFactory(this);

    }

    View view;

    @Override
    public void onClick(View v) {
        view = v;
        switch (v.getId()){
            case R.id.imageButtonNextAmount:
                //modelClass.alertDialog(this, TAG, "Next Amount Button Clicked");
                logDebug(TAG, "Next Amount Button Clicked");
                if (i<20){
                    i++;
                    amount = amountList[i];
                    textViewAmount.setText(amount);
                    j=i;
                }else{
                    i=0;
                    amount = amountList[i];
                    textViewAmount.setText(amount);
                }
                resetCountdownTimer(10000,1000);
                break;
            case R.id.imageButtonPreviousAmount:
                //modelClass.alertDialog(this, TAG, "Previous Amount Button Clicked");
                logDebug(TAG,"Previous Amount Button Clicked");
                if (j>0){
                    j--;
                    i=j;
                    amount = amountList[j];
                    textViewAmount.setText(amount);
                }else {
                    j=20;
                    amount = amountList[j];
                    textViewAmount.setText(amount);
                }
                resetCountdownTimer(10000,1000);
                break;
            case R.id.imageButtonNextSubjectQuickMultiScreen:
                //modelClass.alertDialog(this, TAG, "Next Subject Button Clicked");
                logDebug(TAG,"Next Subject Button Clicked");
                if (k<6){
                    k++;
                    l=k;
                    subject = subjectList[k];
                    subCode = subjectCode[k];
                    textViewSubject.setText(subject);
                }else {
                    k=0;
                    subject = subjectList[k];
                    subCode = subjectCode[k];
                    textViewSubject.setText(subject);
                }
                resetCountdownTimer(10000,1000);
                break;
            case R.id.imageButtonPreviousSubjectQuickMultiScreen:
                //modelClass.alertDialog(this, TAG, "Previous Subject Button Clicked");
                logDebug(TAG,"Previous Subject Button Clicked");
                if (l>0){
                    l--;
                    k=l;
                    subCode = subjectCode[l];
                    subject = subjectList[l];
                    textViewSubject.setText(subject);
                }else {
                    l=6;
                    subCode = subjectCode[l];
                    subject = subjectList[l];
                    textViewSubject.setText(subject);
                }
                resetCountdownTimer(10000,1000);
                break;
            case R.id.button_leave_room_quick_multi_screen:
                //modelClass.alertDialog(this, TAG, "Leave Room Button Clicked");
                logDebug(TAG,"Leave Room Button Clicked");
                break;

        }
    }

    @Override
    public View makeView() {
        TextView textView = new TextView(getApplicationContext());
        textView.setLayoutParams(new TextSwitcher.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setTextSize(20);
        textView.setTextColor(Color.RED);
        return textView;
    }


    CountDownTimer countDownTimer;
    TextView textViewCount;

    void startCountdownTimer(long max, long min){
        countDownTimer = new CountDownTimer(max, min) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
//                countdownTextView.setText("TIME : " + String.format("%02d", minutes)
//                        + ":" + String.format("%02d", seconds));
                textViewCount = (TextView) textSwitcherCountdown.getChildAt(0);
//                if(millisUntilFinished < 10001)
//                    textView.setTextColor(Color.RED);

                textViewCount.setText("Quiz Starts in " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                textViewCount.setText("Starting Quiz...");
//                showAlertDialog(QuickPlayActivity.this,TAG,"Quiz Starting...");
                Toast.makeText(QuickPlayActivity.this, "Connecting to Quiz", Toast.LENGTH_SHORT).show();
//                logDebug("Amount", amount);
//                logDebug("Subject", subject);
                Intent intent = new Intent(QuickPlayActivity.this, QuickPlayGame.class);
                intent.putExtra("Amount", amount);
                intent.putExtra("Subject", subject);
                intent.putExtra("SubCode", subCode);
                startActivity(intent);
//                Bundle bundle = new Bundle();
//                bundle.putString("Amount", amount);
//                bundle.putString("Subject", subject);
//                QuizFragment quizFragment = new QuizFragment();
//                quizFragment.setArguments(bundle);
//                switchToFragment(quizFragment);
            }
        };
        countDownTimer.start();
    }



    // Switch UI to the given fragment
    private void switchToFragment(Fragment newFrag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, newFrag)
                .commit();
    }

    void resetCountdownTimer(long max, long min) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        startCountdownTimer(max, min);
    }

    @Override
    public void onFragmentInteraction(@NotNull Uri uri) {

    }
}
