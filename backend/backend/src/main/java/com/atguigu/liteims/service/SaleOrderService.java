package com.atguigu.liteims.service;

import com.atguigu.liteims.dto.OrderDTO;
import com.atguigu.liteims.entity.SaleOrder;
import com.atguigu.liteims.vo.SaleOrderVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SaleOrderService extends IService<SaleOrder> {

    IPage<SaleOrderVO> findPage(IPage page, String orderNo);

    void saveOrder(OrderDTO orderDTO);

    void exportOrderList();
}
