package com.mszlu.shop.buyer.pages.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DiscountAdvertOption implements Serializable {

    private ImageData bgImg;

    private List<ImageData> classification;

    private List<ImageData> brandList;

}
