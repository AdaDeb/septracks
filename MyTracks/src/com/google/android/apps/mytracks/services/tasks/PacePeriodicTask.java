package com.google.android.apps.mytracks.services.tasks;

import com.google.android.apps.mytracks.services.TrackRecordingService;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import pacer.PaceController;

public class PacePeriodicTask extends AnnouncementPeriodicTask {


  private static final String TAG = PacePeriodicTask.class.getSimpleName();
  
  public PacePeriodicTask(Context context) {
    super(context);
  }
  
  protected String getAnnouncement(PaceController pc){
   
     String ret = "";
     double state = pc.getStatus();
     
     if(state < 0)
       ret = "Too slow";
     else if(state == 0)
       ret = "On target pace";
     else if(state > 0) 
       ret = "Too fast";           
    return ret;   
  }
  
  @Override
  public void run(TrackRecordingService trackRecordingService) {
    if (trackRecordingService == null) {
      Log.e(TAG, "TrackRecordingService is null.");
      return;
    }
    announce(trackRecordingService.getPaceController());
  }
  
  void announce(PaceController pc) {
    if (pc == null) {
      Log.e(TAG, "PaceControl is null.");
      return;
    }

    synchronized (this) {
      if (!ready) {
        ready = initStatus == TextToSpeech.SUCCESS;
        if (ready) {
          onTtsReady();
        }
      }
      if (!ready) {
        Log.i(TAG, "TTS not ready.");
        return;
      }
    }

    if (!speechAllowed) {
      Log.i(TAG, "Speech is not allowed at this time.");
      return;
    }
    speakAnnouncement(getAnnouncement(pc));
  }

}
