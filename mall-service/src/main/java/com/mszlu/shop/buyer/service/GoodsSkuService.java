package com.mszlu.shop.buyer.service;

import com.mszlu.shop.common.vo.Result;
import com.mszlu.shop.model.buyer.vo.goods.GoodsDetailVo;

/**
 * @author ：XueQi
 * @description：TODO
 * @date ：2022/5/10 1:41
 */
public interface GoodsSkuService {
    Result<GoodsDetailVo> getGoodsSkuDetail(String goodsId, String skuId);

    void importES();
}
