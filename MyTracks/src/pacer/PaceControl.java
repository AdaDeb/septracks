package pacer;

import com.google.android.apps.mytracks.stats.DoubleBuffer;

import android.content.Context;
import android.util.Log;

public class PaceControl implements PaceListener{
  private static final String TAG = PaceControl.class.getSimpleName();
  
  private DoubleBuffer paceBuffer;
  private Double targetPace;
  private Double currentPace;
  
  //factor determining how far you can deviate from the target pace without warning 
  private Double fuzzFactor;  
  
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
    fuzzFactor = 0.1 * currentPace; // TODO use a logarithmic or similar to account for different activities
    Log.d("Pace", "Speed was updated. Speed was: " + speed + ", " +
    		"currentPace: " + currentPace + ", targetPace: " + targetPace);
    
    Double paceDiff = currentPace - targetPace; 
    if (Math.abs(paceDiff) > fuzzFactor){
      sendPaceAlert(paceDiff);
    }
  }
  
  private void sendPaceAlert(Double paceDiff){
    Log.d(TAG, "PACE ALERT MOTHERFUCKER!");
    if (paceDiff > 0){
      Log.d(TAG, "SLOW DOWN yo");
    } else {
      Log.d(TAG, "SPEED UP YO!!!!!!");
    }
  }
    
  public void setContext(Context context){
    // paceContext = context;
  }
  
}
