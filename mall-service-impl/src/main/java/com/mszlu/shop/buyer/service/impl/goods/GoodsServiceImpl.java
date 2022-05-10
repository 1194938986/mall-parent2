package com.mszlu.shop.buyer.service.impl.goods;

import com.mszlu.shop.buyer.service.GoodsService;
import com.mszlu.shop.model.buyer.params.EsGoodsSearchParam;
import com.mszlu.shop.model.buyer.params.PageParams;
import com.mszlu.shop.model.buyer.pojo.es.EsGoodsIndex;
import com.mszlu.shop.model.buyer.vo.goods.GoodsPageVo;
import org.apache.dubbo.config.annotation.DubboService;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.List;
import java.util.stream.Collectors;

@DubboService(version = "1.0.0",interfaceClass = GoodsService.class)
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private ElasticsearchRestTemplate elasticsearchTemplate;

    @Override
    public GoodsPageVo searchGoods(EsGoodsSearchParam goodsSearchParams, PageParams pageParams) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        MatchQueryBuilder matchQueryBuilder =  QueryBuilders.matchQuery("goodsName",goodsSearchParams.getKeyword());
        MatchQueryBuilder matchQueryBuilder1 =  QueryBuilders.matchQuery("sellingPoint",goodsSearchParams.getKeyword());
        boolQueryBuilder.must(matchQueryBuilder);
        boolQueryBuilder.should(matchQueryBuilder1);
        PageRequest pageRequest = PageRequest.of(pageParams.getPageNumber() -1, pageParams.getPageSize());
        NativeSearchQuery query =
                new NativeSearchQueryBuilder()
                        .withQuery(boolQueryBuilder)
                        .withPageable(pageRequest)
                        .build();
        SearchHits<EsGoodsIndex> search = elasticsearchTemplate.search(query, EsGoodsIndex.class);
        SearchPage<EsGoodsIndex> searchHits = SearchHitSupport.searchPageFor(search, pageRequest);

        GoodsPageVo goodsPageVo = new GoodsPageVo();
        goodsPageVo.setTotalElements(searchHits.getTotalElements());
        List<EsGoodsIndex> collect = searchHits.getContent().stream().map(SearchHit::getContent).collect(Collectors.toList());
        goodsPageVo.setContent(collect);
        return goodsPageVo;
    }

}
