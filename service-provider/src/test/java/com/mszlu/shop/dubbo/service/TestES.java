package com.mszlu.shop.dubbo.service;

import com.mszlu.shop.buyer.service.GoodsService;
import com.mszlu.shop.buyer.service.GoodsSkuService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestES {

    @Autowired
    private GoodsSkuService goodsService;

    @Test
    public void testImportGoods(){
        goodsService.importES();
    }
}
