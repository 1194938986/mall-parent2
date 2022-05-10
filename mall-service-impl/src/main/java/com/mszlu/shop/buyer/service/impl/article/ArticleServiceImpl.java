package com.mszlu.shop.buyer.service.impl.article;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mszlu.shop.buyer.article.ArticleCategoryVO;
import com.mszlu.shop.buyer.mapper.ArticleCategoryMapper;
import com.mszlu.shop.buyer.mapper.ArticleMapper;
import com.mszlu.shop.buyer.mapper.TestMapper;
import com.mszlu.shop.buyer.pojo.Test;
import com.mszlu.shop.buyer.service.ArticleService;
import com.mszlu.shop.model.buyer.params.ArticleSearchParams;
import com.mszlu.shop.model.buyer.pojo.Article;
import com.mszlu.shop.model.buyer.vo.article.ArticleVO;
import com.mszlu.shop.pojo.ArticleCategory;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

//加dubbo注解
//发布当前的service服务到nacos上
//ip：port/接口名称/方法名称
//version便于 接口有不同的实现，或者版本升级之用
//interfaceClass 不加这个事务 无法使用
@DubboService(version = "1.0.0",interfaceClass = ArticleService.class)
public class ArticleServiceImpl implements ArticleService {
    @Resource
    private TestMapper testMapper;

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private ArticleCategoryMapper articleCategoryMapper;

    @Override
    public ArticleVO findArticleById(Long id) {
        Article article = this.articleMapper.selectById(id);
        return copy(article);
    }

    @Override
    public String findArticle() {
        Test test = testMapper.selectById(1);
        return test.getUsername();
    }

    @Override
    public Page<ArticleVO> articlePage(ArticleSearchParams articleSearchParams) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        String categoryId = articleSearchParams.getCategoryId();
        if (StringUtils.isNotBlank(categoryId)) {
            queryWrapper.eq(Article::getCategoryId, categoryId);
        }
        String title = articleSearchParams.getTitle();
        if (StringUtils.isNotBlank(title)) {
            queryWrapper.eq(Article::getTitle, title);
        }
        String type = articleSearchParams.getType();
        if (StringUtils.isNotBlank(type)) {
            queryWrapper.eq(Article::getType, type);
        }
        queryWrapper.orderByDesc(Article::getSort);

        queryWrapper.select(Article::getCategoryId, Article::getId, Article::getOpenStatus, Article::getTitle, Article::getSort);
        Page<Article> articlePage = new Page<>(articleSearchParams.getPageNumber(),articleSearchParams.getPageSize());
        Page<Article> articlePage1 = articleMapper.selectPage(articlePage, queryWrapper);

        Page<ArticleVO> articleVOPage = new Page<>();
        BeanUtils.copyProperties(articlePage1,articleVOPage);
        List<Article> records = articlePage1.getRecords();
        articleVOPage.setRecords(copyList(records));
        return articleVOPage;
    }



    public ArticleVO copy(Article article){
        if (article == null){
            return null;
        }
        ArticleVO articleVO = new ArticleVO();

        BeanUtils.copyProperties(article,articleVO);
        articleVO.setId(article.getId().toString());
        return articleVO;
    }
    public  List<ArticleVO> copyList(List<Article> articleList){
        List<ArticleVO> articleVOList = new ArrayList<>();

        for (Article article : articleList) {
            articleVOList.add(copy(article));
        }
        return articleVOList;
    }

    @Override
    public List<ArticleCategoryVO> findAllArticleCategory() {
        LambdaQueryWrapper<ArticleCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleCategory::getDeleteFlag,false);
        List<ArticleCategory> articleCategories = articleCategoryMapper.selectList(queryWrapper);
        List<ArticleCategoryVO> articleCategoryVOs = copyCategoryList(articleCategories);
        List<ArticleCategoryVO> articleCategoryVOList = new ArrayList<>();
        for (ArticleCategoryVO articleCategoryVO : articleCategoryVOs) {
            if (articleCategoryVO.getLevel() == 0){
                addCategoryChild(articleCategoryVO,articleCategoryVOs);
                articleCategoryVOList.add(articleCategoryVO);
            }
        }
        return articleCategoryVOList;
    }

    private void addCategoryChild(ArticleCategoryVO articleCategoryVO, List<ArticleCategoryVO> articleCategoryVOs) {
        List<ArticleCategoryVO> articleCategoryVOList = new ArrayList<>();
        for (ArticleCategoryVO categoryVO : articleCategoryVOs) {
            if (categoryVO.getParentId().equals(articleCategoryVO.getId())){
                addCategoryChild(categoryVO,articleCategoryVOs);
                articleCategoryVOList.add(categoryVO);
            }
        }
        articleCategoryVO.setChildren(articleCategoryVOList);
    }

    public ArticleCategoryVO copyCategory(ArticleCategory article){
        if (article == null){
            return null;
        }
        ArticleCategoryVO articleVO = new ArticleCategoryVO();

        BeanUtils.copyProperties(article,articleVO);
        articleVO.setId(article.getId().toString());
        articleVO.setParentId(article.getParentId().toString());
        return articleVO;
    }

    public  List<ArticleCategoryVO> copyCategoryList(List<ArticleCategory> articleList){
        List<ArticleCategoryVO> articleVOList = new ArrayList<>();

        for (ArticleCategory article : articleList) {
            articleVOList.add(copyCategory(article));
        }
        return articleVOList;
    }
}
