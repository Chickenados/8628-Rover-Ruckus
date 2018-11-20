package chickenlib.opmode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import chickenlib.CknTaskManager;
import chickenlib.display.CknSmartDashboard;

public abstract class CknOpMode extends LinearOpMode {

    public CknOpMode(){
        super();

        mgr = new CknTaskManager();
    }

    private static final int NUM_LINES = 16;

    // Do all initializing here.
    public abstract void cknInit();

    public abstract void cknStart();

    public abstract void cknLoop();

    public abstract void cknStop();

    private CknTaskManager mgr;
    private CknSmartDashboard dash;
    private long loopCount = 0;

    public long getLoopCount(){
        return loopCount;
    }

    @Override
    public void runOpMode(){
        dash = new CknSmartDashboard(telemetry, null);

        cknInit();

        waitForStart();

        cknStart();

        while(opModeIsActive()){
            loopCount++;
            mgr.executeTasks(CknTaskManager.TaskType.PRECONTINUOUS);

            cknLoop();

            mgr.executeTasks(CknTaskManager.TaskType.POSTCONTINUOUS);
        }

    }


}
