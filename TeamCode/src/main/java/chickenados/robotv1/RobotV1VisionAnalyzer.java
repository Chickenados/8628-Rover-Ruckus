package chickenados.robotv1;

import org.firstinspires.ftc.robotcore.external.tfod.Recognition;

import java.util.List;

public class RobotV1VisionAnalyzer {

    private String Gold_Label;

    enum GoldState{
        UNKNOWN,
        LEFT,
        CENTER,
        RIGHT;
    }

    public RobotV1VisionAnalyzer(String gold_Label){
        this.Gold_Label = gold_Label;
    }

    public GoldState analyzeTFOD(List<Recognition> objects){
        if(objects != null){

            if(objects.size() == 3){
                //Gather the location of each mineral.
                int goldX = -1;
                int silver1X = -1;
                int silver2X = -1;

                //Iterate through each instance in the list and gather it's location
                for(Recognition rec : objects){
                    if(rec.getLabel().equals(Gold_Label)){
                        goldX = (int) rec.getLeft();
                    } else if(silver1X == -1){
                        silver1X = (int) rec.getLeft();
                    } else {
                        silver2X = (int) rec.getLeft();
                    }

                    if(goldX < silver1X && goldX < silver2X){
                        return GoldState.LEFT;
                    } else if(goldX > silver1X && goldX > silver2X){
                        return GoldState.RIGHT;
                    } else {
                        return GoldState.CENTER;
                    }
                }

            }
        }
        return GoldState.UNKNOWN;
    }
}
