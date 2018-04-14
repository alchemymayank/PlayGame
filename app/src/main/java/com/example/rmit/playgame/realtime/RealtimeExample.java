package com.example.rmit.playgame.realtime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.rmit.playgame.BaseActivity;
import com.example.rmit.playgame.R;
import com.example.rmit.playgame.dashboard.DashboardActivity;
import com.example.rmit.playgame.newquickplay.NewQuickPlay;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.InvitationsClient;
import com.google.android.gms.games.RealTimeMultiplayerClient;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.InvitationCallback;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.OnRealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateCallback;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RMIT on 15/01/2018.
 */

public class RealtimeExample  extends BaseActivity implements RealTimeFragment.OnFragmentInteractionListener{

    final static String TAG = RealtimeExample.class.getSimpleName();

    // Request codes for the UIs that we show with startActivityForResult:
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_INVITATION_INBOX = 10001;
    final static int RC_WAITING_ROOM = 10002;
    private String mPlayerId;

    @Override
    protected void onStop() {
        super.onStop();
        leaveRoom();
    }

    // Client used to sign in with Google APIs
    private GoogleSignInClient mGoogleSignInClient = null;

    int minPlayer = 1;
    int maxPlayer = 7;

    // Client used to interact with the real time multiplayer system.
    private RealTimeMultiplayerClient mRealTimeMultiplayerClient = null;

    // Client used to interact with the Invitation system.
    private InvitationsClient mInvitationsClient = null;

    // Room ID where the currently active game is taking place; null if we're
    // not playing.
    String mRoomId = null;

    // Holds the configuration of the current room.
    RoomConfig mRoomConfig;

    // Are we playing in multiplayer mode?
    boolean mMultiplayer = false;

    // The participants in the currently active game
    ArrayList<Participant> mParticipants = null;

    // My participant ID in the currently active game
    String mMyId = null;

    // If non-null, this is the id of the invitation we received via the
    // invitation listener
    String mIncomingInvitationId = null;

    // Message buffer for sending messages
    byte[] mMsgBuf = new byte[2];



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time_example);

        mGoogleSignInClient = getmGoogleSignInClient();

        mRealTimeMultiplayerClient = Games.getRealTimeMultiplayerClient(this, GoogleSignIn.getLastSignedInAccount(this));

        mInvitationsClient  =  Games.getInvitationsClient(this, GoogleSignIn.getLastSignedInAccount(this));

        mInvitationsClient.registerInvitationCallback(mInvitationCallback);

        progressBar = new ProgressBar(this);


    }

    public void startQuickGame(View view) {
        logDebug(TAG, "Start Quick Game Button Clicked");
//        invitePlayers(minPlayer, maxPlayer);
//        startQuickGame();

        Intent intent = new Intent(RealtimeExample.this, NewQuickPlay.class);
        startActivity(intent);
    }

    private ProgressBar progressBar;


    private void showProgressBar(){


    }

    void updateRoom(Room room) {
        logDebug(TAG, "Room Updating");
        if (room != null) {
            mParticipants = room.getParticipants();
        }
        if (mParticipants != null) {
//            updatePeerScoresDisplay();
        }
    }

    private void startQuickGame() {
        // quick-start a game with 1 randomly selected opponent
        final int MIN_OPPONENTS = 1, MAX_OPPONENTS = 1;
        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_OPPONENTS,
                MAX_OPPONENTS, 0);
//        switchToScreen(R.id.screen_wait);
        showToast("Please Wait...");
        keepScreenOn();
