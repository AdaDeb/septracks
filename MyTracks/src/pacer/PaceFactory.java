package pacer;

public class PaceFactory {

  public static PaceListener getPaceListener(int targetPace){
    return new PaceControl(targetPace);
  }
  
  
}
