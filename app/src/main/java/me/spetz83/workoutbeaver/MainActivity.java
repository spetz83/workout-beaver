package me.spetz83.workoutbeaver;

import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.squareup.otto.Bus;

import java.io.IOException;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends AbstractWBActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{
    private static final String MAIN_TAG = "MAIN_ACT";
    public static final String AUTH_MESSAGE = "AUTH_MESSAGE";
    private static final int RC_SIGN_IN = 0;

    private GoogleApiClient mGoogleApiClient;
    private boolean mIntentInProgress;
    private boolean mGplusClicked;
    private ConnectionResult mConnectionResult;

    @Inject
    Bus bus;

    @InjectView(R.id.username)
    EditText username;

    @InjectView(R.id.password)
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DaggerInjector.inject(this);
        ButterKnife.inject(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
        mGoogleApiClient.connect();
    }

    @OnClick(R.id.btn_login)
    public void login(View view)
    {
        Log.d(MAIN_TAG, "Login");
        navigateToHomeScreen();
    }

    @OnClick(R.id.btn_debug)
    public void debug(View view)
    {
        Log.d(MAIN_TAG, "Debug");
        if(mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Log.d(MAIN_TAG, "Disconnected User");
                        }
                    });
        }
    }

    @OnClick(R.id.btn_gplus)
    public void gPlusSignIn(View view)
    {
        if(!mGoogleApiClient.isConnecting())
        {
            mGplusClicked = true;
            resolveSignInError();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        bus.register(this);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if(mGoogleApiClient.isConnected())
        {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle)
    {
        mGplusClicked = false;

        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String token = null;

                try
                {
                    token = GoogleAuthUtil.getToken(MainActivity.this, Plus.AccountApi.getAccountName(mGoogleApiClient), "oauth2:" + Plus.SCOPE_PLUS_PROFILE);
                }
                catch(IOException transientEx)
                {
                    Log.e(MAIN_TAG, transientEx.toString());
                }
                catch(UserRecoverableAuthException e)
                {
                    Log.e(MAIN_TAG, e.toString());
                    Intent recoverIntent = e.getIntent();
                    startActivityForResult(recoverIntent, RC_SIGN_IN);
                }
                catch(GoogleAuthException authEx)
                {
                    Log.e(MAIN_TAG, authEx.toString());
                }
                return token;
            }

            @Override
            protected void onPostExecute(String token)
            {
                Log.i(MAIN_TAG, "Access token retrieved: " + token);
                HashMap<String, String> params = new HashMap<>();
                params.put("email", Plus.AccountApi.getAccountName(mGoogleApiClient));
                params.put("code", token);
                ParseCloud.callFunctionInBackground("accessGoogleUser", params, new FunctionCallback<String>() {
                    @Override
                    public void done(String result, ParseException e) {
                        Log.i(MAIN_TAG, result);
                    }
                });
            }
        };
        task.execute();
        //navigateToHomeScreen();
    }

    @Override
    public void onConnectionSuspended(int cause)
    {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult)
    {
        if(!connectionResult.hasResolution())
        {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
            return;
        }
        if(!mIntentInProgress)
        {
            mConnectionResult = connectionResult;

            if(mGplusClicked)
            {
                resolveSignInError();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent)
    {
        if(requestCode == RC_SIGN_IN)
        {
            if(responseCode != RESULT_OK)
            {
                mGplusClicked = false;
            }
            mIntentInProgress = false;
            if(!mGoogleApiClient.isConnecting())
            {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void resolveSignInError()
    {
        if(mConnectionResult.hasResolution())
        {
            try
            {
                mIntentInProgress = true;
                startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            }
            catch (IntentSender.SendIntentException e)
            {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    private void navigateToHomeScreen()
    {
        Intent intent = new Intent(this, HomeActivity.class);

        String message = username.getText().toString() + ' ' + password.getText().toString();
        intent.putExtra(AUTH_MESSAGE, message);
        startActivity(intent);
    }
}
