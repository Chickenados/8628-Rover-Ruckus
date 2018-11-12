package chickenados;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import chickenlib.util.CknSmartDashboard;
import chickenlib.CknTaskManager;

@TeleOp(name = "Task Manager Example")
@Disabled
public class TaskManagerOp extends LinearOpMode{

    CknTaskManager mgr = new CknTaskManager();
    CknSmartDashboard dash;

    @Override
    public void runOpMode(){

        dash = CknSmartDashboard.createInstance(telemetry);

        ExampleTask task = new ExampleTask(dash);
        task.enableTask();

        waitForStart();

        while(opModeIsActive()){
            // This should be called first thing in the loop
            mgr.executeTasks(CknTaskManager.TaskType.PRECONTINUOUS);


            // This should be called last thing in the loop
            mgr.executeTasks(CknTaskManager.TaskType.POSTCONTINUOUS);
        }

    }
}
