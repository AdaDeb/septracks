package com.google.android.apps.mytracks;

import com.google.android.maps.mytracks.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
   
    TextView t = new TextView(this);
    t.setText(a.getTitle());
    t.setTextSize(15f);
    
//    CheckBox c = new CheckBox(this);
//    c.setEnabled(false);
//    c.setText(a.getTitle());
    
    l.addView(t);
//    l.addView(c);
    
    SharedPreferences sp = this.getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);
    if(sp.getBoolean("completed-" + a.getId(), false)){    
      l.setBackgroundColor(Color.GREEN);
    }
    else{
      l.setBackgroundColor(Color.RED);
    }
    
    return l;
  }
}