//        resetGameVars();

        mRoomConfig = RoomConfig.builder(mRoomUpdateCallback)
                .setOnMessageReceivedListener(mOnRealTimeMessageReceivedListener)
                .setRoomStatusUpdateCallback(mRoomStatusUpdateCallback)
                .setAutoMatchCriteria(autoMatchCriteria)
                .build();
        mRealTimeMultiplayerClient.create(mRoomConfig);
    }

    private RoomUpdateCallback mRoomUpdateCallback = new RoomUpdateCallback(){

        @Override
        public void onRoomCreated(int statusCode, @Nullable Room room) {
            logDebug(TAG, "On Room Created Called");

            showWaitingRoom(room);
        }

        @Override
        public void onJoinedRoom(int statusCode, @Nullable Room room) {
            logDebug(TAG, "On Joined Room Called");
        }

        @Override
        public void onLeftRoom(int statusCode, @NonNull String s) {
            logDebug(TAG, "On Left Room Called");
        }

        @Override
        public void onRoomConnected(int statusCode, @Nullable Room room) {
            logDebug(TAG, "On Room Connected Called");
        }
    };

    OnRealTimeMessageReceivedListener mOnRealTimeMessageReceivedListener = new OnRealTimeMessageReceivedListener() {
        @Override
        public void onRealTimeMessageReceived(@NonNull RealTimeMessage realTimeMessage) {

        }
    };

    Room mRoom;

    private RoomStatusUpdateCallback mRoomStatusUpdateCallback = new RoomStatusUpdateCallback() {
        @Override
        public void onRoomConnecting(@Nullable Room room) {
            logDebug(TAG, "On Room Connecting...");
        }

        @Override
        public void onRoomAutoMatching(@Nullable Room room) {
            logDebug(TAG, "On Room Auto Matching..");
        }

        @Override
        public void onPeerInvitedToRoom(@Nullable Room room, @NonNull List<String> list) {
            logDebug(TAG, "On Peer Invited to Room...");
            updateRoom(room);
        }

        @Override
        public void onPeerDeclined(@Nullable Room room, @NonNull List<String> list) {
            logDebug(TAG, "On Peer Declined...");
            updateRoom(room);
        }

        @Override
        public void onPeerJoined(@Nullable Room room, @NonNull List<String> list) {
            logDebug(TAG, "On Peer Joined...");
            updateRoom(room);
        }

        @Override
        public void onPeerLeft(@Nullable Room room, @NonNull List<String> list) {
            logDebug(TAG, "On Peer Left...");
            updateRoom(room);
        }

        @Override
        public void onConnectedToRoom(@Nullable Room room) {
            logDebug(TAG, "On Connecteed to Room...");
            Log.d(TAG, "onConnectedToRoom.");

            mRoom = room;

            //get participants and my ID:
            mParticipants = room.getParticipants();
            mMyId = room.getParticipantId(mPlayerId);

            // save room ID if its not initialized in onRoomCreated() so we can leave cleanly before the game starts.
            if (mRoomId == null) {
                mRoomId = room.getRoomId();
            }

            // print out the list of participants (for debug purposes)
            Log.d(TAG, "Room ID: " + mRoomId);
            Log.d(TAG, "My ID " + mMyId);
            Log.d(TAG, "<< CONNECTED TO ROOM>>");
        }

        @Override
        public void onDisconnectedFromRoom(@Nullable Room room) {
            logDebug(TAG, "On Disconnected From Room...");
            mRoomId = null;
            mRoomConfig = null;
            showGameError();

        }

        @Override
        public void onPeersConnected(@Nullable Room room, @NonNull List<String> list) {
            logDebug(TAG, "On Peers Connected...");
            updateRoom(room);
        }

        @Override
        public void onPeersDisconnected(@Nullable Room room, @NonNull List<String> list) {
            logDebug(TAG, "On Peers Disconnected...");
            updateRoom(room);
        }

        @Override
        public void onP2PConnected(@NonNull String s) {
            logDebug(TAG, "On P2P Connected...");
        }

        @Override
        public void onP2PDisconnected(@NonNull String s) {
            logDebug(TAG, "On P2P Disconnected...");
        }
    };

    void showGameError() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.game_problem))
                .setNeutralButton(android.R.string.ok, null).create();

