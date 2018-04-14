package com.example.rmit.playgame.newquickplay;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.rmit.playgame.BaseActivity;
import com.example.rmit.playgame.R;
import com.example.rmit.playgame.realtime.RealtimeExample;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.InvitationsClient;
import com.google.android.gms.games.RealTimeMultiplayerClient;
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
 * Created by RMIT on 20/01/2018.
 */

public class NewQuickPlay extends BaseActivity {

    final static String TAG = RealtimeExample.class.getSimpleName();

    // Request codes for the UIs that we show with startActivityForResult:
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_INVITATION_INBOX = 10001;
    final static int RC_WAITING_ROOM = 10002;
    private String mPlayerId;

    // Client used to sign in with Google APIs
    private GoogleSignInClient mGoogleSignInClient = null;

    int minPlayer = 1;
    int maxPlayer = 1;

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

    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_quick_play);

        mGoogleSignInClient = getmGoogleSignInClient();

        mRealTimeMultiplayerClient = Games.getRealTimeMultiplayerClient(this, GoogleSignIn.getLastSignedInAccount(this));

        mInvitationsClient  =  Games.getInvitationsClient(this, GoogleSignIn.getLastSignedInAccount(this));

//        mInvitationsClient.registerInvitationCallback(mInvitationCallback);

//        progressBar = new ProgressBar(this);
        logDebug(TAG, "Activity New Quick Play Created");
        progressBar = new ProgressBar(this);

        Toast.makeText(this, "Progress Bar Started", Toast.LENGTH_SHORT).show();
        startQuickGame();
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

        logDebug(TAG, "Please Wait...");
    }

    void keepScreenOn() {
        logDebug(TAG, "!!!Initializing Keep Screen On!!!");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    // Clears the flag that keeps the screen on.
    void stopKeepingScreenOn() {
        logDebug(TAG, "!!!Keep Screen On Stopped!!!");
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private RoomUpdateCallback mRoomUpdateCallback = new RoomUpdateCallback(){

        @Override
        public void onRoomCreated(int statusCode, @Nullable Room room) {
            logDebug(TAG, "On Room Created Called");
            Toast.makeText(NewQuickPlay.this, "Progress Bar Stopped", Toast.LENGTH_SHORT).show();

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
            logDebug(TAG, "On Realtime Message Recieved");

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

    void updateRoom(Room room) {
        logDebug(TAG, "Room Updating");
        if (room != null) {
            mParticipants = room.getParticipants();
        }
        if (mParticipants != null) {
//            updatePeerScoresDisplay();
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

    private OnFailureListener createFailureListener(final String string) {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                handleException(NewQuickPlay.this,e, string);
            }
        };
    }

    void showGameError() {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.game_problem))
                .setNeutralButton(android.R.string.ok, null).create();

//        switchToMainScreen();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_WAITING_ROOM) {
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
        super.onActivityResult(requestCode, resultCode, data);
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
            logDebug(TAG,"Room Left Successfully..");
        }
    }
}
