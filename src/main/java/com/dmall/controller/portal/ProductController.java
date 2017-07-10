package com.dmall.controller.portal;

import com.dmall.common.ServerResponse;
import com.dmall.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private IProductService iProductService;

    @GetMapping("/detail/{productId}")
    public ServerResponse getDetail(@PathVariable("productId") Integer productId) {
        return iProductService.getProductDetail(productId);
    }
}
