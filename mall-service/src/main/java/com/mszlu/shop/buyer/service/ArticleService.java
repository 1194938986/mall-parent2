package com.mszlu.shop.buyer.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mszlu.shop.buyer.article.ArticleCategoryVO;
import com.mszlu.shop.model.buyer.params.ArticleSearchParams;
import com.mszlu.shop.model.buyer.vo.article.ArticleVO;

import java.util.List;

public interface ArticleService {
    ArticleVO findArticleById(Long id);

    String findArticle();

    Page<ArticleVO> articlePage(ArticleSearchParams articleSearchParams);

    List<ArticleCategoryVO> findAllArticleCategory();
}
