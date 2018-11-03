package chickenlib;

public class CknData<T> {

    public T data;
    public double timestamp;

    public CknData(T data, double timestamp){
        this.data = data;
        this.timestamp = timestamp;
    }

}
