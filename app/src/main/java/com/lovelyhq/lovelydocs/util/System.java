package com.lovelyhq.lovelydocs.util;

import cn.bmob.v3.BmobObject;

/**
 * Created by lrw on 2017/10/21.
 */

public class System extends BmobObject {
    private boolean isauto;
    private boolean iswlan;
    private boolean isnight;
    public  boolean getIsauto() {
        return isauto;
    }

    public void setIsauto(boolean isauto) {
        this.isauto = isauto;
    }

    public boolean getIswlan() {
        return iswlan;
    }

    public void setIswlan(boolean iswlan) {
        this.iswlan = iswlan;
    }

    public boolean getIsnight() {
        return isnight;
    }

    public void setIsnight(boolean isnight) {
        this.isnight = isnight;
    }


}
