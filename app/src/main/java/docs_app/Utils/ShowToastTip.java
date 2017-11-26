package docs_app.Utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by asus-wh on 2017/10/12.
 */

public class ShowToastTip {
    public static  void showTips(Context context,String tip){
        Toast.makeText(context,tip, Toast.LENGTH_SHORT).show();
    }
}
