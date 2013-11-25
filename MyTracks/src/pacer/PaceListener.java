package pacer;


public interface PaceListener {
  
  /**
   * Inform the listener that a new speed has been recorded
   * @param speed the speed in m/s
   */
  void updateSpeed(double speed);
//  
//  /**
//   * Set how often you receive warnings when you go outside your specificed pace range  
//   * @param period seconds between each warning
//   */
//  void setWarningPeriod(int period);
//  
//  /**
//   * Set target pace  
//   * @param pace a pace in m/s 
//   */
//  void setTargetPace(int  pace);
//  
}
