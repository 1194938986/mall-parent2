package com.mszlu.shop.buyer.service.goods;

import com.mszlu.shop.buyer.service.GoodsSkuService;
import com.mszlu.shop.common.vo.Result;
import com.mszlu.shop.model.buyer.vo.goods.GoodsDetailVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

@Service
public class GoodsBuyerService {

    @DubboReference(version = "1.0.0")
    private GoodsSkuService goodsSkuService;

    public Result<GoodsDetailVo> getGoodsSkuDetail(String goodsId, String skuId) {
        return goodsSkuService.getGoodsSkuDetail(goodsId,skuId);
    }
}
