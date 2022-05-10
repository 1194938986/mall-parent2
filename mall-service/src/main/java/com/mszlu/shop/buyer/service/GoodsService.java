package com.mszlu.shop.buyer.service;

import com.mszlu.shop.model.buyer.params.EsGoodsSearchParam;
import com.mszlu.shop.model.buyer.params.PageParams;
import com.mszlu.shop.model.buyer.vo.goods.GoodsPageVo;

public interface GoodsService {
    GoodsPageVo searchGoods(EsGoodsSearchParam goodsSearchParams, PageParams pageParams);
}
