package com.mszlu.shop.buyer.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Nav implements Serializable {

    private String title;

    private String desc;
}
