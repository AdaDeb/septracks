package com.google.android.apps.mytracks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

public class GamificationAchievement {

  private Node node;
  private Map<String, String> values;
  private List<String> keys;
  
  public GamificationAchievement(Node node){
    this.node = node;
    this.values = new HashMap<String, String>();
    this.keys = new ArrayList<String>();
    parseNode();
  }
  
  private void parseNode(){
    for (int i = 0; i < node.getChildNodes().getLength(); i++) {
      String text = node.getChildNodes().item(i).getTextContent();
      String name = node.getChildNodes().item(i).getNodeName();
      if(node.getChildNodes().item(i).getNodeType() == Node.DOCUMENT_POSITION_DISCONNECTED){
        values.put(name, text);
        keys.add(name);
      }
    }
  }
  
  public Map<String, String> getValues(){
    return values;
  }
  
  public List<String> getKeys(){
    return keys;
  }
  
  public int getId(){
    if(values.containsKey("id")){
      return Integer.parseInt(values.get("id"));
    }
    return 0;
  }
  
  public String getMessage(){
    if(values.containsKey("message")){
      return values.get("message");
    }
    return "No Message Found";
  }
  
  public String getTitle(){
    if(values.containsKey("title")){
      return values.get("title");
    }
    return "No Title found!";
  }
  
}
