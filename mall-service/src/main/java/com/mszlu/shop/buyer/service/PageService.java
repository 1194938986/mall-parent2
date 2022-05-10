package com.mszlu.shop.buyer.service;

import com.mszlu.shop.common.vo.Result;

public interface PageService {
    Result findPageTemplate(Integer clientType, int pageType);
}
