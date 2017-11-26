package docs_app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.lovelyhq.android.lovelydocs.R;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import docs_app.Utils.CheckInput;
import docs_app.Utils.ShowToastTip;
import docs_app.bean.User;


public class ForgotPswActivity extends AppCompatActivity {
    private Button btn_forgot_confirm;
    private TextView forgot_emial;
    BmobQuery<User> query=new BmobQuery<User>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_psw);
        Bmob.initialize(this,"8afbcaae22fc8ee6739956da9cbc2d7a");
        forgot_emial=(TextView) findViewById(R.id.forgot_email);
        btn_forgot_confirm=(Button) findViewById(R.id.confirm_email);
        btn_forgot_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CheckInput.isEmail(forgot_emial)){
                    query.addWhereEqualTo("email",forgot_emial.getText().toString());
                    query.findObjects(new FindListener<User>() {
                        @Override
                        public void done(List<User> list, BmobException e) {
                            if(e==null) {
                                list.get(0).resetPasswordByEmail(forgot_emial.getText().toString(), new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        if(e==null){
                                            ShowToastTip.showTips(ForgotPswActivity.this,"重置请求成功,请到"+forgot_emial.getText().toString()+"邮箱进行重置操作");
                                        }
                                        else{
                                            ShowToastTip.showTips(ForgotPswActivity.this,"请求失败"+e.getMessage());
                                        }
                                    }
                                });
                            }
                            else{
                                ShowToastTip.showTips( ForgotPswActivity.this,"该邮箱还未注册");
                            }
                        }
                    });
                }
                /*BmobQuery<User> query = new BmobQuery<User>();
                query.findObjects(new FindListener<User>() {
                    @Override
                    public void done(List<User> list, BmobException e) {
                        if(e==null){
                            Toast.makeText(ForgotPswActivity.this, "得到的用户数是:"+list.size(), Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(ForgotPswActivity.this, "code"+e.getErrorCode() +" "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/

                /*TUser tuser=new TUser();
                tuser.setDemail("1290507445@qq.com");
                tuser.setDpassword("123");
                tuser.setDusername("ztt");*/
                /*User user = new User();
                user.setUsername("13121");
                user.setPassword("ztt2");
                user.signUp(new SaveListener<User>() {
                    @Override
                    public void done(User tuser, BmobException e) {
                        if(e==null) {
                            Toast.makeText(ForgotPswActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            ForgotPswActivity.this.finish();
                        }
                        else{
                            Toast.makeText(ForgotPswActivity.this, "注册失败"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                });*/
               /* UserModel userModel=new UserModel();
                if(userModel.queryAll().size()>0){
                    Toast.makeText(ForgotPswActivity.this, "查询成功", Toast.LENGTH_SHORT).show();
                    //Log.d("user:",userModel.queryAll().get(0).getUsername());
                }
                else{
                    Toast.makeText(ForgotPswActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
                }*/

            }
        });
        /*getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);*/
    }
  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                Log.d("da","da");
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/

}
