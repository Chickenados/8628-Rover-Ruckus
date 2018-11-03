package chickenlib;

import android.content.Context;
import android.view.View;

public interface CknViewDisplay {
    void setCurrentView(Context context, View view);
    void removeCurrentView(Context context);

}
