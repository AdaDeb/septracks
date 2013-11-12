package pacer;

import com.google.android.apps.mytracks.stats.DoubleBuffer;

import android.content.Context;
import android.util.Log;



public class PaceControl implements PaceListener{
  private DoubleBuffer paceBuffer;
  private Double targetPace;
  private Double currentPace;
  private Context paceContext;
  
  public PaceControl(){
    //TODO dynamic size
    paceBuffer = new DoubleBuffer(10);
    targetPace = 0.8;
  }


  @Override
  public void updateSpeed(double speed) {
    handleSpeedUpdate(speed);
  }
  
  private void handleSpeedUpdate(double speed){ 
    paceBuffer.setNext(speed);
    currentPace = paceBuffer.getAverage();
    //Toast.makeText(paceContext, "targetPace: " + targetPace + "   currentPace: " + currentPace , Toast.LENGTH_LONG).show();
    Log.d("Pace", "targetPace: " + targetPace + "   currentPace: " + currentPace);
  }
    
  public void setContext(Context context){
    paceContext = context;
  }
  
}
