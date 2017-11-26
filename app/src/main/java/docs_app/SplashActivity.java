package docs_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.lovelyhq.android.lovelydocs.R;

public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH=2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent=new Intent(SplashActivity.this,LoginActivity.class);

                //显示主窗口
                SplashActivity.this.startActivity(mainIntent);
                //关闭当前窗口
                SplashActivity.this.finish();
            }
        },SPLASH_DISPLAY_LENGTH);
        //setContentView(R.layout.activity_splash);

    }
}
