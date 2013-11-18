package pacer;


public interface PaceController {

  /**
   * Returns whether user should slow down, speed up or stay on pace
   * 
   * Value > 0 means user should slow down; 
   * Value < 0 means user should speed up;
   * Value == 0 means user should retain pace 
   * @return double representing how user should change pace
   */
  public String getStateVoiceMessage();
  
  public double getTargetPace();
  
  public double getCurrentPace();
  
  public int getWarningPeriod();
    
  public void setWarningPeriod(int period);

  public void setTargetPace(int pace);
  
  public String getPaceMessage();
  
  /**
   * Returns this PaceController as a PaceListener
   * @return a PaceListener view of this PaceController
   */
  public PaceListener asPaceListener();
}
