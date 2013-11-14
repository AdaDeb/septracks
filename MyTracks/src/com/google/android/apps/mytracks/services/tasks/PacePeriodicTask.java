package com.google.android.apps.mytracks.services.tasks;

import android.content.Context;

import pacer.PaceControl;

public class PacePeriodicTask extends AnnouncementPeriodicTask {

  public PacePeriodicTask(Context context) {
    super(context);
    // TODO Auto-generated constructor stub
  }
  
  protected String getAnnouncement(PaceControl pc){
    /**
     * string ret = "";
     * int state = pc.getState();
     * switch (state){
     *      case -1:
     *          ret = "Too slow"
     *          break;
     *      case 0:
     *          break;
     *      case 1:
     *          ret = "Too fast"
     *          break;
     *   }
     *   return ret;
     */
    return null;
  }

}
