package me.spetz83.workoutbeaver;

import dagger.ObjectGraph;

/**
 * Created by Tom on 1/9/2015.
 */
public final class DaggerInjector
{
    public static ObjectGraph objectGraph;

    public static void init(final Object... modules)
    {
        if(objectGraph == null)
        {
            objectGraph = ObjectGraph.create(modules);
        }
        else
        {
            objectGraph = objectGraph.plus(modules);
        }
        objectGraph.injectStatics();
    }

    public static final void inject(final Object target)
    {
        objectGraph.inject(target);
    }

    public static <T> T resolve(Class<T> type)
    {
        return objectGraph.get(type);
    }
}
