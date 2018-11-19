package chickenlib.sensor;

import chickenlib.util.CknData;

public abstract class CknSensor<D> {

    private int numAxes;

    public CknSensor(int numAxes){
        this.numAxes = numAxes;
    }

    public int getNumAxes(){
        return numAxes;
    }

    /**
     * This method will be inherited to return raw sensor data
     * @param axis The axis of the sensor to read
     * @param dataType The data type to read
     * @return CknData object
     */
    public abstract CknData<?> getRawData(int axis, D dataType);


    public CknData<Double> getData(int axis, D dataType){
        // Read the raw data to be processed.
        CknData<Double> data = (CknData<Double>) getRawData(axis, dataType);

        //TODO: Data processing.

        return data;
    }
}
