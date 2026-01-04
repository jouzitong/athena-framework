package org.athena.test.jpa.service;

import org.athena.test.jpa.bean.BizOrder;
import org.athena.test.jpa.bean.ProductStock;
import org.athena.test.jpa.repository.OrderRepository;
import org.athena.test.jpa.repository.ProductStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private ProductStockRepository stockRepo;
    @Autowired
    private OrderRepository orderRepo;

    @Transactional
    public BizOrder createOrder(Long productId, Integer qty) {

        ProductStock stock = stockRepo.lockById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        if (stock.getAvailableStock() < qty) {
            throw new RuntimeException("库存不足");
        }

        stock.setAvailableStock(stock.getAvailableStock() - qty);
        stockRepo.save(stock);

        BizOrder order = new BizOrder();
        order.setOrderNo(UUID.randomUUID().toString());
        order.setProductId(productId);
        order.setQuantity(qty);
        order.setStatus("NEW");

        return orderRepo.save(order);
    }

    @Transactional
    public void cancelOrder(Long orderId) {

        BizOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!"NEW".equals(order.getStatus())) return;

        ProductStock stock = stockRepo.lockById(order.getProductId())
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        stock.setAvailableStock(stock.getAvailableStock() + order.getQuantity());
        stockRepo.save(stock);

        order.setStatus("CANCELED");
        orderRepo.save(order);
    }
}
