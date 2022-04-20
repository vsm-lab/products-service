package de.hska.vslab.products;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @PostMapping(path = "/products")
    public @ResponseBody
    String addNewProduct(@RequestBody AddNewProductRequest request) {
        try {
            // todo: check if category exists
            // todo: wait for category service

            Product product = new Product();
            product.setName(request.name);
            product.setPrice(request.price);
            product.setCategoryId(request.categoryId);
            product.setDetails(request.details);
            productRepository.save(product);
            return "Saved";
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @GetMapping(path = "/products")
    public @ResponseBody
    Iterable<Product> getAllProduct() {
        try {
            return productRepository.findAll();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @GetMapping(path = "/products/{productId}")
    public @ResponseBody
    Product getProductById(@PathVariable(value = "productId") Integer productId) {
        try {
            var result = productRepository.findById(productId);
            return result.orElse(null);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @GetMapping(path = "/products/search")
    public @ResponseBody
    Iterable<Product> searchProducts(@RequestParam(value = "description", required = false) String description,
                                     @RequestParam(value = "minPrice", required = false) Double minPrice,
                                     @RequestParam(value = "maxPrice", required = false) Double maxPrice) {
        try {
            return productRepository.searchProduct(description, minPrice, maxPrice);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @DeleteMapping(path = "/products/{productId}")
    public @ResponseBody
    void deleteProductById(@PathVariable(value = "productId") Integer productId) {
        try {
            productRepository.deleteById(productId);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @DeleteMapping(path = "/products-by-category/{categoryId}")
    public @ResponseBody
    void deleteProductByCategoryId(@PathVariable(value = "categoryId") Integer categoryId) {
        try {
            productRepository.deleteByCategoryId(categoryId);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }
}