package com.dmall.controller.backend;

import com.dmall.common.Const;
import com.dmall.common.ResponseCode;
import com.dmall.common.ServerResponse;
import com.dmall.pojo.Product;
import com.dmall.pojo.User;
import com.dmall.service.IFileService;
import com.dmall.service.IProductService;
import com.dmall.service.IUserService;
import com.dmall.utils.PropertiesUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/manager/product")
public class ProductManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    @PostMapping("/add")
    public ServerResponse addProduct(Product product, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse
                    .createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请登陆");
        }
        //        校验管理员权限
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.addOrUpdateProduct(product);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @PutMapping("/sale/status")
    public ServerResponse setSaleStatus(Integer productId, Integer status, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse
                    .createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请登陆");
        }
        //        校验管理员权限
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.setSaleStatus(productId, status);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @GetMapping("/detail/{productId}")
    public ServerResponse getDetail(@PathVariable("productId") Integer productId, HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse
                    .createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请登陆");
        }
        //        校验管理员权限
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.manageProductDetail(productId);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @GetMapping("/list")
    public ServerResponse getList(@RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
                                  @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                  HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse
                    .createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请登陆");
        }
        //        校验管理员权限
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.getProductList(pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @GetMapping("/search")
    public ServerResponse searchProduct(String productName,
                                        Integer productId,
                                        @RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
                                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                        HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse
                    .createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请登陆");
        }
        //        校验管理员权限
        if (iUserService.checkAdminRole(user).isSuccess()) {
            return iProductService.searchProduct(productName, productId, pageNum, pageSize);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    @PostMapping("/upload")
    public ServerResponse upload(@RequestParam(value = "upload_file") MultipartFile file,
                                 HttpSession session,
                                 HttpServletRequest request) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse
                    .createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "请登陆");
        }
        //        校验管理员权限
        if (iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

            Map fileMap = Maps.newHashMap();
            fileMap.put("uri", targetFileName);
            fileMap.put("url", url);
            return ServerResponse.createBySuccess(fileMap);
        } else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }


    @PostMapping("/text/img/upload")
    public Map upload(@RequestParam(value = "upload_file") MultipartFile file,
                                 HttpSession session,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        Map resultMap = Maps.newHashMap();
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            resultMap.put("success", false);
            resultMap.put("msg", "请登陆管理员");
            return resultMap;
        }
        //        校验管理员权限
//        simditor 返回值要求：
//        {
//            "success": true/false,
//                "msg": "error message", # optional
//            "file_path": "[real file path]"
//        }
        if (iUserService.checkAdminRole(user).isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
//            失败返回
            if (StringUtils.isBlank(targetFileName)) {
                resultMap.put("success", false);
                resultMap.put("msg", "上传失败");
                return resultMap;
            }

//            拼接图片 url
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix" + targetFileName);

            resultMap.put("success", true);
            resultMap.put("msg", "上传成功");
            resultMap.put("file_path", url);
            response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
            return resultMap;
        } else {
            resultMap.put("success", false);
            resultMap.put("msg", "无权限操作");
            return resultMap;
        }
    }
}
