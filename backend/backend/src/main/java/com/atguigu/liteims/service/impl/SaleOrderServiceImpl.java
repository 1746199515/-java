package com.atguigu.liteims.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atguigu.liteims.dto.OrderDTO;
import com.atguigu.liteims.entity.Customer;
import com.atguigu.liteims.entity.Product;
import com.atguigu.liteims.entity.SaleOrder;
import com.atguigu.liteims.entity.SaleOrderItem;
import com.atguigu.liteims.mapper.CustomerMapper;
import com.atguigu.liteims.mapper.ProductMapper;
import com.atguigu.liteims.mapper.SaleOrderItemMapper;
import com.atguigu.liteims.mapper.SaleOrderMapper;
import com.atguigu.liteims.service.SaleOrderService;
import com.atguigu.liteims.vo.OrderExportVO;
import com.atguigu.liteims.vo.SaleOrderVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SaleOrderServiceImpl extends ServiceImpl<SaleOrderMapper, SaleOrder> implements SaleOrderService {

    @Autowired
    SaleOrderMapper saleOrderMapper;

    @Autowired
    ProductMapper productMapper;

    @Autowired
    SaleOrderItemMapper saleOrderItemMapper;

    @Autowired
    CustomerMapper customerMapper;

    @Override
    public IPage<SaleOrderVO> findPage(IPage page, String orderNo) {
        IPage<SaleOrderVO> saleOrderVOPage = saleOrderMapper.findPage(page,orderNo);
        return saleOrderVOPage;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveOrder(OrderDTO orderDTO) {

        BigDecimal totalAmount = BigDecimal.ZERO;

        //保存订单数据 (总价格暂时不保存)    主键回填
        SaleOrder saleOrder = new SaleOrder();
        saleOrder.setOrderNo(String.valueOf(System.currentTimeMillis()));
        saleOrder.setCustomerId(orderDTO.getCustomerId());
        saleOrder.setUserId(orderDTO.getUserId());
        saleOrder.setTotalAmount(totalAmount);
        saleOrderMapper.insert(saleOrder);

        Long orderId = saleOrder.getId();  //主键回填


        List<OrderDTO.OrderItemDTO> items = orderDTO.getItems();
        for (OrderDTO.OrderItemDTO item : items) {
            Long productId = item.getProductId();
            Product product = productMapper.selectById(productId);
            //校验库存
            if(product.getStock() < item.getQuantity()){
                throw new RuntimeException(product.getName()+"-库存不足");
            }
            //更新库存
            product.setStock(product.getStock() - item.getQuantity());
            productMapper.updateById(product);

            //保存订单项数据  多条
            SaleOrderItem saleOrderItem = new SaleOrderItem();
            saleOrderItem.setOrderId(orderId);
            saleOrderItem.setProductId(item.getProductId());
            saleOrderItem.setQuantity(item.getQuantity());
            saleOrderItem.setPrice(product.getPrice());
            saleOrderItem.setAmount(product.getPrice().multiply(new BigDecimal(item.getQuantity())));
            saleOrderItemMapper.insert(saleOrderItem);

            totalAmount = totalAmount.add(saleOrderItem.getAmount());
        }

        //更新订单总价格
        saleOrder.setTotalAmount(totalAmount);
        saleOrderMapper.updateById(saleOrder);
    }


    @Override
    public void exportOrderList() {
        try {
            //如何获取HttpServletResponse对象
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletResponse response = attributes.getResponse();


            // 1. 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("销售订单报表", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");  // 另存为   以附件  文件编码


            // 2. 查询数据
            List<SaleOrder> orders = saleOrderMapper.selectList(new QueryWrapper<SaleOrder>().orderByDesc("create_time"));

            //获取所有订单对应的客户id集合
            List<Long> customerIdList = orders.stream().map(SaleOrder::getCustomerId).distinct().toList();
            //根据客户ID集合查询客户实体对象集合
            List<Customer> customerList = customerMapper.selectList(new LambdaQueryWrapper<Customer>().in(Customer::getId, customerIdList));
            //将List集合转换为Map集合  Map<id,name>
            Map<Long, String> idAndNameMap = customerList.stream().collect(Collectors.toMap(Customer::getId, Customer::getName));


            //3.转换数据
            List<OrderExportVO> exportList = new ArrayList<>();
            for (SaleOrder order : orders) {
                OrderExportVO vo = new OrderExportVO();
                vo.setOrderNo(order.getOrderNo());

                vo.setTotalAmount(order.getTotalAmount());
                vo.setCreateTime(order.getCreateTime());

                // 状态转换
                String statusStr = switch (order.getStatus()) { //状态：0-待支付，1-已支付，2-已发货，3-已完成，4-已取消
                    case 0 -> "待支付";
                    case 1 -> "已支付";
                    case 2 -> "已发货";
                    case 3 -> "已完成";
                    case 4 -> "取消";
                    default -> "待处理";
                };
                vo.setStatusStr(statusStr);
// 订单数量多的话，性能很差
//                Customer customer = customerMapper.selectById(order.getCustomerId());
//                vo.setCustomerName(customer.getName());

                vo.setCustomerName(idAndNameMap.get(order.getCustomerId()));
                exportList.add(vo);
            }

            // 3. 导出文件
            EasyExcel.write(response.getOutputStream(),OrderExportVO.class).sheet("销售订单信息").doWrite(exportList);
        } catch (Exception e) {
            throw new RuntimeException("导出订单失败");
        }
    }
}
