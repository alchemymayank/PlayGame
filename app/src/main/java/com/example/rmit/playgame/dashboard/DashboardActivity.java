package com.example.rmit.playgame.dashboard;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.rmit.playgame.BaseActivity;
import com.example.rmit.playgame.MainActivity;
import com.example.rmit.playgame.R;
import com.example.rmit.playgame.play.PlayActivity;
import com.example.rmit.playgame.realtime.RealtimeExample;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.EventsClient;
import com.google.android.gms.games.Game;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesCallbackStatusCodes;
import com.google.android.gms.games.GamesClientStatusCodes;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.games.RealTimeMultiplayerClient;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by RMIT on 27/12/2017.
 */

public class DashboardActivity extends BaseActivity {

    GoogleSignIn googleSignIn;

    // Client used to sign in with Google APIs
    private GoogleSignInClient mGoogleSignInClient = null;

    final static String TAG = "DashboardActivity";

    private static final int RC_ACHIEVEMENT_UI = 9003;

    private static final int RC_LEADERBOARD_UI = 9004;

    private Room mRoom;
    private String mRoomId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Create the client used to sign in.
        //mGoogleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);

        mGoogleSignInClient  = getmGoogleSignInClient();

        if (isSignedIn()){
            Toast.makeText(this,"Sign In Successfully", Toast.LENGTH_SHORT).show();
            //findViewById(R.id.button_sign_out).setVisibility(View.VISIBLE);
        }else{
            Toast.makeText(this, "Sign In Failed", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
            startActivity(intent);
            //findViewById(R.id.button_sign_out).setVisibility(View.GONE);
        }

        Intent intent = getIntent();
        String status = intent.getStringExtra("Status");
        logDebug(TAG, "-" + status);
        if (status == "success"){
            mRoom = intent.getParcelableExtra("Room");
            mRoomId = intent.getStringExtra("RoomId");

            logDebug(TAG, String.valueOf(mRoom));
            logDebug(TAG, mRoomId);
            String testRoomId = mRoom.getRoomId();
            logDebug(TAG, "Test Room Id : " + testRoomId);
        }else {
            logDebug(TAG, "Intent is Null");
        }






//
//        findViewById(R.id.button_sign_out).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "Sign Out Button Clicked");
//                signOut();
//            }
//        });
        Games.getGamesClient(DashboardActivity.this, GoogleSignIn.getLastSignedInAccount(this)).setViewForPopups(findViewById(android.R.id.content));

        mAchievementsClient = Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dashboard, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dashboard_logout:
                Log.d(TAG, "LogOut Clicked");
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private boolean isSignedIn() {
        return googleSignIn.getLastSignedInAccount(this) != null;
    }

    public void signOut() {
        Log.d(TAG, "signOut()");

        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Log.d(TAG, "signOut(): success");
                            //findViewById(R.id.button_sign_out).setVisibility(View.GONE);
                            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
                            startActivity(intent);

                        } else {
                            //handleException(task.getException(), "signOut() failed!");
                            handleException(DashboardActivity.this, task.getException(), "signOut() failed!");
                        }

                        onDisconnected();
                    }
                });
    }

