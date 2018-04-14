package com.example.rmit.playgame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.rmit.playgame.dashboard.DashboardActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesCallbackStatusCodes;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.GamesClientStatusCodes;
import com.google.android.gms.games.InvitationsClient;
import com.google.android.gms.games.Player;
import com.google.android.gms.games.PlayersClient;
import com.google.android.gms.games.RealTimeMultiplayerClient;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by RMIT on 26/12/2017.
 */

public class MainActivity extends BaseActivity implements View.OnClickListener {

    final static String TAG = "MainActivity";

    // Request code used to invoke sign in user interactions.
    private static final int RC_SIGN_IN = 9001;

    // Client used to sign in with Google APIs
    //private GoogleSignInClient mGoogleSignInClient = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //findViewById(R.id.button_sign_out).setVisibility(View.GONE);



        // Create the client used to sign in.
        //mGoogleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);

        for (int id : CLICKABLES) {
            findViewById(id).setOnClickListener(this);
        }
    }



    // This array lists everything that's clickable, so we can install click
    // event handlers.
    final static int[] CLICKABLES = {
            R.id.button_sign_in
//            ,R.id.button_sign_out
//            R.id.button_accept_popup_invitation, R.id.button_invite_players,
//            R.id.button_quick_game, R.id.button_see_invitations, R.id.button_sign_in,
//            R.id.button_sign_out, R.id.button_click_me, R.id.button_single_player,
//            R.id.button_single_player_2
    };

    public void signOut() {
        Log.d(TAG, "signOut()");

        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Log.d(TAG, "signOut(): success");
                            //findViewById(R.id.button_sign_out).setVisibility(View.GONE);
                            findViewById(R.id.button_sign_in).setVisibility(View.VISIBLE);
                        } else {
                            //handleException(task.getException(), "signOut() failed!");
                            handleException(MainActivity.this, task.getException(),"signOut() failed!");
                        }

                        onDisconnected();
                    }
                });
    }

    /**
     * Since a lot of the operations use tasks, we can use a common handler for whenever one fails.
     *
     //* @param exception The exception to evaluate.  Will try to display a more descriptive reason for the exception.
     //* @param details   Will display alongside the exception if you wish to provide more details for why the exception
     *                  happened
     */

    public void onDisconnected() {
        Log.d(TAG, "onDisconnected()");


        mRealTimeMultiplayerClient = null;
        mInvitationsClient = null;
//
//        switchToMainScreen();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(intent);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                onConnected(account);
            } catch (ApiException apiException) {
                String message = apiException.getMessage();
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error);
                }

                onDisconnected();

//                new AlertDialog.Builder(this)
//                        .setMessage(message)
//                        .setNeutralButton(android.R.string.ok, null)
//                        .show();
                showAlertDialog(this,"Error", message);
            }
        }
//        else if (requestCode == RC_SELECT_PLAYERS) {
//            // we got the result from the "select players" UI -- ready to create the room
//            handleSelectPlayersResult(resultCode, intent);

