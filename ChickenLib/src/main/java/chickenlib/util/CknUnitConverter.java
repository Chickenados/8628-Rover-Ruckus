package chickenlib.util;

import java.util.HashMap;

public class CknUnitConverter {

    public enum Unit{
        MILLIMETERS,
        METERS,
        INCHES,
        FEET,
        YARDS;
    }

    static CknUnitConverter instance;
    static HashMap<Unit, Double> units;

    /**
     * Creates a list of all conversions relative to millimeters.
     */
    private CknUnitConverter(){
        units = new HashMap<Unit, Double>();
        units.put(Unit.MILLIMETERS, 1.0);
        units.put(Unit.METERS, 1000.0);
        units.put(Unit.INCHES, 25.4);
        units.put(Unit.FEET, 304.8);
        units.put(Unit.YARDS, 914.4);
    }

    public static CknUnitConverter getInstance(){
        if(instance != null) {
            instance = new CknUnitConverter();
        }
        return instance;
    }

    public double convertValue(Unit unitFrom, Unit unitTo, double value){
        return value * units.get(unitFrom) / units.get(unitTo);
    }

}
