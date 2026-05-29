package com.atguigu.liteims.service;

import com.atguigu.liteims.entity.Product;
import com.atguigu.liteims.vo.ProductVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

public interface ProductService extends IService<Product> {

    /**
     * 分页查询
     * @param page 当前页
     * @param size 每页条数
     * @param name 商品名称
     * @param categoryId 分类主键
     * @return 商品分页列表数据
     */
    IPage<ProductVO> findPage(Long page, Long size, String name, Long categoryId);
}
