package com.mszlu.shop.buyer.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class Carousel implements Serializable {

    private String type;

    private String name;

    private String icon;

    private String showName;

    private String size;

    private CarouselOption options;

    private String key;

}
