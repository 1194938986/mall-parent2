package com.mszlu.shop.model.buyer.pojo.es;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;

/**
 * @author ：XueQi
 * @description：TODO
 * @date ：2022/4/23 1:43
 */

@Data
@NoArgsConstructor
public class EsGoodsAttribute implements Serializable {

    /*
    属性参数：0->规格；1->参数
     */
    @Field(type = FieldType.Integer)
    private Integer type;

    /*
    属性名称
     */
    private String nameId;

    /*
    属性名称
     */
    @Field(type = FieldType.Text)
    private String name;

    /*
    属性值
     */
    @Field(type = FieldType.Text)
    private String valueId;

    /*
    属性值
     */
    @Field(type = FieldType.Text)
    private String value;


}
