package docs_app.Utils;

import android.text.TextUtils;
import android.widget.TextView;

/**
 * Created by asus-wh on 2017/10/12.
 */

public class CheckInput {
    public static boolean check_textview(TextView textView){
        if(TextUtils.isEmpty(textView.getText().toString())){
            textView.setError("必填项");
            textView.requestFocus();
            return false;
        }
        if(!TextUtils.isEmpty(textView.getText().toString())&&textView.getText().toString().length()<6){
            textView.setError("太短了,至少6位");
            textView.requestFocus();
            return false;
        }
        return true;
    }
    //邮箱验证
    public static boolean isEmail(TextView textView) {
        String inputEmail=textView.getText().toString();
        String strPattern ="^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)$";
        if (TextUtils.isEmpty(inputEmail)) {
            textView.setError("必填项");
            textView.requestFocus();
            return false;
        }
        if(!TextUtils.isEmpty(inputEmail)&&!inputEmail.matches(strPattern)){
            textView.setError("邮箱格式不正确");
            textView.requestFocus();
            return false;
        }
        return true;
    }
    public static boolean check_password(TextView textView){
        String regex = "^[a-zA-Z0-9]+$";
        if(TextUtils.isEmpty(textView.getText().toString())){
            textView.setError("必填项");
            textView.requestFocus();
            return false;
        }
        if(!TextUtils.isEmpty(textView.getText().toString())&&textView.getText().toString().length()<6){
            textView.setError("太短了,至少6位");
            textView.requestFocus();
            return false;
        }
        if(!textView.getText().toString().matches(regex)){
            textView.setError("只能含数字和字母");
            textView.requestFocus();
            return false;

        }
        return true;
    }

}
