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
  private Double epsilon;  
  
  public PaceControl(int targetPace){
    //TODO dynamic size
    paceBuffer = new DoubleBuffer(10);
    this.targetPace = (double) targetPace;
  }


  @Override
  public void updateSpeed(double speed) {
    handleSpeedUpdate(speed);
  }
  
  private void handleSpeedUpdate(double speed){ 
    paceBuffer.setNext(speed);
    currentPace = paceBuffer.getAverage();
    epsilon = 0.1 * currentPace; // TODO use a logarithmic or similar to account for different activities
    Log.d("Pace", "Speed was updated. Speed was: " + speed + ", " +
    		"currentPace: " + currentPace + ", targetPace: " + targetPace);
    
    Double paceDiff = currentPace - targetPace; 
    if (Math.abs(paceDiff) > epsilon){
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
