package org.athena.test.jpa.web;

import jakarta.annotation.PostConstruct;
import org.athena.test.jpa.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostConstruct
    public void test(){
        orderService.createOrder(1L,10);
    }

}
