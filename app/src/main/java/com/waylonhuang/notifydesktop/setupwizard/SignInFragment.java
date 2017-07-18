package com.waylonhuang.notifydesktop.setupwizard;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.waylonhuang.notifydesktop.R;

import static com.waylonhuang.notifydesktop.MainActivity.PREFS_FILE;

public class SignInFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_SIGN_IN = 9001;
    private String email, username;
    private boolean signedIn;

    private GoogleApiClient mGoogleApiClient;

    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_FILE, 0);
        signedIn = settings.getBoolean("signedIn", false);
        email = settings.getString("email", "");
        username = settings.getString("username", "");

        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestServerAuthCode(getString(R.string.default_web_client_id))
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Button signOutButton = (Button) view.findViewById(R.id.signout_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        Button signInButton = (Button) view.findViewById(R.id.signin_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateUI(signedIn);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("Connection failed: " + connectionResult.toString());
        Toast.makeText(getActivity(), "Connection failure.", Toast.LENGTH_SHORT).show();
    }

    private void updateUI(boolean signedIn) {
        View view = getView();

        CardView signInCV = (CardView)view.findViewById(R.id.sign_in_cv);
        CardView signedInCV = (CardView)view.findViewById(R.id.signed_in_cv);

        TextView emailTV = (TextView) view.findViewById(R.id.signed_in_tv);
        String emailStr = "Signed in: " + email + "\n" + "Welcome " + username + "!";
        emailTV.setText(emailStr);

        if (signedIn) {
            signInCV.setVisibility(View.GONE);
            signedInCV.setVisibility(View.VISIBLE);
        } else {
            signInCV.setVisibility(View.VISIBLE);
            signedInCV.setVisibility(View.GONE);
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

        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
        } else {
            updateUI(false);
            editor.putBoolean("signedIn", false);
            Toast.makeText(getActivity(), "Couldn't sign in.", Toast.LENGTH_SHORT).show();
        }

        editor.apply();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
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
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user == null) {
                                return;
                            }

                            email = user.getEmail();
                            username = user.getDisplayName();

                            editor.putBoolean("signedIn", true);
                            editor.putString("email", email);
                            editor.putString("username", username);
                            editor.putString("uid", user.getUid());

                            Toast.makeText(getActivity(), "Signed in successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            editor.putBoolean("signedIn", false);
                            Toast.makeText(getActivity(), "Couldn't sign in.", Toast.LENGTH_SHORT).show();
                        }
                        updateUI(task.isSuccessful());

                        editor.apply();
                    }
                });
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
                        signedIn = !status.isSuccess();
                        email = "";
                        username = "";

                        editor.apply();
                        updateUI(signedIn);
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
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }
}
