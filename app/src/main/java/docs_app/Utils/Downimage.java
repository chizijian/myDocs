package docs_app.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by asus-wh on 2017/10/21.
 */

public class Downimage extends AsyncTask {

    private ImageView imageView;

    public Downimage(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(Object... params) {
        String url = params[0].toString();
        Bitmap bitmap = null;
        try {
            //加载一个网络图片
            InputStream is = new URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    @Override
    protected void onPostExecute(Object o) {
        imageView.setImageBitmap((Bitmap) o);
    }


}