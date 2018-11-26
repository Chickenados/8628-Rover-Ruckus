package chickenlib.inputstreams;

import com.qualcomm.robotcore.hardware.DcMotor;

public class CknEncoderInputStream extends CknInputStream<Integer>{

    DcMotor motor;

    public CknEncoderInputStream(DcMotor motor){
        this.motor = motor;
    }

    @Override
    public Integer getInput(){
        return motor.getCurrentPosition();
    }
}