//        } else if (requestCode == RC_INVITATION_INBOX) {
//            // we got the result from the "select invitation" UI (invitation inbox). We're
//            // ready to accept the selected invitation:
////            handleInvitationInboxResult(resultCode, intent);
//
//        } else if (requestCode == RC_WAITING_ROOM) {
//            // we got the result from the "waiting room" UI.
////            if (resultCode == Activity.RESULT_OK) {
////                // ready to start playing
////                Log.d(TAG, "Starting game (waiting room returned OK).");
////                //startGame(true);
////            } else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
////                // player indicated that they want to leave the room
////                //leaveRoom();
////            }
////            else if (resultCode == Activity.RESULT_CANCELED) {
////                // Dialog was cancelled (user pressed back key, for instance). In our game,
////                // this means leaving the room too. In more elaborate games, this could mean
////                // something else (like minimizing the waiting room UI).
////                //leaveRoom();
////                Log.d(TAG, "Result Cancelled");
////            }
//        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");

        // Since the state of the signed in user can change when the activity is not active
        // it is recommended to try and sign in silently from when the app resumes.
        signInSilently();
    }

    public void startSignInIntent() {
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    GoogleSignInAccount mSignedInAccount = null;
    // Client used to interact with the real time multiplayer system.
    private RealTimeMultiplayerClient mRealTimeMultiplayerClient = null;
    //Client used to interact with the Invitation system.
    private InvitationsClient mInvitationsClient = null;
    private String mPlayerId;

    private void onConnected(GoogleSignInAccount googleSignInAccount) {
        Log.d(TAG, "onConnected(): connected to Google APIs");
        //Log.d("SignIn Account", googleSignInAccount.getDisplayName());
        if (mSignedInAccount != googleSignInAccount) {

            mSignedInAccount = googleSignInAccount;

            // update the clients
            mRealTimeMultiplayerClient = Games.getRealTimeMultiplayerClient(this, googleSignInAccount);
            mInvitationsClient = Games.getInvitationsClient(MainActivity.this, googleSignInAccount);

            // get the playerId from the PlayersClient
            PlayersClient playersClient = Games.getPlayersClient(this, googleSignInAccount);
            playersClient.getCurrentPlayer().addOnSuccessListener(
                    new OnSuccessListener<Player>() {
                        @Override
                        public void onSuccess(Player player) {
                            mPlayerId = player.getPlayerId();
                            Log.d("Player ID", mPlayerId);
                            Log.d("Display Name", player.getDisplayName());
                            Log.d("Player Name", player.getName());
                            findViewById(R.id.button_sign_in).setVisibility(View.GONE);
                            //findViewById(R.id.button_sign_out).setVisibility(View.VISIBLE);
                            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                            intent.putExtra("DisplayName", player.getDisplayName());
                            intent.putExtra("PlayerName", player.getName());
                            startActivity(intent);
                            finish();


                        }
                    }
            );
        }
//
//        // register listener so we are notified if we receive an invitation to play
//        // while we are in the game
//        mInvitationsClient.registerInvitationCallback(mInvitationCallback);
//
//        // get the invitation from the connection hint
//        // Retrieve the TurnBasedMatch from the connectionHint
//        GamesClient gamesClient = Games.getGamesClient(MainActivity.this, googleSignInAccount);
//        gamesClient.getActivationHint()
//                .addOnSuccessListener(new OnSuccessListener<Bundle>() {
//                    @Override
//                    public void onSuccess(Bundle hint) {
//                        if (hint != null) {
//                            Invitation invitation =
//                                    hint.getParcelable(Multiplayer.EXTRA_TURN_BASED_MATCH);
//
//                            if (invitation != null && invitation.getInvitationId() != null) {
//                                // retrieve and cache the invitation ID
//                                Log.d(TAG, "onConnected: connection hint has a room invite!");
//                                acceptInviteToRoom(invitation.getInvitationId());
//                            }
//                        }
//                    }
//                })
//                .addOnFailureListener(createFailureListener("There was a problem getting the activation hint!"));
//
//        switchToMainScreen();
    }

    public void signInSilently() {
        Log.d(TAG, "signInSilently()");

        mGoogleSignInClient.silentSignIn().addOnCompleteListener(this,
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInSilently(): success");
                            onConnected(task.getResult());
                        } else {
                            Log.d(TAG, "signInSilently(): failure", task.getException());
                            onDisconnected();
                        }
                    }
                });
    }

    private OnFailureListener createFailureListener(final String string) {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //handleException(e, string);
                handleException(MainActivity.this, e, string);
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_sign_in:
                Log.d(TAG, "Sign-in button clicked");
                startSignInIntent();
                break;
//            case R.id.button_sign_out:
//                Log.d(TAG, "Sign-out button clicked");
//                signOut();
//                break;
        }
    }
}
