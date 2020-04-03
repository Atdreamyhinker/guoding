package org.csu.mypetstore.persistence;

import org.csu.mypetstore.domain.Category;

import java.util.List;

public interface CategoryMapper {
       Category getCategory(String categoryId);

       List<Category> getCategoryList();
}
