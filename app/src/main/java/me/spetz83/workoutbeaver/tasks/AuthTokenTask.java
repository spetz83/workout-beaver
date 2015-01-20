package me.spetz83.workoutbeaver.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import java.io.IOException;

/**
 * Created by Tom on 1/16/2015.
 */
public class AuthTokenTask
{
    public AuthTokenTask()
    {

    }

    public String retreiveAuthToken(GoogleApiClient apiClient)
    {
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String accessToken = null;
                try
                {
                    accessToken = GoogleAuthUtil.getToken(this,
                            Plus.AccountApi.getAccountName(apiClient),
                            "oauth2:" + Plus.SCOPE_PLUS_PROFILE);
                    Log.d(MAIN_TAG, accessToken);
                }
                catch(IOException transietEx)
                {
                    return;
                }
                catch(UserRecoverableAuthException e)
                {
                    accessToken = null;
                }
                catch(Exception e)
                {
                    throw new RuntimeException(e);
                }

                return accessToken;
            }
        }
    }


}
