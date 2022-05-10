package com.mszlu.shop.buyer.service;

import com.mszlu.shop.common.vo.Result;

import java.util.Map;

/**
 * @author ：XueQi
 * @description：TODO
 * @date ：2022/5/10 1:41
 */
public interface GoodsSkuService {
    Result<Map<String, Object>> getGoodsSkuDetail(String goodsId, String skuId);

    void importES();
}
