package com.atguigu.liteims.task;

import com.atguigu.liteims.entity.Product;
import com.atguigu.liteims.mapper.ProductMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class StockTask {

    @Autowired
    ProductMapper productMapper;

    //@Scheduled(cron = "0/20 * * * * ?") //时间表达式   7个组成部分：   秒  分  时   日  月  周  年
    @Scheduled(cron = "0 0 8 * * ?")
    public void stockCheck(){
        log.info("开始执行库存检查任务...");

        // 查询库存 < 10 的商品
        List<Product> lowStockProducts = productMapper.selectList(
                new QueryWrapper<Product>().lt("stock", 10)
        );

        if (lowStockProducts.isEmpty()) {
            log.info("库存充足，无预警信息。");
        } else {
            for (Product p : lowStockProducts) {
                log.warn("【库存预警】商品 ID: {}, 名称: {}, 当前库存: {}",p.getId(), p.getName(), p.getStock());
            }
        }
    }
}
