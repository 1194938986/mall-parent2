package com.mszlu.shop.buyer.service.goods;

import com.mszlu.shop.buyer.service.GoodsSkuService;
import com.mszlu.shop.common.vo.Result;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GoodsBuyerService {

    @DubboReference(version = "1.0.0")
    private GoodsSkuService goodsSkuService;

    public Result<Map<String, Object>> getGoodsSkuDetail(String goodsId, String skuId) {
        return goodsSkuService.getGoodsSkuDetail(goodsId,skuId);
    }
}
