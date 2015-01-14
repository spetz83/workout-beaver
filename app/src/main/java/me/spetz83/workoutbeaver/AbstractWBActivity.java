package me.spetz83.workoutbeaver;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import com.parse.Parse;

/**
 * Created by tom on 1/7/2015.
 */
public class AbstractWBActivity extends ActionBarActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar != null)
        {
            setSupportActionBar(toolbar);
        }

        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));
    }
}