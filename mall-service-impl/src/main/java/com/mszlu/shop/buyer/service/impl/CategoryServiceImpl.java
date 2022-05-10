package com.mszlu.shop.buyer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mszlu.shop.buyer.mapper.CategoryMapper;
import com.mszlu.shop.buyer.pojo.Category;
import com.mszlu.shop.buyer.service.CategoryService;
import com.mszlu.shop.buyer.vo.CategoryVO;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//事务能生效
@DubboService(version = "1.0.0",interfaceClass = CategoryService.class)
//@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    public CategoryVO copy(Category category){
        CategoryVO target = new CategoryVO();
        BeanUtils.copyProperties(category, target);
        return target;
    }
    public List<CategoryVO> copyList(List<Category> categoryList){
        List<CategoryVO> categoryVOList = new ArrayList<>();
        for (Category category : categoryList) {
            categoryVOList.add(copy(category));
        }
        return categoryVOList;
    }

    private List<CategoryVO> categoryTree(Long parentId){
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getStatus,0);
        List<Category> categories = categoryMapper.selectList(queryWrapper);
        List<CategoryVO> categoryVOS = copyList(categories);
        List<CategoryVO> categoryVOList = new ArrayList<>();
        for (CategoryVO categoryVO : categoryVOS) {
            if (categoryVO.getParentId().equals(parentId)){
                categoryVOList.add(categoryVO);
                addAllChildren(categoryVO,categoryVOS);
            }
        }
        return categoryVOList;
    }

    @Override
    public List<String> getCategoryNameByIds(List<String> idList) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Category::getId,idList);
        List<Category> categories = categoryMapper.selectList(queryWrapper);
        List<String> strings = categories.stream().map(Category::getName).collect(Collectors.toList());
        return strings;
    }

    private void addAllChildren(CategoryVO categoryVO, List<CategoryVO> categoryVOS) {
        List<CategoryVO> categoryVOList = new ArrayList<>();
        for (CategoryVO vo : categoryVOS) {
            if (vo.getParentId().equals(categoryVO.getId())){
                categoryVOList.add(vo);
                addAllChildren(vo,categoryVOS);
            }
        }
        categoryVO.setChildren(categoryVOList);
    }

    @Override
    public List<CategoryVO> findCategoryTree(Long parentId) {
        /**
         * 1. 根据parentId 获取对应的分类列表
         * 2. 获取到的分类列表 只有一级
         * 3. 根据刚才的sql执行 发现了问题： 循环 递归的获取 所有分类的 子分类列表
         * 4. 如果这么做的话，数据库连接就会多次 连接 性能就会降低
         * 5. 不能这么写，想了一个方法 把所有的分类查出来，一次查询 效率高
         * 6. 代码去组合 层级关系，代码的允许速度是非常快的，因为代码运行在内存当中
         */

        return categoryTree(parentId);
    }
}
