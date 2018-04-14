package com.example.rmit.playgame.quickplay;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.example.rmit.playgame.BaseActivity;
import com.example.rmit.playgame.PlayGameApplication;
import com.example.rmit.playgame.R;
import com.example.rmit.playgame.network.IQuestion;
import com.example.rmit.playgame.network.Question;

import java.util.ArrayList;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static java.lang.Thread.sleep;

/**
 * Created by RMIT on 02/01/2018.
 */

public class QuickPlayGame extends BaseActivity implements WinFragment.OnFragmentInteractionListener, View.OnClickListener, ViewSwitcher.ViewFactory {

    private static String amount, subject, subCode;
    TextView question, option_a, option_b, option_c, option_d, option_e;
    Retrofit retrofit;
    PlayGameApplication application;
    private WinFragment mWinFragment;
    TextSwitcher gameCountdown;

    String TAG = "Quiz";
    int milliSec = 50;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quick_play_game);

        Intent intent = getIntent();
        amount = intent.getStringExtra("Amount");
        subject = intent.getStringExtra("Subject");
        subCode = intent.getStringExtra("SubCode");


        Log.d("Amount", amount);
        Log.d("Subject", subject);
        Log.d("SubjectCode", subCode);

        mWinFragment = new WinFragment();

        question = (TextView) findViewById(R.id.ques_text_view);
        option_a = (TextView) findViewById(R.id.option_a);
        option_b = (TextView) findViewById(R.id.option_b);
        option_c = (TextView) findViewById(R.id.option_c);
        option_d = (TextView) findViewById(R.id.option_d);
        option_e = (TextView) findViewById(R.id.option_e);
        gameCountdown = (TextSwitcher) findViewById(R.id.gameCountdown);

        gameCountdown.setFactory(this);


        for (int id : CLICKABLES) {
            findViewById(id).setOnClickListener(this);
        }

        getRandomNonRepeatingIntegers(10, 1, 10);
        getQuesFromDatabase();
    }

    static ArrayList<Integer> numbers;

    public static ArrayList<Integer> getRandomNonRepeatingIntegers(int size, int min,
                                                                   int max) {
        numbers = new ArrayList<Integer>();

        while (numbers.size() < size) {
            int random = getRandomInt(min, max);

            if (!numbers.contains(random)) {
                numbers.add(random);
            }
        }
        return numbers;
    }

    public static int getRandomInt(int min, int max) {
        Random random = new Random();

        return random.nextInt((max - min) + 1) + min;
    }

    final static int[] CLICKABLES = {
            R.id.option_a, R.id.option_b,R.id.option_c,R.id.option_d,R.id.option_e, R.id.ques_text_view
    };




    // Switch UI to the given fragment
    private void switchToFragment(Fragment newFrag) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newFrag)
                .commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void clearBackground(){
        option_a.setBackgroundColor(Color.WHITE);
        option_b.setBackgroundColor(Color.WHITE);
        option_c.setBackgroundColor(Color.WHITE);
        option_d.setBackgroundColor(Color.WHITE);
        option_e.setBackgroundColor(Color.WHITE);
    }

    public void enableClick(){
        option_a.setClickable(true);
        option_b.setClickable(true);
        option_c.setClickable(true);
        option_d.setClickable(true);
        option_e.setClickable(true);

    }

    public void disableClick(){
        option_a.setClickable(false);
        option_b.setClickable(false);
        option_c.setClickable(false);
        option_d.setClickable(false);
        option_e.setClickable(false);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.option_a:
                vibrateDevice(milliSec);
                disableClick();
                if (option_a.getText().equals(answer)){
                    option_a.setBackgroundColor(Color.GREEN);
                    trueQues++;
                }else {
                    option_a.setBackgroundColor(Color.RED);
                    falseQues++;
                }
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    getQuesFromDatabase();
                    resetCountdownTimer(10000,1000);

                }
                break;
            case R.id.option_b:
                vibrateDevice(milliSec);
                disableClick();
                if (option_b.getText().equals(answer)){
                    option_b.setBackgroundColor(Color.GREEN);
                    trueQues++;
                }else {
                    option_b.setBackgroundColor(Color.RED);
                    falseQues++;
                }
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    getQuesFromDatabase();
                    resetCountdownTimer(10000,1000);

                }
                break;
            case R.id.option_c:
                disableClick();
                if (option_c.getText().equals(answer)){
                    option_c.setBackgroundColor(Color.GREEN);
                    trueQues++;
                }else {
                    option_c.setBackgroundColor(Color.RED);
                    falseQues++;
                }
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    getQuesFromDatabase();
                    resetCountdownTimer(10000,1000);

                }
                vibrateDevice(milliSec);
                break;
            case R.id.option_d:
                vibrateDevice(milliSec);
                disableClick();
                if (option_d.getText().equals(answer)){
                    option_d.setBackgroundColor(Color.GREEN);
                    trueQues++;
                }else {
                    option_d.setBackgroundColor(Color.RED);
                    falseQues++;
                }
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    getQuesFromDatabase();
                    resetCountdownTimer(10000,1000);

                }
                break;
            case R.id.option_e:
                vibrateDevice(milliSec);
                disableClick();
                if (option_e.getText().equals(answer)){
                    option_e.setBackgroundColor(Color.GREEN);
                    trueQues++;
                }else {
                    option_e.setBackgroundColor(Color.RED);
                    falseQues++;
                }
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                    getQuesFromDatabase();
                    resetCountdownTimer(10000,1000);

                }
                break;
            case R.id.ques_text_view:
