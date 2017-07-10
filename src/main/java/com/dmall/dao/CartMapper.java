package com.dmall.dao;

import com.dmall.pojo.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartMapper {
    int updateByPrimaryKeySelective(Cart record);

    List<Cart> selectCartByUserId(Integer userId);

    int insert(Cart record);

    Cart selectCartByUserIdProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    int selectCartProductCheckedStatusByUserId(Integer userId);

    int deleteByUserIdProductIds(@Param("userId") Integer userId, @Param("productIds") List<String> productIds);

    int checkedOrUnCheckedProduct(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("checked") Integer checked);

    int selectCartProductsCount(Integer userId);

    List<Cart> selectCheckedCartByUserId(Integer userId);

    int deleteByPrimaryKey(Integer id);
}
