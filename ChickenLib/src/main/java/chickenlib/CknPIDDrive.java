package chickenlib;

public class CknPIDDrive {

    CknDriveBase driveBase;
    CknPIDController yPid;
    CknPIDController turnPid;
    CknEvent event;

    private boolean isActive = false;
    double startTime;
    double timeout;

    public CknPIDDrive(CknDriveBase driveBase, CknPIDController yPid, CknPIDController turnPid){
        this.driveBase = driveBase;
        this.yPid = yPid;
        this.turnPid = turnPid;
    }

    public void driveStraightTankLoop(double distance, double heading, double timeout, CknEvent event){

        if(event != null) {
            event.reset();
        }

        double leftPower, rightPower;
        double startTime = CknUtil.getCurrentTime();

        //Convert the distance from inches to encoder ticks.
        double target = distance / ((3.1415 * driveBase.diameter) / driveBase.ticksPerRev);

        yPid.setSetPoint(target, true);
        //turnPid.setSetPoint(heading);

        while(!yPid.onTarget() && CknUtil.getCurrentTime() < timeout + startTime){

            //CknSmartDashboard.getInstance().setLine(5, "WHILE");


            leftPower = yPid.getOutput();
            rightPower = yPid.getOutput();

            driveBase.tankDrive(leftPower, rightPower);

        }

        if(event != null){
            event.set(true);
        }

    }

    public void driveDistanceTank(double distance, double heading, double timeout, CknEvent event){

        if(event != null) {
            event.reset();
            this.event = event;
        }

        this.timeout = timeout;

        startTime = CknUtil.getCurrentTime();

        //Convert the distance from inches to encoder ticks.
        double target = distance / ((((3.1415 * driveBase.diameter)) / driveBase.ticksPerRev) * driveBase.getGearRatio());

        yPid.setSetPoint(target, true);
        turnPid.setSetPoint(heading, false);

        isActive = true;

    }

    public void driveStraightTankLoop(double distance, double heading){
        driveStraightTankLoop(distance, heading, 0, null);
    }

    public void setTarget(double target){
        yPid.setSetPoint(target, true);
    }

    public void stop(){
        if(turnPid != null){
            turnPid.reset();
        }
        if(yPid != null){
            yPid.reset();
        }

        driveBase.stopMotors();
    }


    //Call this method every loop possible

    public void handlePIDs(){
        if(isActive){

            double leftPower, rightPower;

            //TODO: Test Gyro assistance and turn support
            leftPower = yPid.getOutput() + turnPid.getOutput();
            rightPower = yPid.getOutput() - turnPid.getOutput();

            //CknSmartDashboard.getInstance().setLine(7, "Out: " + turnPid.getOutput());

            driveBase.tankDrive(leftPower, rightPower);

            //Check if the robot has reached the target or timed out
            if((yPid.onTarget() && turnPid.onTarget()) || CknUtil.getCurrentTime() > startTime + timeout){
                isActive = false;
                stop();
                if(event != null){
                    event.set(true);
                }
            }

        }


    }

}
