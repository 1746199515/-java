package com.atguigu.liteims.mapper;

import com.atguigu.liteims.entity.SaleOrderItem;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SaleOrderItemMapper extends BaseMapper<SaleOrderItem> {

    List<SaleOrderItem> getSaleOrderItemByOrderId(Integer orderId);
}
