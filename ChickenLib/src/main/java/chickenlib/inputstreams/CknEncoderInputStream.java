package chickenlib.inputstreams;

import com.qualcomm.robotcore.hardware.DcMotor;

public class CknEncoderInputStream extends CknInputStream{

    DcMotor motor;

    public CknEncoderInputStream(DcMotor motor){
        this.motor = motor;
    }

    @Override
    public Object getInput(){
        return (double) motor.getCurrentPosition();
    }
}
