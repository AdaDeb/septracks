package pacer;

public class PaceFactory {

  public static PaceController getPaceController(int targetPace){
    return new PaceControl(targetPace);
  }

  public static PaceController getPaceController() {
    return getPaceController(10);
  }
  
  
}
