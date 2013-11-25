package com.google.android.apps.mytracks.services.tasks;

import android.content.Context;

/**
 * {@link PeriodicTaskFactory} is used for text-to-speech announcement of the PacePeriodicTask.
 * 
 * @author Johan Grundén, Björn Lexell
 *
 */
public class PacePeriodicTaskFactory implements PeriodicTaskFactory {

  @Override
  public PeriodicTask create(Context context) {
    return new PacePeriodicTask(context);
  }
}
