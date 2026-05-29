package com.atguigu.liteims.controller;

import com.atguigu.liteims.common.Result;
import com.atguigu.liteims.dto.OrderDTO;
import com.atguigu.liteims.entity.SysUser;
import com.atguigu.liteims.service.SaleOrderService;
import com.atguigu.liteims.vo.SaleOrderVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "订单模块")
@RestController
@RequestMapping("/api/orders")
public class SaleOrderController {

    @Autowired
    SaleOrderService saleOrderService;

    @Operation(summary = "分页查询")
    @GetMapping
    public Result<IPage<SaleOrderVO>> findPage(Long page, Long size, String orderNo) {
        return Result.success(saleOrderService.findPage(new Page(page, size), orderNo));
    }

    @Operation(summary = "保存")
    @PostMapping
    public Result save(@RequestBody OrderDTO orderDTO, HttpSession session) {
        SysUser sysUser  = (SysUser) session.getAttribute("user");
        orderDTO.setUserId(sysUser.getId());
        saleOrderService.saveOrder(orderDTO);
        return Result.success(null);
    }




}
