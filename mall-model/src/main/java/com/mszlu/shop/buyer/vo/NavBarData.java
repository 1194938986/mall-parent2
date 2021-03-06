package com.mszlu.shop.buyer.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NavBarData implements Serializable {
    private String name;
    private String url;
}
