package de.hska.vslab.products;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class ProductController {

    @Value("${category-service.base-url}")
    private String categoryBaseUrl;

    @Autowired
    private ProductRepository productRepository;

    private static final RestTemplate restTemplate = new RestTemplate();

    @PostMapping(path = "/products")
    public ResponseEntity<String> addNewProduct(@RequestBody AddNewProductRequest request) {
        try {
            var categoriesResponse = restTemplate.exchange(
                    categoryBaseUrl + "/categories/" + request.categoryId,
                    HttpMethod.GET,
                    null,
                    String.class);
            assert (categoriesResponse.getStatusCode().is2xxSuccessful());
            Product product = new Product();
            product.setName(request.name);
            product.setPrice(request.price);
            product.setCategoryId(request.categoryId);
            product.setDetails(request.details);
            productRepository.save(product);
            return new ResponseEntity<>("Saved", HttpStatus.OK);
        } catch (Exception ex) {
            if (ex instanceof HttpClientErrorException &&
                    ((HttpClientErrorException) ex).getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "categoryId not found");
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @GetMapping(path = "/products")
    public ResponseEntity<Iterable<Product>> getAllProduct() {
        try {
            return new ResponseEntity<>(productRepository.findAll(), HttpStatus.OK);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @GetMapping(path = "/products/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable(value = "productId") Integer productId) {
        try {
            var product = productRepository.findById(productId);
            if (product.isPresent()) {
                return new ResponseEntity<>(product.get(), HttpStatus.OK);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "productId not found");
            }
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @GetMapping(path = "/products/search")
    public ResponseEntity<Iterable<Product>> searchProducts(@RequestParam(value = "description", required = false) String description,
                                                            @RequestParam(value = "minPrice", required = false) Double minPrice,
                                                            @RequestParam(value = "maxPrice", required = false) Double maxPrice) {
        try {
            var products = productRepository.searchProduct(description, minPrice, maxPrice);
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @DeleteMapping(path = "/products/{productId}")
    public ResponseEntity<Void> deleteProductById(@PathVariable(value = "productId") Integer productId) {
        try {
            productRepository.deleteById(productId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @DeleteMapping(path = "/products/by-category/{categoryId}")
    public ResponseEntity<Void> deleteProductsByCategoryId(@PathVariable(value = "categoryId") Integer categoryId) {
        try {
            productRepository.deleteByCategoryId(categoryId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }
}