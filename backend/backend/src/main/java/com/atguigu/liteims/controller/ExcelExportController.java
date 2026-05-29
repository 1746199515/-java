package com.atguigu.liteims.controller;

import com.atguigu.liteims.service.SaleOrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "导出报表")
@RestController
@RequestMapping("/api/excel")
public class ExcelExportController {

    @Autowired
    SaleOrderService saleOrderService;

    @GetMapping("/orders/export")
    public void exportOrderList(){
        saleOrderService.exportOrderList();
    }
}
