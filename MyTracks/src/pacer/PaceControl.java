package pacer;

import com.google.android.apps.mytracks.stats.DoubleBuffer;

import android.util.Log;

/**
 * A class that helps users keep to a certain predefined pace by 
 * comparing the current speed to a desired target speed. 
 * Periodically issues voice alerts to speed up or slow down if the user's
 * current pace falls outside a distance from the target pace.  
 * 
 * @author axellj, urdell
 */
public class PaceControl implements PaceListener, PaceController{
  private static final String TAG = PaceControl.class.getSimpleName();
  
  private DoubleBuffer paceBuffer;
  private Double targetPace;  // pace is in m/s
  private Double currentPace; // pace is in m/s
  private int warningPeriod;  // period is in s (period=60 means one potential warning a minute)
  private double paceDiff; // difference between target pace and current pace in m/s
  
  //factor determining how far you can deviate from the target pace without warning 
  private Double epsilon;  
  
  public PaceControl(int targetPace){
    //TODO dynamic size of buffer - take GPS settings into account! 
    paceBuffer = new DoubleBuffer(10);
    this.targetPace = (double) targetPace;
  }
  
  public PaceControl(){
    this(10);
  }

  @Override
  public void updateSpeed(double speed) {
    handleSpeedUpdate(speed);
  }
  
  private void handleSpeedUpdate(double speed){ 
    paceBuffer.setNext(speed);
    currentPace = paceBuffer.getAverage();

    epsilon = 0.1 * currentPace; // TODO use a logarithmic or similar to account for different activities
    Log.i("Pace", "Speed was updated. Speed was: " + speed + ", " +
    		"currentPace: " + currentPace + ", targetPace: " + targetPace);
    
    paceDiff = currentPace - targetPace; 
    
  }
  
  @Override
  public double getStatus(){
    sendPaceAlert(paceDiff); // DEBUG remove
    return Math.abs(paceDiff) > epsilon ? paceDiff : 0;
  }
  
  private void sendPaceAlert(Double paceDiff){
    Log.i(TAG, "PACE ALERT!");
    if (paceDiff > 0){
      Log.i(TAG, "SLOW DOWN");
    } else {
      Log.i(TAG, "SPEED UP");
    }
  }
  
  @Override
  public double getTargetPace(){
    return targetPace;
  }
  
  @Override
  public double getCurrentPace(){
    return currentPace;
  }
  
  @Override
  public int getWarningPeriod(){
    return warningPeriod;
  }
    
  @Override
  public void setWarningPeriod(int period) {
    warningPeriod = period;  
  }

  @Override
  public void setTargetPace(int pace) {
    targetPace = (double) pace; // TODO double input     
  }
  
  @Override
  public PaceListener asPaceListener(){
    return (PaceListener) this;
  }
  
}
