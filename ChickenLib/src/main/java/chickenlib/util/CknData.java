package chickenlib.util;

public class CknData<T> {

    public T value;
    public double timestamp;

    public CknData(T value, double timestamp){
        this.value = value;
        this.timestamp = timestamp;
    }

}
