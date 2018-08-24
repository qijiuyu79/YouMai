package com.youmai.project.callback;

import com.youmai.project.bean.GoodsBean;

/**
 * 交易操作的回调
 */
public interface TradingPlay {

    /**
     * 交易完成
     * @param goodsBean
     */
    public void complete(GoodsBean goodsBean);

    /**
     * 交易取消
     * @param goodsBean
     */
    public void cancle(GoodsBean goodsBean);

    /**
     * 联系卖家
     * @param phone
     */
    public void playPhon(String phone);
}
