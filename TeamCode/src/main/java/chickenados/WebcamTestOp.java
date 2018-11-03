package chickenados;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.internal.camera.WebcamExample;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

import java.io.File;

@TeleOp(name = "Webcam CV Test")
public class WebcamTestOp extends LinearOpMode{

    VuforiaLocalizer vuforia;
    WebcamName webcam;

    @Override
    public void runOpMode(){

        try{
            Thread.sleep(20);
        } catch (InterruptedException e) { return;}

        webcam = hardwareMap.get(WebcamName.class, "Webcam 1");

        //int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = "AV2hPmr/////AAABmQLD9hUunkK4tSZiwFAlrpZPoN76Ej8hCf1AdzRK5+dWdO6VF0iKY/cqgZLxkQ4RCD0KXMvXtiUx87IkUWaghhJYq446Zx2MDU12MXtsE9hq8p3alcdmCCvCun+veOD/mwKlEXDnZYl8jMzxcCOpEqr3Uc2MzsjpFbrdr+m5tYXmNAKQrN9Bq4VALSSl/pUhk1/swPiJenMa938xu0pN4C+xuOCyAmNX44yln0q8GnoGmtmdMCg3NTOiEDm6K/fFTLI1nWN2LOWzVQZ88Ul0EIjgdTfA+DYgz5O8AS/leZcUn7WTbPbhy/5NaqorhI+6u1YMYYFaPq41j3lenoUU+6DdfK133dZ8+M57EvFVXJSv";
        parameters.cameraName = webcam;



        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        //vuforia.enableConvertFrameToBitmap();

        waitForStart();


        while(opModeIsActive()){



        }

    }
}
