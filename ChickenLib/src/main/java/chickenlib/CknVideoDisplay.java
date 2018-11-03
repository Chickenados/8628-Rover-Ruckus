package chickenlib;

// Singleton Class
public class CknVideoDisplay {
    private static CknVideoDisplay instance;

    private CknVideoDisplay(){

    }

    public CknVideoDisplay getInstance(){
        if(instance == null) instance = new CknVideoDisplay();
        return instance;
    }





}
