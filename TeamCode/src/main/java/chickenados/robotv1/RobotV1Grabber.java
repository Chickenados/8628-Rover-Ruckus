package chickenados.robotv1;

import com.qualcomm.robotcore.hardware.CRServo;

import chickenlib.util.CknEvent;

public class RobotV1Grabber {

    private CRServo grabber;

    RobotV1Grabber(CRServo servo){
        this.grabber = servo;
    }

    /**
     * Releases the grabber and sleeps for 3 seconds before triggering event.
     * @param event event to trigger once action is done.
     * @throws InterruptedException if sleep thread is interrupted
     */
    public void release(CknEvent event) throws InterruptedException{
        grabber.setPower(-1);
        Thread.sleep(4000);
        grabber.setPower(0);
        if(event != null) event.set(true);
    }

    public void release() throws InterruptedException{
        release(null);
    }

}
