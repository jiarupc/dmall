package com.dmall.dao;

import com.dmall.pojo.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {
    int insert(Product record);

    int updateByPrimaryKeySelective(Product record);

    Product selectByPrimaryKey(Integer productId);

    List<Product> selectList();

    List<Product> selectByNameAndProductId(@Param("productName") String productName, @Param("productId") Integer productId);
}
