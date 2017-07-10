package com.dmall.service;

import com.dmall.common.ServerResponse;
import com.dmall.pojo.Shipping;
import com.github.pagehelper.PageInfo;

public interface IShippingService {

    ServerResponse addShipping(Integer userId, Shipping shipping);

    ServerResponse<String> deleteShipping(Integer userId, Integer shippingId);

    ServerResponse updateShipping(Integer userId, Shipping shipping);

    ServerResponse<Shipping> getShipping(Integer userId, Integer shippingId);

    ServerResponse<PageInfo> getShippingList(Integer userId, int pageNum, int pageSize);
}
