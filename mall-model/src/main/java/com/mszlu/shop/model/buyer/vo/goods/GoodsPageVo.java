package com.mszlu.shop.model.buyer.vo.goods;

import com.mszlu.shop.model.buyer.pojo.es.EsGoodsIndex;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GoodsPageVo implements Serializable {


    private Long totalElements;

    private List<EsGoodsIndex> content;
}
