package com.dmall.dao;

import com.dmall.pojo.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {
    Order selectByUserIdAndOrderNo(@Param("userId") Integer userId, @Param("orderNo") Long orderNo);

    Order selectByOrderNo(Long orderNo);

    int updateByPrimaryKeySelective(Order order);

    int insert(Order order);

    List<Order> selectByUserId(Integer userId);

    List<Order> selectAllOrder();
}
