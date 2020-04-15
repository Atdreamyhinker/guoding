package org.csu.mypetstore.controller;

import org.csu.mypetstore.domain.Account;
import org.csu.mypetstore.domain.Cart;
import org.csu.mypetstore.domain.Order;
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

  @GetMapping("newOrderForm")
  public String newOrderForm(HttpSession session,Model model)
  {
      Account account = (Account) session.getAttribute("account");
      boolean isAuthenticated=(boolean)session.getAttribute("authenticated");
      Cart cart=(Cart) session.getAttribute("cart");

      Order order = new Order();
      boolean shippingAddressRequired = false;
      boolean confirmed = false;
      List<Order> orderList = null;
      model.addAttribute("shippingAddressRequired",shippingAddressRequired);
      model.addAttribute("confirmed",confirmed);
      model.addAttribute("orderList",orderList);
      model.addAttribute("order",order);

      if(account==null||!isAuthenticated){
          String msg="You must sign on before attempting to check out.  Please sign on and try checking out again.";
          model.addAttribute("msg",msg);
          return "account/signon";
      }
      else if(cart!=null)
      {
          order.initOrder(account,cart);
          return "order/NewOrderForm";
      }
      else {
          String msg= "An order could not be created because a cart could not be found.";
          model.addAttribute("msg",msg);
          return "common/error";
      }
  }

  @PostMapping("newOrder")
  public String newOrder(Order order,HttpSession session,Model model){
        boolean shippingAddressRequired=(boolean)session.getAttribute("shippingAddressRequired");
        boolean isConfirmed=(boolean)session.getAttribute("isConfirmed");

      model.addAttribute("order",order);
      if (shippingAddressRequired) {
          shippingAddressRequired = false;
          return "order/ShippingForm";
      } else if (!isConfirmed) {
          return "order/ConfirmOrder";
      } else if (order!= null) {

          orderService.insertOrder(order);

          Cart cart=(Cart)session.getAttribute("cart");
          cart=new Cart();

          String msg= "Thank you, your order has been submitted.";
          model.addAttribute("msg",msg);

          return "order/ViewOrder";
      } else {
          String msg="An error occurred processing your order (order was null).";
          return "common/error";
      }

  }
}
