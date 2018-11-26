package chickenlib.display;

import android.graphics.Paint;
import android.widget.TextView;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.ArrayList;

import chickenlib.CknTaskManager;
import chickenlib.logging.CknDbgLog;
import chickenlib.util.CknUtil;

public class CknSmartDashboard implements CknTaskManager.Task {

    public static class Parameters {
        public int displayWidth = 1080;
        public int numLines = 16;
        public int updateRateMS = 50;
    }

    private Parameters params;

    private static CknSmartDashboard instance;
    private Telemetry telemetry = null;

    private Telemetry.Item[] display;

    private int numLines;

    private TextView textView;
    private Paint paint;

    private double prevTime = 0.0;

    /**
     * Create a new instance of the Smart Dashboard
     * @param telemetry telemetry object.
     * @return instance
     */
    public static CknSmartDashboard createInstance(Telemetry telemetry, Parameters params){
        if(instance == null){
            instance = new CknSmartDashboard(telemetry, params);
        }

        return instance;
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
     * @param params
     */
    public CknSmartDashboard(Telemetry telemetry, Parameters params){
        this.params = params;
        this.numLines = params.numLines;
        this.display = new Telemetry.Item[numLines];

        // Turns off autoClear and creates a blank dashboard.
        this.telemetry = telemetry;
        telemetry.setAutoClear(false);
        telemetry.clearAll();

        for(int i = 0; i < numLines; i++){
            display[i] = telemetry.addData(i +"", "");
        }
        telemetry.update();
    }

    public void setLine(int lineNum, String text){
        if(lineNum >= numLines || lineNum < 0){
            throw new IllegalArgumentException("Invalid line number!");
        }
        display[lineNum].setValue(text);
        telemetry.update();
    }

    public void clearDisplay(){
        for(int i = 0; i < numLines; i++){
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

    public void setTaskEnabled(boolean enabled){
        if(enabled){
            CknTaskManager.getInstance().registerTask(this, CknTaskManager.TaskType.PRECONTINUOUS);
        } else {
            CknTaskManager.getInstance().unregisterTask(this, CknTaskManager.TaskType.PRECONTINUOUS);
        }
    }

    @Override
    public void preContinuous(){
        // Update the display as close to a constant rate as possible, as specified in the parameters.
        if(CknUtil.getCurrentTime() - prevTime > params.updateRateMS){

        }
    }

    @Override
    public void postContinuous(){

    }
}