//    private void handleException(Exception exception, String details) {
//        int status = 0;
//
//        if (exception instanceof ApiException) {
//            ApiException apiException = (ApiException) exception;
//            status = apiException.getStatusCode();
//        }
//
//        String errorString = null;
//        switch (status) {
//            case GamesCallbackStatusCodes.OK:
//                break;
//            case GamesClientStatusCodes.MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
//                errorString = getString(R.string.status_multiplayer_error_not_trusted_tester);
//                break;
//            case GamesClientStatusCodes.MATCH_ERROR_ALREADY_REMATCHED:
//                errorString = getString(R.string.match_error_already_rematched);
//                break;
//            case GamesClientStatusCodes.NETWORK_ERROR_OPERATION_FAILED:
//                errorString = getString(R.string.network_error_operation_failed);
//                break;
//            case GamesClientStatusCodes.INTERNAL_ERROR:
//                errorString = getString(R.string.internal_error);
//                break;
//            case GamesClientStatusCodes.MATCH_ERROR_INACTIVE_MATCH:
//                errorString = getString(R.string.match_error_inactive_match);
//                break;
//            case GamesClientStatusCodes.MATCH_ERROR_LOCALLY_MODIFIED:
//                errorString = getString(R.string.match_error_locally_modified);
//                break;
//            default:
//                errorString = getString(R.string.unexpected_status, GamesClientStatusCodes.getStatusCodeString(status));
//                break;
//        }
//
//        if (errorString == null) {
//            return;
//        }
//
//        String message = getString(R.string.status_exception_error, details, status, exception);
//
//        new AlertDialog.Builder(DashboardActivity.this)
//                .setTitle("Error")
//                .setMessage(message + "\n" + errorString)
//                .setNeutralButton(android.R.string.ok, null)
//                .show();
//    }

    public void onDisconnected() {
        Log.d(TAG, "onDisconnected()");


//        mRealTimeMultiplayerClient = null;
//        mInvitationsClient = null;
//
//        switchToMainScreen();
    }

    public void check(View view) {
        Log.d(TAG,"Check Started");
        GoogleSignInAccount account =  googleSignIn.getLastSignedInAccount(this);
        PlayersClient playersClient = Games.getPlayersClient(this, account);
        playersClient.getCurrentPlayer().addOnSuccessListener(new OnSuccessListener<Player>() {
            @Override
            public void onSuccess(Player player) {
                Log.d("Dashboard Display Name", player.getDisplayName());
                Log.d("Dashboard Player Name", player.getName());
            }
        });
    }

    private void showAchievements() {
        Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .getAchievementsIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_ACHIEVEMENT_UI);
                    }
                });
    }



    public void showAchievements(View view) {
        Log.d(TAG, "Achievements Button Clicked");
        showAchievements();
    }

    private void showLeaderboard() {
        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .getAllLeaderboardsIntent()
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_LEADERBOARD_UI);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,"There was an issue communicating with leaderboards");
            }
        });
    }

    public void showLeaderboards(View view) {
        //Log.d(TAG, "Leaderboards Button Clicked");
        logDebug(TAG,"Leaderboards Button Clicked" );
        showLeaderboard();
    }

    public void showSettings(View view) {

    }

    public void startPlay(View view) {
        Intent intent = new Intent(DashboardActivity.this, PlayActivity.class);
        startActivity(intent);
    }

    public void openAchievementOne(View view) {
        checkForAchievements(5,5);
    }

    public void openAchievementTwo(View view) {

        checkForAchievements(10,10);
    }

    public void openLeaderboardOne(View view) {
        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .submitScore(getString(R.string.leaderboard_id), 1337);
    }

    public void openLeaderboardTwo(View view) {


        Games.getGamesClient(DashboardActivity.this,GoogleSignIn.getLastSignedInAccount(this));
        Games.getLeaderboardsClient(this,GoogleSignIn.getLastSignedInAccount(this))
                .submitScore(getString(R.string.leaderboard_id),1337);
    }


    // Client variables
    private AchievementsClient mAchievementsClient;
    private LeaderboardsClient mLeaderboardsClient;
    private EventsClient mEventsClient;
    private PlayersClient mPlayersClient;

    private void checkForAchievements(int requestedScore, int finalScore) {
        // Check if each condition is met; if so, unlock the corresponding
        // achievement.
        Log.d("MyTag", "Checking Achievements");

//        if (requestedScore == 5) {
//
//            achievementToast("CgkI_vzj6IgWEAIQBw");
//            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
//                    .unlock(getString(R.string.my_achievement_id));
//        }
        if (finalScore == 10) {
//            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
//                    .unlock(getString(R.string.my_achievement_id));
//
//            achievementToast("Good Progress");
            //mAchievementsClient = Games.getAchievementsClient(getApplicationContext(), GoogleSignIn.getLastSignedInAccount(this));
            //mAchievementsClient.reveal(getString(R.string.my_achievement_id));
            mAchievementsClient.unlock(getString(R.string.my_achievement_id));
            Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .submitScore(getString(R.string.leaderboard_id), finalScore);





        }
        if (finalScore == 5) {
//            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
//                    .unlock(getString(R.string.my_achievement_id_2));
            //mAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this)).
            achievementToast(getString(R.string.my_achievement_id_2));
            mAchievementsClient.unlock(getString(R.string.my_achievement_id_2));
            Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .submitScore(getString(R.string.leaderboard_id), finalScore);


        }

    }

    public void achievementToast(String achievement) {
        // Only show toast if not signed in. If signed in, the standard Google Play
        // toasts will appear, so we don't need to show our own.
        if (!isSignedIn()) {
            Toast.makeText(this, getString(R.string.achievement) + ": " + achievement,
                    Toast.LENGTH_LONG).show();
        }
    }

    public void openRealtime(View view) {

        logDebug(TAG,"Realtime Player Button Clicked" );
        Intent intent = new Intent(DashboardActivity.this, RealtimeExample.class);
        startActivity(intent);
    }
}
