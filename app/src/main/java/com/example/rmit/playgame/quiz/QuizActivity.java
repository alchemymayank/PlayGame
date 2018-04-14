package com.example.rmit.playgame.quiz;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rmit.playgame.BaseActivity;
import com.example.rmit.playgame.PlayGameApplication;
import com.example.rmit.playgame.R;
import com.example.rmit.playgame.network.ApiClient;
import com.example.rmit.playgame.network.IQuestion;
import com.example.rmit.playgame.network.MyApiClient;
import com.example.rmit.playgame.network.Question;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by RMIT on 28/12/2017.
 */

public class QuizActivity extends BaseActivity implements View.OnClickListener {

    TextView question, option_a, option_b, option_c, option_d, option_e;
    Context context;

    ImageButton previousAmount, nextAmount, previousSubject, nextSubject;

    Retrofit retrofit;
    PlayGameApplication application;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
//        switchToScreen(R.id.quick_multi_play_screen);


        question = (TextView) findViewById(R.id.ques_text_view);
        option_a = (TextView) findViewById(R.id.option_a);
        option_b = (TextView) findViewById(R.id.option_b);
        option_c = (TextView) findViewById(R.id.option_c);
        option_d = (TextView) findViewById(R.id.option_d);
        option_e = (TextView) findViewById(R.id.option_e);


        for (int id : CLICKABLES) {
            findViewById(id).setOnClickListener(this);
        }
    }

    final static int[] CLICKABLES = {
            R.id.option_a, R.id.option_b,R.id.option_c,R.id.option_d,R.id.option_e, R.id.ques_text_view
    };

    String TAG = "Quiz";
    int milliSec = 50;

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.option_a:
                showAlertDialog(this,TAG, "Option A Clicked" );
                vibrateDevice(milliSec);
                break;
            case R.id.option_b:
                showAlertDialog(this, TAG,"Option B Clicked");
                vibrateDevice(milliSec);
                break;
            case R.id.option_c:
                showAlertDialog(this,TAG, "Option C Clicked");
                vibrateDevice(milliSec);
                break;
            case R.id.option_d:
                showAlertDialog(this, TAG, "Option D Clicked");
                vibrateDevice(milliSec);
                break;
            case R.id.option_e:
                showAlertDialog(this, TAG, "Option E Clicked");
                vibrateDevice(milliSec);
                break;
            case R.id.ques_text_view:
                vibrateDevice(milliSec);
                logDebug(TAG,"Question Clicked");
                getQuesFromDatabase();
                break;

        }
    }

    // This array lists all the individual screens our game has.
    final static int[] SCREENS = {
            R.id.question_layout//, R.id.quick_multi_play_screen
    };
    int mCurScreen = -1;

    void switchToScreen(int screenId) {
        // make the requested screen visible; hide all others.
        for (int id : SCREENS) {
            findViewById(id).setVisibility(screenId == id ? View.VISIBLE : View.GONE);
        }
        mCurScreen = screenId;
    }


    private void getQuesFromDatabase(){
        application = new PlayGameApplication();
        retrofit = application.getRetrofit();
        if (retrofit==null){
            Toast.makeText(this, "Retrofit is Null", Toast.LENGTH_SHORT).show();
            application.setRetrofit();
            retrofit = application.getRetrofit();
        }else {
            Toast.makeText(this, "Retrofit is not Null", Toast.LENGTH_SHORT).show();
        }

        retrofit.create(IQuestion.class).getQuestion("2").enqueue(new Callback<Question>() {
            @Override
            public void onResponse(Call<Question> call, Response<Question> response) {
                logDebug(TAG, "Response "+response);
                    String ques = response.body().getQuestion();
                    String optionA = response.body().getOptionA();
                    String optionB = response.body().getOptionB();
                    String optionC = response.body().getOptionC();
                    String optionD = response.body().getOptionD();
                    String optionE = response.body().getOptionE();
                    String optionAnswer = response.body().getAnswer();
                    question.setText(ques);
                    option_a.setText(optionA);
                    option_b.setText(optionB);
                    option_c.setText(optionC);
                    option_d.setText(optionD);
                    option_e.setText(optionE);

            }
            @Override
            public void onFailure(Call<Question> call, Throwable t) {
                logDebug(TAG, "Failed Response "+String.valueOf(t));
            }
        });


    }

}
