package de.hska.vslab.products;

import java.util.List;

interface ProductRepositoryExtension {
    List<Product> searchProduct(String description, Double minPrice, Double maxPrice);

    void deleteByCategoryId(Integer categoryId);
}