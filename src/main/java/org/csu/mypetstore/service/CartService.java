package org.csu.mypetstore.service;

import org.csu.mypetstore.domain.Cart;
import org.csu.mypetstore.domain.CartItem;
import org.csu.mypetstore.domain.Item;
import org.csu.mypetstore.persistence.CartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service
public class CartService {
    @Autowired
    CartMapper cartMapper;

    public void insertCartItem(CartItem cartItem){
        cartMapper.insertCartItem(cartItem);
    }

    public CartItem getCartItem(String username,String itemId){
        CartItem cartItem = new CartItem();
        cartItem.setUsername(username);
        cartItem.setItemId(itemId);
        return cartMapper.getCartItemByUsernameAndItemId(cartItem);
    }

//    public Cart getCartByUsername(String username){
//        List<CartItem> cartItemList = cartMapper.getCartItemList(username);
//        Cart cart = new Cart();
//        Iterator<CartItem> cartItems = cartItemList.iterator();
//
//        while (cartItems.hasNext()){
//            CartItem cartItem = cartItems.next();
//
//            boolean isInStock = cartItem.isInStock();
//            String itemId = cartItem.getItem().getItemId();
//            cart.addItem(cartItem.getItem(),isInStock);
//        }
//        return cart;
//    }


    public void updateQuantity(CartItem cartItem){
        cartMapper.updateQuantity(cartItem);
    }

    public List<CartItem> getCartItemList(String username){
        return cartMapper.getCartItemList(username);
    }

    public void deleteCartItem(String username,String itemId){
        CartItem cartItem = new CartItem();
        cartItem.setUsername(username);
        cartItem.setItemId(itemId);
        cartMapper.deleteCartItem(cartItem);
    }

    public void deleteCartByUsername(String username){
        cartMapper.deleteCartItemByUsername(username);
    }
}
