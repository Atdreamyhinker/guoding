package org.csu.mypetstore.persistence;

import org.csu.mypetstore.domain.Cart;
import org.csu.mypetstore.domain.CartItem;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartMapper {
    void insertCartItem(CartItem cartItem);
    CartItem getCartItemByUsernameAndItemId(CartItem cartItem);
    void updateQuantity(CartItem cartItem);
    List<CartItem> getCartItemList(String username);
    void deleteCartItem(CartItem cartItem);
    void deleteCartItemByUsername(String username);
}
