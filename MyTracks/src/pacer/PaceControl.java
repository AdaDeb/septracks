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
  
  private enum PaceState {
    ON_PACE, UNDER_PACE, OVER_PACE
  };
  
  private PaceState previousState; // Keeps track on the status of the pace
  
  // String constants for voice messages
  public static final String SPEED_UP = "Speed up.";
  public static final String SLOW_DOWN = "Slow down.";
  public static final String ON_PACE = "Reached target pace.";
  
  
  //factor determining how far you can deviate from the target pace without warning 
  //Epsilon is max value to prevent pacer to send messages when standing still or accruing GPS
  private Double epsilon = Double.MAX_VALUE;  
  
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
  
  // Returns the updated state
  private PaceState getUpdatedState()
  {
    PaceState newState;
    if(Math.abs(paceDiff) < epsilon)
      newState = PaceState.ON_PACE;
    else if(paceDiff > 0)
      newState = PaceState.OVER_PACE;
    else
      newState = PaceState.UNDER_PACE;
    
    return newState;
  }

  // Returns a message of the pace used for voice output.
  public String getPaceMessage()
  {
    String ret = "";
    PaceState current = getUpdatedState();
    if(current != previousState){
      if(current == PaceState.ON_PACE)
        ret = ON_PACE;
      else if(current == PaceState.OVER_PACE)
        ret = SPEED_UP;
      else
        ret = SLOW_DOWN;
    }
    
    // Update the state.
    previousState = current;
    
    return ret;
  }
  
  private PaceState getState(){
    sendPaceAlert(paceDiff); // DEBUG remove
    return previousState;
  }
  
  // Returns a string that is used for announcing the state of the current pace.
  @Override
  public String getStateVoiceMessage(){
    PaceState state = getState();
    previousState = getUpdatedState();
    
    if(state == PaceState.OVER_PACE)
      return SLOW_DOWN;
    else if (state == PaceState.UNDER_PACE)
      return SPEED_UP;
    else 
      return ON_PACE;    
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
