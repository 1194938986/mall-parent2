package com.mszlu.shop.buyer.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageTemplateVo implements Serializable {

    private String type;

    private Object pageData;



}
