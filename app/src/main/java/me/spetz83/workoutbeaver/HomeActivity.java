package me.spetz83.workoutbeaver;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import me.spetz83.workoutbeaver.events.AuthEvent;


public class HomeActivity extends AbstractWBActivity
{
    @InjectView(R.id.statusText)
    TextView statusText;

    @Inject
    Bus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        DaggerInjector.inject(this);
        ButterKnife.inject(this);
        Intent intent = getIntent();

        statusText.setText(intent.getStringExtra(MainActivity.AUTH_MESSAGE));
    }

    @Subscribe
    public void onAuthEvent(AuthEvent event)
    {
        statusText.setText(event.message);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
