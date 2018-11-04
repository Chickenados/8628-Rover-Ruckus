package chickenados;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import chickenlib.CknSmartDashboard;
import chickenlib.CknTaskManager;
import chickenlib.CknUtil;
import chickenlib.ExampleTask;

@TeleOp(name = "Task Manager Test")
public class TaskManagerOp extends LinearOpMode{

    CknTaskManager mgr = new CknTaskManager();
    CknSmartDashboard dash;

    @Override
    public void runOpMode() throws InterruptedException{

        dash = CknSmartDashboard.createInstance(telemetry);

        boolean removed = false;

        ExampleTask task = new ExampleTask(dash);
        task.enableTask();

        waitForStart();

        double startTime = CknUtil.getCurrentTime();

        while(opModeIsActive()){
            mgr.executeTasks(CknTaskManager.TaskType.PRECONTINUOUS);

            if(CknUtil.getCurrentTime() > startTime + 5 && !removed){
                removed = true;
                mgr.unregisterTask(task, CknTaskManager.TaskType.POSTCONTINUOUS);
            }

            mgr.executeTasks(CknTaskManager.TaskType.POSTCONTINUOUS);
        }

    }
}
