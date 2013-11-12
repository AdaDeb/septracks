package pacer;

public class PaceFactory {

  public static PaceListener getPaceListener(){
    return new PaceControl();
  }
  
  
}
