package chickenlib.display;

import android.graphics.Paint;
import android.widget.TextView;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import chickenlib.CknTaskManager;
import chickenlib.logging.CknDbgLog;

public class CknSmartDashboard implements CknTaskManager.Task {

    private static final int DEFAULT_NUM_LINES = 16;

    private static CknSmartDashboard instance;
    private Telemetry telemetry = null;
    private Telemetry.Item[] display;

    private int displayWidth;

    private TextView textView;
    private Paint paint;

    /**
     * Create a new instance of the Smart Dashboard
     * @param telemetry telemetry object.
     * @return instance
     */
    public static CknSmartDashboard createInstance(Telemetry telemetry, int displayWidth, int numLines){
        if(instance == null){
            instance = new CknSmartDashboard(telemetry, displayWidth, numLines);
        }

        return instance;
    }

    public static CknSmartDashboard createInstance(Telemetry telemetry, int displayWidth){
        return createInstance(telemetry, displayWidth, DEFAULT_NUM_LINES);
    }

    public static CknSmartDashboard createInstance(Telemetry telemetry){
        return createInstance(telemetry, 1080, DEFAULT_NUM_LINES);
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
     * @param displayWidth
     * @param numLines
     */
    public CknSmartDashboard(Telemetry telemetry, int displayWidth, int numLines){

        this.displayWidth = displayWidth;

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

    public void setTextView(TextView textView){
        this.textView = textView;
        this.paint = textView.getPaint();
    }

    public String alignRight(String message){
        if(paint != null){
            int padding = Math.round(paint.measureText(" "));
        }
        CknDbgLog.msg(CknDbgLog.Priority.WARN, "Cannot align text without TextView!");
        return message;

    }

    public void appendInfo(){

    }

    @Override
    public void preContinuous(){

    }

    @Override
    public void postContinuous(){

    }
}
