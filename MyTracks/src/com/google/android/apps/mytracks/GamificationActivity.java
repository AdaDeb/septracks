package com.google.android.apps.mytracks;

import com.google.android.maps.mytracks.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

// Check out which superclass is most suitable
public class GamificationActivity extends Activity {

@Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setContentView(R.layout.gamificationview);

    readInAchievements();
  }

  @Override
  protected void onResume(){
    super.onResume();
    updateCompletedAchievements();
  }
  
  private void updateCompletedAchievements(){
//  Updating completed achievements
    Intent intent = new Intent().setAction(getString(R.string.track_deleted_broadcast_action)).putExtra(getString(R.string.track_id_broadcast_extra), 0);
    sendBroadcast(intent, getString(R.string.permission_notification_value));
  }

  private void readInAchievements() {
    try{
      LinearLayout mainLayout = (LinearLayout)findViewById(R.id.gamificationMainLayout);
      
      List<GamificationAchievement> achievements = new ArrayList<GamificationAchievement>();
      InputStream raw = this.getAssets().open("gamification.xml");
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document d = builder.parse(raw);
      NodeList nodeList = d.getElementsByTagName("Challenge");
      
      for (int i = 0; i < nodeList.getLength(); i++) {
        achievements.add(new GamificationAchievement(nodeList.item(i)));
      }
      
      for(GamificationAchievement a : achievements){
        mainLayout.addView(getViewFromAchievement(a));
      }
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }
  
  private View getViewFromAchievement(GamificationAchievement a){
    LinearLayout l = new LinearLayout(this);
    l.setGravity(Gravity.CENTER);
   
//    TextView t = new TextView(this);
//    t.setText(a.getTitle());
//    t.setTextSize(22f);
    
    final CheckBox c = new CheckBox(this);
    c.setClickable(false);
    c.setText(a.getTitle());
    c.setTextSize(20);
    l.addView(c);
    
    SharedPreferences sp = this.getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
    if(sp.getBoolean("completed-" + a.getId(), false)){ 
      c.setSelected(true);
      c.setChecked(true);
    }
    else{
      c.setSelected(false);
    }
    
    return l;
  }
}
