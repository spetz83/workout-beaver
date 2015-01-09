package me.spetz83.workoutbeaver.modules;

import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.spetz83.workoutbeaver.HomeActivity;
import me.spetz83.workoutbeaver.MainActivity;

/**
 * Created by tom on 1/8/2015.
 */

@Module(injects = {MainActivity.class, HomeActivity.class})
public class EventBusModule
{
   @Provides
   @Singleton
   Bus provideBus()
   {
       return new Bus();
   }
}
