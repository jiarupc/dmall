package com.dmall.service;

import com.dmall.common.ServerResponse;
import com.dmall.pojo.Product;
import com.dmall.vo.ProductDetailVo;
import com.dmall.vo.ProductListVo;
import com.github.pagehelper.PageInfo;

public interface IProductService {

    ServerResponse<String> addOrUpdateProduct(Product product);

    ServerResponse<String> setSaleStatus(Integer productId, Integer status);

    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

    ServerResponse<PageInfo> getProductList(int pageNum, int pageSize);

    ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize);

    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);
}
