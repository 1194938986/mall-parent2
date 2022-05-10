package com.mszlu.shop.buyer.service.impl.goods;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mszlu.shop.buyer.mapper.GoodsSkuMapper;
import com.mszlu.shop.buyer.service.CategoryService;
import com.mszlu.shop.buyer.service.GoodsService;
import com.mszlu.shop.buyer.service.GoodsSkuService;
import com.mszlu.shop.common.vo.Result;
import com.mszlu.shop.model.buyer.pojo.es.EsGoodsIndex;
import com.mszlu.shop.model.buyer.pojo.goods.GoodsSku;
import com.mszlu.shop.model.buyer.vo.goods.*;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ：XueQi
 * @description：TODO
 * @date ：2022/5/10 1:39
 */

@DubboService(version = "1.0.0",interfaceClass = GoodsSkuService.class)
public class GoodsSkuServiceImpl implements GoodsSkuService {
    @Resource
    private GoodsSkuMapper goodsSkuMapper;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;

    @Override
    public Result<GoodsDetailVo> getGoodsSkuDetail(String goodsId, String skuId) {
        /**
         * 目的：GoodsDetailVo
         * 1. 根据skuid 查询 goodsSku表 ，转换为GoodsSkuVO对象
         * 2. categoryName 表中有 分类对应id列表，根据分类的id列表 去分类表中查询
         * 3. specs sku数据中 json的spec字符串，处理对应的字符串即可
         * 4. 促销信息 暂时不理会
         */
        //如果skuid 为null 或者 查询不出来数据，怎么办？
        //还有商品id，可以根据商品id 查询sku数据
        if (goodsId == null){
            return Result.fail(-999,"参数错误");
        }
        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        GoodsSku goodsSku;
        if (skuId == null){
            //根据商品id查询
            LambdaQueryWrapper<GoodsSku> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(GoodsSku::getGoodsId,Long.parseLong(goodsId));
            List<GoodsSku> goodsSkus = goodsSkuMapper.selectList(queryWrapper);
            if (goodsSkus.size() <= 0){
                return Result.fail(-999,"参数错误");
            }
            goodsSku = goodsSkus.get(0);
        }else{
            goodsSku = goodsSkuMapper.selectById(Long.parseLong(skuId));
        }
        GoodsSkuVO goodsSkuVO = getGoodsSkuVO(goodsSku);
        goodsDetailVo.setData(goodsSkuVO);
        //分类列表
        String categoryPath = goodsSku.getCategoryPath();

        List<String> categoryNames = categoryService.getCategoryNameByIds(Arrays.asList(categoryPath.split(",")));
        goodsDetailVo.setCategoryName(categoryNames);

        //规则参数
        List<SpecValueVO> specList = goodsSkuVO.getSpecList();
        List<GoodsSkuSpecVO> goodsSkuSpecVOList = new ArrayList<>();
        GoodsSkuSpecVO goodsSkuSpecVO = new GoodsSkuSpecVO();
        goodsSkuSpecVO.setSkuId(goodsSku.getId().toString());
        goodsSkuSpecVO.setQuantity(goodsSku.getQuantity());
        goodsSkuSpecVO.setSpecValues(specList);
        goodsSkuSpecVOList.add(goodsSkuSpecVO);
        goodsDetailVo.setSpecs(goodsSkuSpecVOList);
        //促销信息
        goodsDetailVo.setPromotionMap(new HashMap<>());
        return Result.success(goodsDetailVo);
    }

    public GoodsSkuVO getGoodsSkuVO(GoodsSku goodsSku) {

        GoodsSkuVO goodsSkuVO = new GoodsSkuVO();
        BeanUtils.copyProperties(goodsSku, goodsSkuVO);
        //获取sku信息
        JSONObject jsonObject = JSON.parseObject(goodsSku.getSpecs());
        //用于接受sku信息
        List<SpecValueVO> specValueVOS = new ArrayList<>();
        //用于接受sku相册
        List<String> goodsGalleryList = new ArrayList<>();
        //循环提交的sku表单
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            SpecValueVO specValueVO = new SpecValueVO();
            if (entry.getKey().equals("images")) {
                specValueVO.setSpecName(entry.getKey());
                if (entry.getValue().toString().contains("url")) {
                    List<SpecImages> specImages = JSON.parseArray(entry.getValue().toString(), SpecImages.class);
                    specValueVO.setSpecImage(specImages);
                    goodsGalleryList = specImages.stream().map(SpecImages::getUrl).collect(Collectors.toList());
                }
            } else {
                specValueVO.setSpecName(entry.getKey());
                specValueVO.setSpecValue(entry.getValue().toString());
            }
            specValueVOS.add(specValueVO);
        }
        goodsSkuVO.setGoodsGalleryList(goodsGalleryList);
        goodsSkuVO.setSpecList(specValueVOS);
        return goodsSkuVO;
    }

    @Override
    public void importES() {
        //导入es数据的时候，导入sku的数据，一般情况sku的数据 会有商品的数据属性，同时还有其余的属性（比如规格等信息）
        //一个商品 会对应多个sku
        LambdaQueryWrapper<GoodsSku> queryWrapper1 = new LambdaQueryWrapper<>();
        List<GoodsSku> goodsSkusList = goodsSkuMapper.selectList(queryWrapper1);
        System.out.println("goodsSkusList.size:" + goodsSkusList.size());

        for (int i = 205; i < 220; i++) {
            GoodsSku goodsSku = goodsSkusList.get(i);
            EsGoodsIndex esGoodsIndex = new EsGoodsIndex();
            BeanUtils.copyProperties(goodsSku,esGoodsIndex);

            esGoodsIndex.setId(goodsSku.getId().toString());
            esGoodsIndex.setGoodsId(goodsSku.getGoodsId().toString());
            esGoodsIndex.setPrice(goodsSku.getPrice().doubleValue());
            BigDecimal promotionPrice = goodsSku.getPromotionPrice();
            if (promotionPrice != null) {
                esGoodsIndex.setPromotionPrice(promotionPrice.doubleValue());
            }
            elasticsearchTemplate.save(esGoodsIndex);
        }

//        for (GoodsSku goodsSku : goodsSkusList) {
//            EsGoodsIndex esGoodsIndex = new EsGoodsIndex();
//            BeanUtils.copyProperties(goodsSku,esGoodsIndex);
//
//            esGoodsIndex.setId(goodsSku.getId().toString());
//            esGoodsIndex.setGoodsId(goodsSku.getGoodsId().toString());
//            esGoodsIndex.setPrice(goodsSku.getPrice().doubleValue());
//            BigDecimal promotionPrice = goodsSku.getPromotionPrice();
//            if (promotionPrice != null) {
//                esGoodsIndex.setPromotionPrice(promotionPrice.doubleValue());
//            }
//            elasticsearchTemplate.save(esGoodsIndex);
//        }
    }


}
