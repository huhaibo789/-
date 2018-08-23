package util;

import com.baidu.location.BDLocation;

/**
 * 得到地图信息回调接口
 * Created by z2wenfa on 2015/11/24.
 */
public interface ReceiveBDLocationListener {
    /**
     * 获得位置信息成功回调
     *
     * @param locationStr 当前位置
     * @param location    当前
     * @return
     */
    BDLocation onReceiveBDLocationSuccess(String locationStr, BDLocation location);


    /**
     * 获得位置信息失败回调
     *
     * @param errorMsg
     * @param errorcode
     * @return
     */
    BDLocation onReceiveBDLocationFailed(String errorMsg, int errorcode);
}
