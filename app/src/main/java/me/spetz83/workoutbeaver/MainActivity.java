package me.spetz83.workoutbeaver;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.spetz83.workoutbeaver.events.AuthEvent;


public class MainActivity extends AbstractWBActivity
{
    private static final String MAIN_TAG = "MAIN_ACT";

    public static final String AUTH_MESSAGE = "AUTH_MESSAGE";

    @Inject
    Bus bus;

    @InjectView(R.id.username)
    EditText username;

    @InjectView(R.id.password)
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        DaggerInjector.inject(this);
        ButterKnife.inject(this);
    }

    @OnClick(R.id.btn_login)
    public void login(View view)
    {
        Log.d(MAIN_TAG, "Login");
        Intent intent = new Intent(this, HomeActivity.class);

        String message = username.getText().toString() + ' ' + password.getText().toString();
        intent.putExtra(AUTH_MESSAGE, message);
        startActivity(intent);
    }

    @OnClick(R.id.btn_register)
    public void register(View view)
    {
        Log.d(MAIN_TAG, "Register");
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
}
