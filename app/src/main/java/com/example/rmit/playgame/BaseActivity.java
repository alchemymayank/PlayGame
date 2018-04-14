package com.example.rmit.playgame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.rmit.playgame.dashboard.DashboardActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesCallbackStatusCodes;
import com.google.android.gms.games.GamesClientStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/**
 * Created by RMIT on 28/12/2017.
 */

public class BaseActivity extends AppCompatActivity {

    private long mBackPressedTime  = 0;

    NavigationView navigationView;
    // Client used to sign in with Google APIs
    protected GoogleSignInClient mGoogleSignInClient = null;

    Vibrator vibrator;
    View view;

    GoogleApiClient mGoogleApiClient;


    @SuppressLint("ServiceCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Create the client used to sign in.
        //mGoogleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);


        getmGoogleSignInClient();

    }


    public GoogleSignInClient getmGoogleSignInClient(){

        mGoogleSignInClient = GoogleSignIn.getClient(this,
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build());

        return mGoogleSignInClient;
    }

    protected void vibrateDevice(int milliseconds){
        vibrator.vibrate(milliseconds);
    }


//    @Override
//    public void onBackPressed() {
//        if (mBackPressedTime + 2000 > System.currentTimeMillis()) {
//            super.onBackPressed();
//
//        } else {
//            showSnackbar(view, "Tap back again to exit");
//
//        }
//        mBackPressedTime = System.currentTimeMillis();
//    }


    protected void showSnackbar(View view, String message){
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }


    protected void logDebug(String tag,String message){
        Log.d(tag, message);
    }

    protected void logError(String tag, String message){
        Log.e(tag, message);
    }

    protected void logWarning(String tag, String message){
        Log.w(tag, message);
    }

    protected void showAlertDialog(Activity activity, String title, String message){
        new android.app.AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, null)
                .show();
    }

    protected void showToast(String message){
        Toast.makeText(this, message,Toast.LENGTH_SHORT).show();
    }

    protected void handleException(Activity activity,Exception exception, String details) {
        int status = 0;

        if (exception instanceof ApiException) {
            ApiException apiException = (ApiException) exception;
            status = apiException.getStatusCode();
        }

        String errorString = null;
        switch (status) {
            case GamesCallbackStatusCodes.OK:
                break;
            case GamesClientStatusCodes.MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
                errorString = getString(R.string.status_multiplayer_error_not_trusted_tester);
                break;
            case GamesClientStatusCodes.MATCH_ERROR_ALREADY_REMATCHED:
                errorString = getString(R.string.match_error_already_rematched);
                break;
            case GamesClientStatusCodes.NETWORK_ERROR_OPERATION_FAILED:
                errorString = getString(R.string.network_error_operation_failed);
                break;
            case GamesClientStatusCodes.INTERNAL_ERROR:
                errorString = getString(R.string.internal_error);
                break;
            case GamesClientStatusCodes.MATCH_ERROR_INACTIVE_MATCH:
                errorString = getString(R.string.match_error_inactive_match);
                break;
            case GamesClientStatusCodes.MATCH_ERROR_LOCALLY_MODIFIED:
                errorString = getString(R.string.match_error_locally_modified);
                break;
            default:
                errorString = getString(R.string.unexpected_status, GamesClientStatusCodes.getStatusCodeString(status));
                break;
        }

        if (errorString == null) {
            return;
        }

        String message = getString(R.string.status_exception_error, details, status, exception);

//        new android.app.AlertDialog.Builder(activity)
//                .setTitle("Error")
//                .setMessage(message + "\n" + errorString)
//                .setNeutralButton(android.R.string.ok, null)
//                .show();
        showAlertDialog(activity,"Error", message + "\n" + errorString);
    }


}
