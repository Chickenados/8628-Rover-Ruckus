package chickenlib;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import javax.crypto.spec.DESedeKeySpec;

public class CknSmartDashboard {

    private static final int DEFAULT_NUM_LINES = 16;

    private static CknSmartDashboard instance;
    private Telemetry telemetry = null;
    private Telemetry.Item[] display;

    /**
     * Create a new instance of the Smart Dashboard
     * @param telemetry telemetry object.
     * @return instance
     */
    public static CknSmartDashboard createInstance(Telemetry telemetry, int numLines){
        if(instance == null){
            instance = new CknSmartDashboard(telemetry, numLines);
        }

        return instance;
    }

    public static CknSmartDashboard createInstance(Telemetry telemetry){
        return createInstance(telemetry, DEFAULT_NUM_LINES);
    }

    /**
     * Get the current instance of this class
     * @return instnace
     */
    public static CknSmartDashboard getInstance(){
        return instance;
    }

    /**
     * Constructor
     * @param telemetry
     * @param numLines
     */
    public CknSmartDashboard(Telemetry telemetry, int numLines){

        // Turns off autoClear and creates a blank dashboard.
        this.telemetry = telemetry;
        telemetry.setAutoClear(false);
        telemetry.clearAll();
        display = new Telemetry.Item[numLines];
        for(int i = 0; i < display.length; i++){
            display[i] = telemetry.addData(Integer.toString(i), "");
        }
        telemetry.update();
    }

    public void setLine(int lineNum, String text){
        if(lineNum > display.length || lineNum < 0){
            throw new IllegalArgumentException("Invalid line number!");
        }
        display[lineNum].setValue(text);
        telemetry.update();
    }

    public void clearDisplay(){
        for(int i = 0; i < display.length; i++){
            display[i].setValue("");
        }
        telemetry.update();
    }

    public void updateDisplay(){
        telemetry.update();
    }
}
