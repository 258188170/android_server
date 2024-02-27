package com.card.lp_server.card.device.call;


import com.card.lp_server.card.device.model.Pair;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author SEO-Dev
 *
 *         用户设备，各家实现的基类，子类必须支持无参数构造（建议不实现任何构造函数）
 */
public abstract class VendorDevice {

    protected ICreateDevice creator;

    /**
     * 底层IO设备
     */
    protected IIODevice dev;

    /**
     * 获取公开方法与操作说明的对应关系
     *
     * @return 方法与参数的对应关系
     */
    public Map<String, Method> getServiceMap() {
        Map<String, Method> map = new HashMap<String, Method>();

        return map;
    }

    /**
     * 设置创建设备的回调
     *
     * @param create 创建设备的回调
     */
    public void SetCreate(ICreateDevice create) {
        this.creator = create;
    }

    /**
     * 最后的一次错误
     */
    private Exception lastError;

    /**
     * 获取最后一次错误
     *
     * @return 最后一次错误
     */
    public Exception getLastError() {
        return lastError;
    }

    /**
     * 获取产口标识（建议以厂商+产品）进行命名，实现类固定返回一个字符串 <b>科泰进行整合时注意查重和记录</b>
     */
    public abstract String identity();

    /**
     * 连接设备并返回设备是否连接成功
     *
     * @return 设备是否连接成功
     *
     */
    public abstract Pair<Boolean,String> connect();

}
