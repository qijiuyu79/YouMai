package com.youmai.project.bean;

/**
 * 交易操作的回调
 */
public interface TradingPlay {

    /**
     * 交易完成
     * @param orderId
     */
    public void complete(String orderId);

    /**
     * 交易取消
     * @param orderId
     */
    public void cancle(String orderId);

    /**
     * 联系卖家
     * @param phone
     */
    public void playPhon(String phone);
}
