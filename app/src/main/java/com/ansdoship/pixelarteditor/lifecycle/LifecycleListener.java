package com.ansdoship.pixelarteditor.lifecycle;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public interface LifecycleListener extends LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onStart();

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onStop();

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy();

}
