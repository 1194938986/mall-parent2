package com.mszlu.shop.buyer.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class NavBar implements Serializable {

    private String type;

    private List<NavBarData> list;

}
