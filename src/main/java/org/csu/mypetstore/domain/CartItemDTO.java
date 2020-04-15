package org.csu.mypetstore.domain;

import java.util.List;

public class CartItemDTO extends CartItem {
    private List<CartItem> cartItemList;
    public List<CartItem> getCartItemList(){
        return cartItemList;
    }

    public void setCartItemList(List<CartItem> cartItemList){
        this.cartItemList = cartItemList;
    }
}
