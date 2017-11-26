package docs_app.model;

import android.content.Context;
import android.widget.Toast;



import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import docs_app.bean.User;

/**
 * Created by asus-wh on 2017/10/12.
 */

public class UserModel{
    /**
     * 查询所有User
     */
    /*public List<User> queryAll(){
        final List<User> users=new ArrayList<User>();
        BmobQuery<User> query=new BmobQuery<User>();
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e==null){
                    int n=list.size();
                    System.out.println(n);
                    for (int i = 0; i < n; i++) {
                        users.add(list.get(i));
                    }
                }
                else{
                    Log.d("ok",e.getMessage());
                }
            }
        });
        return  users;
    }*/
    /**
     * 注册
     */
    public void regist(final Context context, User user){
        user.signUp(new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if(e==null){
                    Toast.makeText(context, "注册成功", Toast.LENGTH_SHORT).show();

                }
                else{
                   Toast.makeText(context, "注册失败"+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //查找User表所有信息-查
}
