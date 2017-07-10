package com.dmall.dao;

import com.dmall.pojo.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {

    int insert(Category record);

    int updateByPrimaryKeySelective(Category record);

    List<Category> selectCategoryChildrenByParentId(Integer parentId);

    Category selectByPrimaryKey(Integer id);
}