//                vibrateDevice(milliSec);
                logDebug(TAG, "Question Clicked");
//                getQuesFromDatabase();
                break;

        }
    }

    int q = 0, trueQues, falseQues, dropQues;
    String answer;

    private void getQuesFromDatabase(){
        if (q<10){
            Log.d("Before", String.valueOf(q));
            Log.d("Value", String.valueOf(numbers.get(q)));
            application = new PlayGameApplication();
            retrofit = application.getRetrofit();
            if (retrofit==null){
                //Toast.makeText(this, "Retrofit is Null", Toast.LENGTH_SHORT).show();
                application.setRetrofit();
                retrofit = application.getRetrofit();
            }else {
                Toast.makeText(this, "Retrofit is not Null", Toast.LENGTH_SHORT).show();
            }

            retrofit.create(IQuestion.class).getQuestion(String.valueOf(numbers.get(q))).enqueue(new Callback<Question>() {
                @Override
                public void onResponse(Call<Question> call, Response<Question> response) {
                    logDebug(TAG, "Response "+response);
                    String ques = response.body().getQuestion();
                    String optionA = response.body().getOptionA();
                    String optionB = response.body().getOptionB();
                    String optionC = response.body().getOptionC();
                    String optionD = response.body().getOptionD();
                    String optionE = response.body().getOptionE();
                    answer = response.body().getAnswer();
                    question.setText(ques);
                    option_a.setText(optionA);
                    option_b.setText(optionB);
                    option_c.setText(optionC);
                    option_d.setText(optionD);
                    option_e.setText(optionE);

                    q++;
                    Log.d("After", String.valueOf(q));
                    clearBackground();
                    enableClick();

                }
                @Override
                public void onFailure(Call<Question> call, Throwable t) {
                    logDebug(TAG, "Failed Response "+String.valueOf(t));
                }
            });
        }else{
            //showAlertDialog(QuickPlayGame.this, "MyGame","Result :\nTrue Ques "+trueQues + "\nFalse Ques "+falseQues );
            if (countDownTimer != null) {
                Log.d("MyTag", "Cancelled Countdown");
                countDownTimer.cancel();
            }
            findViewById(R.id.layout_container).setVisibility(View.GONE);
            Bundle bundle = new Bundle();
            bundle.putString("TrueQues", String.valueOf(trueQues));
            bundle.putString("FalseQues", String.valueOf(falseQues));
            bundle.putString("DropQues", String.valueOf(dropQues));
            WinFragment winFragment = new WinFragment();
            winFragment.setArguments(bundle);
            switchToFragment(winFragment);
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
                textViewCount = (TextView) gameCountdown.getChildAt(0);
//                if(millisUntilFinished < 10001)
//                    textView.setTextColor(Color.RED);

                textViewCount.setText("0"+millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                //textViewCount.setText("Starting Quiz...");
//                showAlertDialog(QuickPlayActivity.this,TAG,"Quiz Starting...");
                Log.d("Alert", "Changing Question");
                dropQues++;
                getQuesFromDatabase();
                //resetCountdownTimer(10000,1000);
            }
        };
        countDownTimer.start();
    }



    void resetCountdownTimer(long max, long min) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        startCountdownTimer(max, min);
    }
}
