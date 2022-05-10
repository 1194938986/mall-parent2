package com.mszlu.shop.buyer.controller.goods;

import com.mszlu.shop.buyer.service.goods.GoodsBuyerService;
import com.mszlu.shop.buyer.service.goods.GoodsSearchService;
import com.mszlu.shop.common.vo.Result;
import com.mszlu.shop.model.buyer.params.EsGoodsSearchParam;
import com.mszlu.shop.model.buyer.params.PageParams;
import com.mszlu.shop.model.buyer.vo.goods.GoodsPageVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Api(tags = "买家端,商品接口")
@RestController
@RequestMapping("goods")
public class GoodsBuyerController {

    @Autowired
    private GoodsSearchService goodsSearchService;

    @Autowired
    private GoodsBuyerService goodsBuyerService;

    @ApiOperation(value = "获取搜索热词")
    @GetMapping("/hot-words")
    public Result<List<String>> getGoodsHotWords(Integer start, Integer end) {
        List<String> hotWords = goodsSearchService.getHotWords(start, end);
        return Result.success(hotWords);
    }

    @ApiOperation(value = "从ES中获取商品信息")
    @GetMapping("/es")
    public Result<GoodsPageVo> getGoodsByPageFromEs(EsGoodsSearchParam goodsSearchParams, PageParams pageParams) {
        GoodsPageVo goodsPageVo = goodsSearchService.searchGoods(goodsSearchParams, pageParams);
        return Result.success(goodsPageVo);
    }

    @GetMapping(value = "/sku/{goodsId}/{skuId}")
    public Result<Map<String, Object>> getSku(@PathVariable("goodsId") String goodsId,
                                              @PathVariable("skuId") String skuId) {
        // 读取选中的列表
        return goodsBuyerService.getGoodsSkuDetail(goodsId, skuId);

    }
}
