package Modules;
abstract class Waiter {
    void waitTask(int milliseconds){
        if (milliseconds<=0) return;
        try{
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex){
            throw new RuntimeException(ex);
        }
    }
}

public class PauseThread extends Waiter {
    public PauseThread(int milliseconds){
        waitTask(milliseconds);
        // float passedSeconds = milliseconds/1000;
        // System.out.println("Telah memberhentikan program selama "+milliseconds+" mdtk atau "+passedSeconds+" dtk.");
    }
}
