package chickenlib;

import chickenlib.inputstreams.CknInputStream;
import chickenlib.display.CknSmartDashboard;
import chickenlib.util.CknUtil;
import chickenlib.util.CknWraparound;

public class CknPIDController {

    private double pTerm;
    private double iTerm;
    private double dTerm;
    private double fTerm;

    private double deltaTime;
    private double deltaError;

    public static class Parameters {
        public double minOutput = -1.0;
        public double maxOutput = 1.0;
        public double minTarget = 0.0;
        public double maxTarget = 0.0;
        public boolean useWraparound = false;
        public double minDeadband = -1.0;
        public double maxDeadband = 1.0;
        public boolean allowOscillation = false;
        public double threshold = 1.0;
        public double settlingTimeThreshold = 1.0;
    }

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
    Parameters params;

    private double threshold;
    double currError = 0.0;
    private double setPoint;

    double prevTime = 0.0;
    double prevError;
    double totalError = 0.0;

    boolean isRelative;

    double settlingTimeThreshold;
    boolean allowOscillation;
    double settleTime;
    double targetTime;
    double minTarget, maxTarget;
    boolean useWraparound;

    private double minOutput, maxOutput;

    public CknPIDController(PIDCoefficients pidCoef, CknInputStream inputStream, double threshold){
        this(pidCoef, inputStream, threshold, 0);
    }

    public CknPIDController(PIDCoefficients pidCoef, CknInputStream inputStream, double threshold, double timeThreshold){
        this.pidCoef = pidCoef;
        this.inputStream = inputStream;
        this.threshold = threshold;
        this.settlingTimeThreshold = timeThreshold;
    }

    public CknPIDController(PIDCoefficients pidCoef, CknInputStream inputStream, Parameters params){
        this.pidCoef = pidCoef;
        this.inputStream = inputStream;
        setParameters(params);
    }

    public void setParameters(Parameters params){
        this.threshold = params.threshold;
        this.minOutput = params.minOutput;
        this.maxOutput = params.maxOutput;
        this.settlingTimeThreshold = params.settlingTimeThreshold;
        this.allowOscillation = params.allowOscillation;
        this.minTarget = params.minTarget;
        this.maxTarget = params.maxTarget;
        this.useWraparound = params.useWraparound;
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

            if(useWraparound) {
                this.setPoint = CknWraparound.getTarget(minTarget, maxTarget, input, setPoint);
            } else {
                this.setPoint = setPoint;
            }
            currError = this.setPoint - input;

        }
    }

    /**
     * Checks if the PID is currently on target.
     * @return true if on target.
     */
    public boolean onTarget(){
        boolean onTarget = false;

        currError = setPoint - (double) inputStream.getInput();


        // We can allow the PID to oscillate and only return true on target if it has
        // been on target for a set time.
        if(allowOscillation){
            if(Math.abs(currError) > threshold){
                settleTime = CknUtil.getCurrentTime();
            } else if(CknUtil.getCurrentTime() - settleTime > settlingTimeThreshold){
                onTarget = true;
            }
        } else {
            if(Math.abs(currError) < threshold){
                onTarget = true;
            }
        }

        return onTarget;
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

        prevError = currError;
        double currTime = CknUtil.getCurrentTime();
        deltaTime = currTime - prevTime;
        prevTime = currTime;
        double input = (double) inputStream.getInput();
        currError = setPoint - input;


        if(pidCoef.kI != 0.0) {
            double gain = (totalError + (currError * deltaTime)) * pidCoef.kI;
            if (gain >= maxOutput) {
                totalError = maxOutput / pidCoef.kI;
            } else if (gain < minOutput) {
                totalError = minOutput / pidCoef.kI;
            } else {
                totalError += currError * deltaTime;
            }
        }

        // Calcluating P, I, D, terms
        pTerm = currError;
        iTerm = totalError;
        if(deltaTime <= 0.0){
            dTerm = 0.0;
        } else {
            dTerm = (currError - prevError) / deltaTime;
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

        return output;

    }
}
