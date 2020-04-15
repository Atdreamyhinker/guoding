package org.csu.mypetstore.controller;

import org.csu.mypetstore.domain.Account;
import org.csu.mypetstore.domain.Cart;
import org.csu.mypetstore.domain.CartItem;
import org.csu.mypetstore.domain.Item;
import org.csu.mypetstore.service.CartService;
import org.csu.mypetstore.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.SessionScope;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Controller
@SessionScope
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CatalogService catalogService;
    @Autowired
    private CartService cartService;

    @GetMapping("viewCart")
    public String viewCart(@SessionAttribute("account") Account account,Model model){

        List<CartItem> cartItemList = cartService.getCartItemList(account.getUsername());
        model.addAttribute("cartItemList",cartItemList);
        model.addAttribute("subTotal",this.totalPrice(account.getUsername()));
        return "cart/cart";
    }

    @GetMapping("addItemToCart")
    public String addItemToCart(@SessionAttribute("account")Account account,String workingItemId, Model model){
        CartItem cartItem = cartService.getCartItem(account.getUsername(),workingItemId);
        if (cartItem==null){
            cartItem = new CartItem();
            cartItem.setItemId(workingItemId);
            cartItem.setUsername(account.getUsername());
            cartItem.setQuantity(1);
            cartService.insertCartItem(cartItem);
        }else {
            cartItem.setQuantity(cartItem.getQuantity()+1);
            cartService.updateQuantity(cartItem);
        }
        return "redirect:/cart/viewCart";
    }

    @GetMapping("removeItemFromCart")
    public String removeItemFromCart(@SessionAttribute("account") Account account, String workingItemId, Model model){
        cartService.deleteCartItem(account.getUsername(),workingItemId);
        return "redirect:/cart/viewCart";
    }

    @PostMapping("updateCartQuantities")
    public String updateCartQuantities(@SessionAttribute("account") Account account,HttpServletRequest request){
        List<CartItem> cartItemList = cartService.getCartItemList(account.getUsername());
        Iterator<CartItem> cartItems = cartItemList.iterator();
        while (cartItems.hasNext()){
            CartItem cartItem = cartItems.next();
            String itemId = cartItem.getItemId();
            try{
                int quantity = Integer.parseInt(request.getParameter(itemId));

                if(quantity < 1){
                    cartService.deleteCartItem(account.getUsername(),itemId);
                }
                if(quantity!=cartItem.getQuantity()){
                    cartItem.setQuantity(quantity);
                    cartItem.setUsername(account.getUsername());
                    cartService.updateQuantity(cartItem);
                }
            }catch (Exception e){

            }
        }
        return "redirect:/cart/viewCart";
    }

    @GetMapping("checkOut")
    public String checkOut(@SessionAttribute("account") Account account,Model model){
        List<CartItem> cartItemList = cartService.getCartItemList(account.getUsername());
        model.addAttribute("cartItemList",cartItemList);
        model.addAttribute("subTotal",this.totalPrice(account.getUsername()));
        return "cart/checkout";
    }

    public BigDecimal totalPrice(String username){
        List<CartItem> cartItemList = cartService.getCartItemList(username);

        BigDecimal subTotal = new BigDecimal("0");
        Iterator<CartItem> items = cartItemList.iterator();
        while (items.hasNext()) {
            CartItem cartItem = (CartItem) items.next();
            BigDecimal listPrice = cartItem.getListPrice();
            BigDecimal quantity = new BigDecimal(String.valueOf(cartItem.getQuantity()));
            subTotal = subTotal.add(listPrice.multiply(quantity));
        }
        return subTotal;
    }
//    @Autowired
//    private CatalogService catalogService;
//    @Autowired
//    private Cart cart;
//    @GetMapping("addItemToCart")
//    public String addItemToCart(String workingItemId, Model model){
//        if(cart.containsItemId(workingItemId)){
//            cart.incrementQuantityByItemId(workingItemId);
//        }else{
//            boolean isInStock = catalogService.isItemInStock(workingItemId);
//            Item item = catalogService.getItem(workingItemId);
//            cart.addItem(item,isInStock);
//        }
//        model.addAttribute("cart",cart);
//        return "cart/cart";
//    }
//
//    @GetMapping("removeItemFromCart")
//    public String removeItemFromCart(String workingItemId, Model model){
//        Item item = cart.removeItemById(workingItemId);
//        model.addAttribute("cart",cart);
//        if(item == null){
//            model.addAttribute("msg", "Attempted to remove null CartItem from Cart.");
//            return "common/error";
//        }else{
//            return "cart/cart";
//        }
//    }
//
//    @PostMapping("updateCartQuantities")
//    public String updateCartQuantities(HttpServletRequest request, Model model){
//        Iterator<CartItem> cartItems = cart.getAllCartItems();
//        while (cartItems.hasNext()){
//            CartItem cartItem = cartItems.next();
//            String itemId = cartItem.getItem().getItemId();
//            try{
//                int quantity = Integer.parseInt(request.getParameter(itemId));
//                cart.setQuantityByItemId(itemId,quantity);
//                if(quantity < 1){
//                    cartItems.remove();
//                }
//            }catch (Exception e){
//
//            }
//        }
//        model.addAttribute("cart",cart);
//        return "cart/cart";
//    }
//
//    @GetMapping("checkOut")
//    public String checkOut(Model model){
//        model.addAttribute("cart",cart);
//        return "cart/checkout";
//    }

}

