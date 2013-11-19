package com.google.android.apps.mytracks.services.tasks;

import com.google.android.apps.mytracks.services.TrackRecordingService;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import pacer.PaceController;

/**
 * SEP-6
 * A task for announcing the status of the pace via voice messages.
 * It extends the AnnouncementPeriodicTask due to the fact that it also
 * handles voice messages (Text-To-Speech).
 * 
 * @author Björn Lexell, Johan Grundén
 *
 */
public class PacePeriodicTask extends AnnouncementPeriodicTask {

  private static final String TAG = PacePeriodicTask.class.getSimpleName();
  
  public PacePeriodicTask(Context context) {
    super(context);
  }
  
  protected String getAnnouncement(PaceController pc){
    return pc.getPaceMessage(); // Only speak if pace-state changes
    //return pc.getStateVoiceMessage(); // Speak state every 5th second
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
    String toAnnounce = getAnnouncement(pc);
    if(toAnnounce.length() > 0)
      speakAnnouncement(toAnnounce);
  }

}
