package com.mszlu.shop.buyer.service.article;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mszlu.shop.buyer.article.ArticleCategoryVO;
import com.mszlu.shop.buyer.service.ArticleService;
import com.mszlu.shop.model.buyer.params.ArticleSearchParams;
import com.mszlu.shop.model.buyer.vo.article.ArticleVO;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BuyerArticleService {

    @DubboReference(version = "1.0.0")
    private ArticleService articleService;

    public Page<ArticleVO> articlePage(ArticleSearchParams articleSearchParams) {
        return articleService.articlePage(articleSearchParams);
    }

    public ArticleVO customGet(Long id) {
        return articleService.findArticleById(id);
    }

    public List<ArticleCategoryVO> allChildren() {
        return articleService.findAllArticleCategory();
    }
}
