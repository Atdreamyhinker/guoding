package org.csu.mypetstore.controller;

import org.csu.mypetstore.domain.Account;
import org.csu.mypetstore.domain.Order;
import org.csu.mypetstore.service.CartService;
import org.csu.mypetstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    private static final List<String> CARD_TYPE_LIST;
    static {
        List<String> cardList = new ArrayList<String>();
        cardList.add("Visa");
        cardList.add("MasterCard");
        cardList.add("American Express");
        CARD_TYPE_LIST = Collections.unmodifiableList(cardList);
    }

    @GetMapping("newOrderForm")
    public String newOrderForm(@SessionAttribute("account") Account account,Model model){
        Order order = new Order();
        order.initOrder(account,cartService.getCartItemList(account.getUsername()));
        model.addAttribute("order",order);
        return "order/newOrderForm";
    }

    @PostMapping("confirmOrderForm")
    public String confirmOrderForm(@SessionAttribute("account")Account account, String shippingAddressRequired,Model model){
        Order order = new Order();

        if (shippingAddressRequired!=null){
            //改地址
            model.addAttribute("order",order);

            return "order/shippingForm";
        }else{
            //不改地址
            order.initOrder(account,cartService.getCartItemList(account.getUsername()));
            model.addAttribute("order",order);
            return "order/confirmOrder";
        }
    }

    @PostMapping("shippingAddress")
    public String shippingAddress(@SessionAttribute("account")Account account,Order order,Model model){
        Order order1 = new Order();
        order1.initOrder(account,cartService.getCartItemList(account.getUsername()));

        order1.setShipToFirstName(order.getShipToFirstName());
        order1.setShipToLastName(order.getShipToLastName());
        order1.setShipAddress1(order.getShipAddress1());
        order1.setShipAddress2(order.getShipAddress2());
        order1.setShipCity(order.getShipCity());
        order1.setShipState(order.getShipState());
        order1.setShipZip(order.getShipZip());
        order1.setShipCountry(order.getShipCountry());

        model.addAttribute("order",order1);
        return "order/confirmOrder";
    }

    @GetMapping("listOrders")
    public String listOrders(Model model,HttpSession session){
        Account account=(Account)session.getAttribute("account");
        assert account != null;
        List<Order> orderList=orderService.getOrdersByUsername(account.getUsername());
        System.out.printf(account.getUsername());
        model.addAttribute("orderList",orderList);
        return "order/ListOrders";
    }


    @PostMapping("viewOrder")
    public String viewOrder(@SessionAttribute("account") Account account,Order order,Model model){
        Order order1 = new Order();

        order1.initOrder(account,cartService.getCartItemList(account.getUsername()));
        order1.setShipToFirstName(order.getShipToFirstName());
        order1.setShipToLastName(order.getShipToLastName());
        order1.setShipAddress1(order.getShipAddress1());
        order1.setShipAddress2(order.getShipAddress2());
        order1.setShipCity(order.getShipCity());
        order1.setShipState(order.getShipState());
        order1.setShipZip(order.getShipZip());
        order1.setShipCountry(order.getShipCountry());

        orderService.insertOrder(order1);
        cartService.deleteCartByUsername(account.getUsername());
        model.addAttribute("order", order1);
        return "order/viewOrder";
    }
}
