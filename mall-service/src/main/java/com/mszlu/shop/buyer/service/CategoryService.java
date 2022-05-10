package com.mszlu.shop.buyer.service;

import com.mszlu.shop.buyer.vo.CategoryVO;

import java.util.List;

public interface CategoryService {
    List<String> getCategoryNameByIds(List<String> idList);

    List<CategoryVO> findCategoryTree(Long parentId);
}
