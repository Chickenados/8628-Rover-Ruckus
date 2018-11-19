package chickenlib.inputstreams;

import chickenlib.location.CknLocation;
import chickenlib.location.CknLocationTracker;

public class CknLocationInputStream extends CknInputStream{

    public enum InputType{
        X_POSITION,
        Y_POSITION,
        Z_POSITION,
        HEADING;
    }

    CknLocationTracker locationTracker;
    InputType inputType;

    public CknLocationInputStream(CknLocationTracker locationTracker, InputType inputType){
        this.locationTracker = locationTracker;
        this.inputType = inputType;
    }

    @Override
    public Object getInput() {
        CknLocation location = locationTracker.getLocation();
        if(location != null) {
            switch (inputType) {
                case X_POSITION:
                    return location.x;
                case Y_POSITION:
                    return location.y;
                case Z_POSITION:
                    return location.z;
                case HEADING:
                    return location.heading;
            }
        }
        return 0.0;
    }
}
