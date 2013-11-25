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
  public double getTargetPace();
  
  public double getCurrentPace();
  
  public int getWarningPeriod();
    
  /**
   * Sets how frequently in ms a user should be warned once outside target pace
   * @param period how many ms between each message
   */
  public void setWarningPeriod(int period);

  /**
   * Sets the target pace in m/s
   * @param pace the target pace in m/s
   */
  public void setTargetPace(double pace);
  
  public String getPaceMessage();
  
  /**
   * Sets how close to the target pace the user must remain to not be warned
   * @param deviation an integer value in percentage - 10 means must keep within 10% of speed
   */
  public void setWarningThreshhold(int deviation);
  
  /**
   * Returns this PaceController as a PaceListener
   * @return a PaceListener view of this PaceController
   */
  public PaceListener asPaceListener();
}