//        switchToMainScreen();
    }

    // Accept the given invitation.
    void acceptInviteToRoom(String invitationId) {
        // accept the invitation
        Log.d(TAG, "Accepting invitation: " + invitationId);

        mRoomConfig = RoomConfig.builder(mRoomUpdateCallback)
                .setInvitationIdToAccept(invitationId)
                .setOnMessageReceivedListener(mOnRealTimeMessageReceivedListener)
                .setRoomStatusUpdateCallback(mRoomStatusUpdateCallback)
                .build();

        //switchToScreen(R.id.screen_wait);
        keepScreenOn();
        //resetGameVars();

        mRealTimeMultiplayerClient.join(mRoomConfig)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Room Joined Successfully!");
                    }
                });
    }





    private void invitePlayers(int minPlayer, int maxPlayer) {
        // launch the player selection screen
        // minimum: 1 other player; maximum: 3 other players
        Games.getRealTimeMultiplayerClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .getSelectOpponentsIntent(minPlayer, maxPlayer, true)
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_SELECT_PLAYERS);
                    }
                });
    }

    int RC_SIGN_IN = -1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task =
                    GoogleSignIn.getSignedInAccountFromIntent(intent);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
//                onConnected(account);
            } catch (ApiException apiException) {
                String message = apiException.getMessage();
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.signin_other_error);
                }

//                onDisconnected();

                new AlertDialog.Builder(this)
                        .setMessage(message)
                        .setNeutralButton(android.R.string.ok, null)
                        .show();
            }
        } else if (requestCode == RC_SELECT_PLAYERS) {
            // we got the result from the "select players" UI -- ready to create the room
            handleSelectPlayersResult(resultCode, intent);

        } else if (requestCode == RC_INVITATION_INBOX) {
            // we got the result from the "select invitation" UI (invitation inbox). We're
            // ready to accept the selected invitation:
            handleInvitationInboxResult(resultCode, intent);

        } else if (requestCode == RC_WAITING_ROOM) {
            // we got the result from the "waiting room" UI.
            if (resultCode == Activity.RESULT_OK) {
                // ready to start playing
                Log.d(TAG, "Starting game (waiting room returned OK).");
//                startGame(true);
            } else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
                // player indicated that they want to leave the room
                leaveRoom();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Dialog was cancelled (user pressed back key, for instance). In our game,
                // this means leaving the room too. In more elaborate games, this could mean
                // something else (like minimizing the waiting room UI).
                leaveRoom();
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);

    }

    void leaveRoom() {
        logDebug(TAG, "Start Leaving Room..");
//        mSecondsLeft = 0;
        stopKeepingScreenOn();
        if (mRoomId != null) {
            mRealTimeMultiplayerClient.leave(mRoomConfig, mRoomId)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mRoomId = null;
                            mRoomConfig = null;
                        }
                    });
//            switchToScreen(R.id.screen_wait);
            logDebug(TAG, "Room Left Successfully...");
        } else {
//            switchToMainScreen();
            logDebug(TAG,"Room Already Left..");
        }
    }

    private InvitationCallback mInvitationCallback = new InvitationCallback() {
        // Called when we get an invitation to play a game. We react by showing that to the user.
        @Override
        public void onInvitationReceived(@NonNull Invitation invitation) {
            // We got an invitation to play a game! So, store it in
            // mIncomingInvitationId
            // and show the popup on the screen.
            mIncomingInvitationId = invitation.getInvitationId();

            new AlertDialog.Builder(RealtimeExample.this)
                    .setTitle("Invitation Received").setMessage(invitation.getInviter().getDisplayName() + " " +
                    getString(R.string.is_inviting_you)).setNeutralButton("Accept", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    logDebug(TAG,"Invitation Accepting...");
                    showInvitations();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    logDebug(TAG, "Cancelling Invitation Accepting...");

                }
            }).show();

