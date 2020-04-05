package org.csu.mypetstore.controller;

import org.csu.mypetstore.domain.Account;
import org.csu.mypetstore.domain.Order;
import org.csu.mypetstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    private static final List<String> CARD_TYPE_LIST;
    static {
        List<String> cardList = new ArrayList<String>();
        cardList.add("Visa");
        cardList.add("MasterCard");
        cardList.add("American Express");
        CARD_TYPE_LIST = Collections.unmodifiableList(cardList);
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


  @GetMapping("viewOrder")
  public String viewOrder(int orderId,Model model,HttpSession session){
       // int orderId=Integer.parseInt(OrderId);
      Account account = (Account) session.getAttribute("account");

      Order order=orderService.getOrder(orderId);

      model.addAttribute("order",order);

      assert account != null;
      if(account.getUsername().equals(order.getUsername())){
          return "order/ViewOrder";
      }
   else {
        order = null;
        model.addAttribute("msg","You may only view your own orders.");
          return "common/error";
    }
  }
}
