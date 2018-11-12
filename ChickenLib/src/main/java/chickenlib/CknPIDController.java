package chickenlib;

import chickenlib.inputstreams.CknInputStream;
import chickenlib.util.CknSmartDashboard;
import chickenlib.util.CknUtil;

public class CknPIDController {

    private double pTerm;
    private double iTerm;
    private double dTerm;
    private double fTerm;

    private double deltaTime;
    private double deltaError;

    //TODO: Add input/output caps, include checks for those caps in I term, finish reset method,
    //TODO: add more options to make a more versatile PID control.

    // A class to group all of the PID coefficeints together
    public static class PIDCoefficients {
        public double kP;
        public double kI;
        public double kD;
        public double kF;

        /**
         *
         * @param kP P Coefficient
         * @param kI I Coefficient
         * @param kD D Coefficient
         * @param kF F Coefficient
         */
        public PIDCoefficients(double kP, double kI, double kD, double kF){
            this.kP = kP;
            this.kI = kI;
            this.kD = kD;
            this.kF = kF;
        }

        public PIDCoefficients(double kP, double kI, double kD){
            this.kP = kP;
            this.kI = kI;
            this.kD = kD;
            this.kF = 0.0;
        }

        public PIDCoefficients(double kP, double kD){
            this.kP = kP;
            this.kI = 0.0;
            this.kD = kD;
            this.kF = 0.0;
        }

    }

    PIDCoefficients pidCoef;
    CknInputStream inputStream;

    private double threshold;
    double currError = 0.0;
    private double setPoint;

    double prevTime = 0.0;
    double prevError;
    double totalError = 0.0;

    boolean isRelative;

    double timeThreshold;
    double targetTime;

    private double minOutput = -1.0, maxOutput = 1.0;

    public CknPIDController(PIDCoefficients pidCoef, CknInputStream inputStream, double threshold){
        this(pidCoef, inputStream, threshold, 0);
    }

    public CknPIDController(PIDCoefficients pidCoef, CknInputStream inputStream, double threshold, double timeThreshold){
        this.pidCoef = pidCoef;
        this.inputStream = inputStream;
        this.threshold = threshold;
        this.timeThreshold = timeThreshold;
    }

    public void setThreshold(double threshold){
        this.threshold = threshold;
    }

    public void setOutputRange(double min, double max){
        minOutput = min;
        maxOutput = max;
    }

    public void setMinOutput(double min){
        minOutput = min;
    }

    public void setMaxOutput(double max){
        maxOutput = max;
    }

    /**
     * Set new PID coefficients for kP, kI, kD, and kF.
     * @param pidCoef The class containing all of the coefficients.
     */
    public void setCoefficients(PIDCoefficients pidCoef){
        this.pidCoef = pidCoef;
    }

    /**
     * Returns the current coefficients on the PID.
     * @return Coefficients.
     */
    public PIDCoefficients getCoefficients() { return pidCoef; }

    /**
     * Returns the current error of the PID
     * @return Current Error
     */
    public double getError(){
        return currError;
    }

    /**
     * Sets the Set point for the PID to reach
     * @param setPoint
     * @param relative
     */
    public void setSetPoint(double setPoint, boolean relative){

        isRelative = relative;

        double input = (double) inputStream.getInput();

        prevTime = CknUtil.getCurrentTime();

        if(isRelative){

            this.setPoint = input + setPoint;

        } else {

            this.setPoint = setPoint;

        }
    }

    public boolean onTarget(){
        currError = setPoint - (double) inputStream.getInput();
        if(Math.abs(currError) > threshold){
            targetTime = CknUtil.getCurrentTime();
        }
        if(Math.abs(currError) < threshold){
            if(CknUtil.getCurrentTime() > targetTime + timeThreshold){
                return true;
            }
        }
        return false;
    }

    /**
     * Reset all the variables in the PID to 0.0
     */
    public void reset(){
        prevTime = 0.0;
        setPoint = 0.0;
        totalError = 0.0;
        currError = 0.0;
    }

    public void printPIDValues(){
        CknSmartDashboard.getInstance().setLine(8, "Target: " + setPoint);
        CknSmartDashboard.getInstance().setLine(9, "P: " + pTerm);
        CknSmartDashboard.getInstance().setLine(10, "I: " + iTerm);
        CknSmartDashboard.getInstance().setLine(11, "D: " + dTerm);
        CknSmartDashboard.getInstance().setLine(12, "P*: " + pTerm * pidCoef.kP);
        CknSmartDashboard.getInstance().setLine(13, "I*: " + iTerm * pidCoef.kI);
        CknSmartDashboard.getInstance().setLine(14, "D*: " + dTerm * pidCoef.kD);
        CknSmartDashboard.getInstance().setLine(15, "Total: " + ((pTerm * pidCoef.kP) + (iTerm * pidCoef.kI)
         + (dTerm * pidCoef.kD)));
        CknSmartDashboard.getInstance().setLine(16, "CurrError: " + currError);
        CknSmartDashboard.getInstance().setLine(17, "DeltaError: " + deltaError);
        CknSmartDashboard.getInstance().setLine(18, "PrevError: " + prevError);
    }

    public double getOutput(){

        // Variables used to calculated P, I, D

        double currTime = CknUtil.getCurrentTime();
        deltaTime = currTime - prevTime;

        double input = (double) inputStream.getInput();
        currError = setPoint - input;
        deltaError = currError - prevError;

        if(pidCoef.kI != 0.0) {
            double gain = (totalError + (currError * deltaTime)) * pidCoef.kI;
            if (gain >= maxOutput) {
                totalError = maxOutput / pidCoef.kI;
            } else if (gain < minOutput) {
                totalError = minOutput / pidCoef.kI;
            } else {
                totalError += currError * deltaTime;
            }
            totalError += currError * deltaTime;
        }

        // Calcluating P, I, D, terms
        pTerm = currError;
        iTerm = totalError;
        if(deltaTime <= 0.0){
            dTerm = 0.0;
        } else {
            dTerm = deltaError / deltaTime;
        }
        fTerm = setPoint;

        // Multiply terms by their constants and return.
        double output =  (pTerm * pidCoef.kP) + (iTerm * pidCoef.kI) + (dTerm * pidCoef.kD)
                + (fTerm * pidCoef.kF);

        if(output > maxOutput){
            output = maxOutput;
        }
        if(output < minOutput){
            output = minOutput;
        }

        // Save current values to be used in next loop.
        prevError = currError;
        prevTime = currTime;

        return output;

    }
}
