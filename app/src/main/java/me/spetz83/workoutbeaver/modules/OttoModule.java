package me.spetz83.workoutbeaver.modules;

import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by tom on 1/8/2015.
 */

@Module(
        injects =
        {
            Bus.class
        }
)
public class OttoModule
{
    private Bus bus;

    public OttoModule(Bus bus)
    {
        this.bus = bus;
    }

    @Provides @Singleton public Bus provideOttoBus()
    {
        return bus;
    }
}
