package pacer;

import android.util.Log;

/**
 * A class that helps users keep to a certain predefined pace by 
 * comparing the current speed to a desired target speed. 
 * Periodically issues voice alerts to speed up or slow down if the user's
 * current pace falls outside a distance from the target pace.  
 * 
 * @author axellj, urdell, B.Lexell, J.Grundén
 */
public class PaceControl implements PaceListener, PaceController{
  private static final String TAG = PaceControl.class.getSimpleName();
  
  private Double targetPace;  // pace is in m/s
  private Double currentPace = 0D; // pace is in m/s
  private int warningPeriod;  // period is in s (period=60 means one potential warning a minute)
  private double paceDiff; // difference between target pace and current pace in m/s
  private PaceState previousState = PaceState.STANDING_STILL; // Keeps track on the status of the pace

  // factor determining how far you can deviate from the target pace without warning
  // Epsilon initially is max value to prevent pacer 
  // from sending messages when standing still or accruing GPS
  private double epsilon = Double.MAX_VALUE; 
  
  private double deviation = 0.1d; // is expressed as a factor [0,1] 
  private long lastRepeat = 0; // used for repeating voice messages
  
  private enum PaceState {
    ON_PACE("Reached target pace"), 
    UNDER_PACE("Speed up"), 
    OVER_PACE("Slow down"),
    STANDING_STILL("Standing still"); 
    
    private final String message; 
    
    private PaceState(String message){
      this.message = message;
    }
    
    public String message(){
      return message; 
    }
  };
  
  public PaceControl(double targetPace){
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
    currentPace = speed; 
    epsilon = deviation * currentPace; // TODO use a logarithmic or similar to account for different activities
    Log.i(TAG, "curDeviation: " + deviation + ", epsilon: " + epsilon);
    paceDiff = currentPace - targetPace; 
    Log.i(TAG, "targetPace: " + targetPace + " , curPace: " + currentPace + ", paceDiff: " + paceDiff);
    boolean onPace = Math.abs(paceDiff) < epsilon; // DEBUG
    Log.i(TAG, "Are we on pace? " + onPace);
  }
  
  // Returns the updated state
  private PaceState getUpdatedState()
  {
    
    PaceState newState;
    if(currentPace == 0){
      newState = PaceState.STANDING_STILL;
    }
    else if(Math.abs(paceDiff) < epsilon){
      newState = PaceState.ON_PACE;
    }
    else if(paceDiff > 0){
      newState = PaceState.OVER_PACE;
    }
    else {
      newState = PaceState.UNDER_PACE;
    }
    return newState;
  }
  
  /*
   * getPaceMessage returns a string with the current status of the pace. The string will be empty if the state is the same as
   * the previous state. If the state is under or over pace then the messages will be repeated every fifth second. 
   * This method is used by paceperiodtask.
   */
  public String getPaceMessage()
  {
    String ret = "";
    PaceState current = getUpdatedState();
    if (!previousState.equals(current)){
      Log.i(TAG, "STATE CHANGED woo!, new state is: " + current);
      
    }
    if(!current.message().equals(previousState.message()))
    {
      if(current.message().equals(PaceState.OVER_PACE.message()) || current.message().equals(PaceState.UNDER_PACE.message()))
      {
        lastRepeat = System.currentTimeMillis();
      }
      ret = current.message();
    }
    else if((current.message().equals(PaceState.OVER_PACE.message()) || current.message().equals(PaceState.UNDER_PACE.message())) && 
        (System.currentTimeMillis()- (warningPeriod * 1000)  > lastRepeat))
    {
      lastRepeat = System.currentTimeMillis();
      ret = current.message();
    }
    
    // Update the state.s
    previousState = current;
    
    return ret;
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
  public void setTargetPace(double pace) {
    targetPace = pace;     
  }
  
  @Override
  public PaceListener asPaceListener(){
    return (PaceListener) this;
  }

  @Override
  public void setWarningThreshhold(int deviation) {
    // deviation is a integer percentage value, e.g. 10 = we can deviate by 0.9
    this.deviation = (double)deviation/100;
    Log.i(TAG, "Got a deviation input of " + deviation + ", setting this.dev to " + this.deviation );
    
  }

}
