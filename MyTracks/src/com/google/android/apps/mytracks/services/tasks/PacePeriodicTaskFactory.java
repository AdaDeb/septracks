package com.google.android.apps.mytracks.services.tasks;

import android.content.Context;

public class PacePeriodicTaskFactory implements PeriodicTaskFactory {

  @Override
  public PeriodicTask create(Context context) {
    return new PacePeriodicTask(context);
  }
}
