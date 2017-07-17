package com.waylonhuang.notifydesktop;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Waylon on 7/16/2017.
 */

public class SetupFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    public static final String PREFS_FILE = "PREFS_FILE";
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = MainActivity.class.getSimpleName();
    private String email;

    private GoogleApiClient mGoogleApiClient;

    public SetupFragment() {
        // Required empty public constructor
    }

    public static SetupFragment newInstance() {
        SetupFragment fragment = new SetupFragment();
        Bundle args = new Bundle();
        // args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_FILE, 0);
        boolean signedIn = settings.getBoolean("signedIn", false);
        email = settings.getString("email", "");
        String uid = settings.getString("uid", "");

        Log.wtf(TAG, "uid: " + uid);

        updateUI(signedIn);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Setup");

        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestServerAuthCode(getString(R.string.default_web_client_id))
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Button enableNotifyButton = (Button) view.findViewById(R.id.enable_notifications);
        enableNotifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivity(intent);
            }
        });

        TextView testNotifyButton = (TextView) view.findViewById(R.id.create_notification);
        testNotifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationManager nManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getActivity());
                notificationBuilder.setContentTitle("Notify Desktop");
                notificationBuilder.setContentText("Notification Wake Screen Content");
                notificationBuilder.setTicker("Notification Wake Screen Ticker");
                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
                notificationBuilder.setAutoCancel(true);
                nManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
            }
        });

        Button signOutButton = (Button) view.findViewById(R.id.signout);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        Button signInButton = (Button) view.findViewById(R.id.signin);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        TextView emailTextView = (TextView) view.findViewById(R.id.email);
        emailTextView.setText("Signed In: " + email);

        return view;
    }

    public void updateUI(boolean signedIn) {
        View view = getView();

        CardView cardViewSignIn = (CardView) view.findViewById(R.id.card_sign_in);
        CardView cardViewSignOut = (CardView) view.findViewById(R.id.card_sign_out);
        CardView cardViewSignTest = (CardView) view.findViewById(R.id.card_sign_test);
        TextView emailTextView = (TextView) view.findViewById(R.id.email);

        if (signedIn) {
            cardViewSignIn.setVisibility(View.GONE);
            cardViewSignOut.setVisibility(View.VISIBLE);
            cardViewSignTest.setVisibility(View.VISIBLE);
            emailTextView.setText("Signed In: " + email);
        } else {
            cardViewSignIn.setVisibility(View.VISIBLE);
            cardViewSignOut.setVisibility(View.GONE);
            cardViewSignTest.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();

        Log.wtf(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
            editor.putBoolean("signedIn", false);

            Toast.makeText(getActivity(), "Couldn't sign in.", Toast.LENGTH_SHORT).show();
        }

        // Commit the edits!
        editor.apply();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.wtf(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_FILE, 0);
                        SharedPreferences.Editor editor = settings.edit();

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.wtf(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user == null) {
                                Log.wtf(TAG, "Error: user null?");
                                return;
                            }
                            editor.putBoolean("signedIn", true);
                            editor.putString("email", user.getEmail());
                            editor.putString("uid", user.getUid());
                            email = user.getEmail();

                            updateUI(true);

                            Log.wtf(TAG, "uid: " + user.getUid());
                            Toast.makeText(getActivity(), "Signed in successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            editor.putBoolean("signedIn", false);
                            updateUI(false);
                            Toast.makeText(getActivity(), "Couldn't sign in.", Toast.LENGTH_SHORT).show();
                        }

                        editor.apply();
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("Connection failed: " + connectionResult.toString());
        Toast.makeText(getActivity(), "Connection failure.", Toast.LENGTH_SHORT).show();
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_FILE, 0);
                        SharedPreferences.Editor editor = settings.edit();

                        if (status.isSuccess()) {
                            editor.putBoolean("signedIn", false);
                            Toast.makeText(getActivity(), "Signed out successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            editor.putBoolean("signedIn", true);
                            Toast.makeText(getActivity(), "Couldn't sign out.", Toast.LENGTH_SHORT).show();
                        }
                        editor.apply();
                        updateUI(!status.isSuccess());
                    }
                });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // Update ui
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        View view = getView();

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.frame1);
        TextView textView = (TextView) view.findViewById(R.id.textView1);
        Button button = (Button) view.findViewById(R.id.enable_notifications);

        if (NotificationManagerCompat.getEnabledListenerPackages(getActivity()).contains(getActivity().getPackageName())) {
            textView.setText("Notification access is enabled.");
            button.setText("Disable");
            layout.setBackgroundColor(Color.rgb(76, 175, 80));
        } else {
            textView.setText("Notification access is not enabled.");
            button.setText("Enable");
            layout.setBackgroundColor(Color.rgb(244, 67, 54));
        }

    }
}
