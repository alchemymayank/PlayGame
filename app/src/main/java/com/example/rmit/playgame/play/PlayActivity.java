package com.example.rmit.playgame.play;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.rmit.playgame.BaseActivity;
import com.example.rmit.playgame.R;
import com.example.rmit.playgame.quickplay.QuickPlayActivity;

/**
 * Created by RMIT on 28/12/2017.
 */

public class PlayActivity extends BaseActivity {

    private static final String TAG = PlayActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
    }

    public void showInvitations(View view) {
        showAlertDialog(this,TAG,"Invitations Button Clicked");
    }

    public void showInviteFriends(View view) {
        showAlertDialog(this, TAG, "Invite Friends Button Clicked");
    }

    public void showQuickPlay(View view) {
//        showAlertDialog(this, TAG, "Quick Play Button Clicked");
        Intent intent = new Intent(PlayActivity.this, QuickPlayActivity.class);
        startActivity(intent);
    }

    public void showSinglePlayer(View view) {
        showAlertDialog(this, TAG, "Single Player Button Clicked");
    }
}
