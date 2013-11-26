package com.google.android.apps.mytracks.services;

import com.google.android.apps.mytracks.Constants;
import com.google.android.apps.mytracks.GamificationAchievement;
import com.google.android.apps.mytracks.GamificationActivity;
import com.google.android.apps.mytracks.content.MyTracksProviderUtils;
import com.google.android.apps.mytracks.content.Track;
import com.google.android.maps.mytracks.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class GamificationService extends Service {

  private final IBinder mBinder = new LocalBinder();

  @Override
  public IBinder onBind(Intent intent) {
    return mBinder;
  }

  @Override
  public void onCreate() {
    IntentFilter filter = new IntentFilter();
    filter.addAction(getString(R.string.track_stopped_and_saved_broadcast_action));
    filter.addAction(getString(R.string.track_deleted_broadcast_action));
    registerReceiver(receiver, filter);
  }

  public class LocalBinder extends Binder {
    GamificationService getService() {
      return GamificationService.this;
    }
  }

  private final BroadcastReceiver receiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
          List<GamificationAchievement> achievements = new ArrayList<GamificationAchievement>();
          InputStream raw = context.getAssets().open("gamification.xml");
          DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
          DocumentBuilder builder = factory.newDocumentBuilder();
          Document d = builder.parse(raw);
          NodeList nodeList = d.getElementsByTagName("Challenge");
          
          for (int i = 0; i < nodeList.getLength(); i++) {
            achievements.add(new GamificationAchievement(nodeList.item(i)));
          }
          
          for(GamificationAchievement a : achievements){
            if(isPassedAchievement(a)){
              sendAsNotification(a);
              setChallengeCompleted(a.getId());
            }
            else{
              setChallengeNotCompleted(a.getId());
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
    }
  };
  
  private void sendAsNotification(GamificationAchievement a){
    NotificationCompat.Builder builder = new NotificationCompat.Builder(
        GamificationService.this).setSmallIcon(R.drawable.troll)
        .setContentTitle("Achievement").setContentText(a.getMessage());
    Intent targetIntent = new Intent(GamificationService.this, GamificationActivity.class);
    PendingIntent contentIntent = PendingIntent.getActivity(GamificationService.this, 0, targetIntent,
        PendingIntent.FLAG_CANCEL_CURRENT);
    builder.setContentIntent(contentIntent);
    NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    Notification n = builder.build();
    n.flags |= Notification.FLAG_AUTO_CANCEL;
    nManager.notify(a.getId(), n);
  }
  
  private boolean isPassedAchievement(GamificationAchievement a){
    List<String> keys = a.getKeys();
    Map<String, String> values = a.getValues();
    for(String s : keys){
      if(!isPassedCriterion(s, values.get(s))){
        return false;
      }
    }
    return true;
  }
  
  private boolean isPassedCriterion(String criterion, String value){
    MyTracksProviderUtils myTracksProviderUtils = MyTracksProviderUtils.Factory.get(GamificationService.this);
    if(criterion.equals(getString(R.string.gamification_criterion_id))){
//      Should not use the settings shared preference here, need to change that. 
      SharedPreferences sp = this.getApplicationContext().getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
      if(!sp.getBoolean("completed-" + value, false)){
        return true;
      }
    }
    else if(criterion.equals(getString(R.string.gamification_criterion_message))){
      return true;
    }
    else if(criterion.equals(getString(R.string.gamification_criterion_title))){
      return true;
    }
    else if(criterion.equals(getString(R.string.gamification_criterion_lengthlasttrack))){
      return myTracksProviderUtils.getLastTrack().getTripStatistics().getTotalDistance() > Double.parseDouble(value);
    }
    else if(criterion.equals(getString(R.string.gamification_criterion_lengthtotal))){
      List<Track> tracks = myTracksProviderUtils.getAllTracks();
      double distance = 0;
      for(Track t : tracks){
        distance += t.getTripStatistics().getTotalDistance();
      }
      return distance >= Double.parseDouble(value);
    }
    else if(criterion.equals(getString(R.string.gamification_criterion_totaltrips))){
      return myTracksProviderUtils.getAllTracks().size() >= Integer.parseInt(value);
    }
    else{
      Toast.makeText(this, "Didn't find match for some criterion!", Toast.LENGTH_SHORT).show();
    }
    return false;
  }

private void setChallengeCompleted(int id){
    SharedPreferences sp = this.getApplicationContext().getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_APPEND);
    sp.edit().putBoolean("completed-" + id, true).commit();
  }

private void setChallengeNotCompleted(int id){
  SharedPreferences sp = this.getApplicationContext().getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_APPEND);
  sp.edit().putBoolean("completed-" + id, false).commit();
}
}
