package com.dmall.dao;

import com.dmall.pojo.PayInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PayInfoMapper {
    int insert(PayInfo payInfo);
}