//            ((TextView) findViewById(R.id.incoming_invitation_text)).setText(
//                    invitation.getInviter().getDisplayName() + " " +
//                            getString(R.string.is_inviting_you));
//            switchToScreen(mCurScreen); // This will show the invitation popup
        }

        @Override
        public void onInvitationRemoved(@NonNull String invitationId) {

            if (mIncomingInvitationId.equals(invitationId) && mIncomingInvitationId != null) {
                mIncomingInvitationId = null;
//                switchToScreen(mCurScreen); // This will hide the invitation popup
            }
        }
    };

    void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // Clears the flag that keeps the screen on.
    void stopKeepingScreenOn() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void seeInvitations(View view) {
        showInvitations();
    }

    private void showInvitations(){
        // show list of pending invitations
        mInvitationsClient.getInvitationInboxIntent().addOnSuccessListener(
                new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_INVITATION_INBOX);
                    }
                }
        ).addOnFailureListener(createFailureListener("There was a problem getting the inbox."));
    }

    private OnFailureListener createFailureListener(final String string) {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handleException(RealtimeExample.this,e, string);
            }
        };
    }

    public void inviteFriend(View view) {
        logDebug(TAG, "Invite Friends Button Clicked...");
        selectOpponent();
    }

    private void selectOpponent(){
        mRealTimeMultiplayerClient.getSelectOpponentsIntent(1, 3).addOnSuccessListener(
                new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, RC_SELECT_PLAYERS);
                    }
                }
        ).addOnFailureListener(createFailureListener("There was a problem selecting opponents."));
    }


    private void handleSelectPlayersResult(int response, Intent intent) {
        if (response != Activity.RESULT_OK) {
            Log.w(TAG, "*** select players UI cancelled, " + response);
//            switchToMainScreen();
            return;
        }

        Log.d(TAG, "Select players UI succeeded.");

        // get the invitee list
        final ArrayList<String> invitees = intent.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
        Log.d(TAG, "Invitee count: " + invitees.size());

        // get the automatch criteria
        Bundle autoMatchCriteria = null;
        int minAutoMatchPlayers = intent.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
        int maxAutoMatchPlayers = intent.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
        if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
            autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                    minAutoMatchPlayers, maxAutoMatchPlayers, 0);
            Log.d(TAG, "Automatch criteria: " + autoMatchCriteria);
        }

        // create the room
        Log.d(TAG, "Creating room...");
//        switchToScreen(R.id.screen_wait);
        keepScreenOn();
//        resetGameVars();

        mRoomConfig = RoomConfig.builder(mRoomUpdateCallback)
                .addPlayersToInvite(invitees)
                .setOnMessageReceivedListener(mOnRealTimeMessageReceivedListener)
                .setRoomStatusUpdateCallback(mRoomStatusUpdateCallback)
                .setAutoMatchCriteria(autoMatchCriteria).build();
        mRealTimeMultiplayerClient.create(mRoomConfig);
        Log.d(TAG, "Room created, waiting for it to be ready...");
    }

    private void handleInvitationInboxResult(int response, Intent data) {
        if (response != Activity.RESULT_OK) {
            Log.w(TAG, "*** invitation inbox UI cancelled, " + response);
//            switchToMainScreen();
            return;
        }

        Log.d(TAG, "Invitation inbox UI succeeded.");
        Invitation invitation = data.getExtras().getParcelable(Multiplayer.EXTRA_INVITATION);

        // accept invitation
        if (invitation != null) {
            acceptInviteToRoom(invitation.getInvitationId());
        }
    }

    void showWaitingRoom(Room room) {
        // minimum number of players required for our game
        // For simplicity, we require everyone to join the game before we start it
        // (this is signaled by Integer.MAX_VALUE).
        final int MIN_PLAYERS = Integer.MAX_VALUE;
        mRealTimeMultiplayerClient.getWaitingRoomIntent(room, MIN_PLAYERS)
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        // show waiting room UI
                        startActivityForResult(intent, RC_WAITING_ROOM);
                    }
                })
                .addOnFailureListener(createFailureListener("There was a problem getting the waiting room!"));
    }

    public void leaveRoom(View view) {
        leaveRoom();
        logDebug(TAG, "Leave Room Button Clicked...");
    }

    public void changeActivity(View view) {
        logDebug(TAG, "Changing Activity..");
//        Intent intent = new Intent(RealtimeExample.this, DashboardActivity.class);
//        intent.putExtra("Room", mRoom);
//        intent.putExtra("RoomId", mRoomId);
//        intent.putExtra("Status", "success");
//        startActivity(intent);

        Bundle bundle = new Bundle();
        bundle.putParcelable("Room", mRoom);
        bundle.putString("RoomId", mRoomId);
        RealTimeFragment realTimeFragment = new RealTimeFragment();
        realTimeFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.real_time_container, realTimeFragment)
                .commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
