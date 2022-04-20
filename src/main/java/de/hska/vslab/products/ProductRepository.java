package de.hska.vslab.products;

import org.springframework.data.repository.CrudRepository;

interface ProductRepository extends CrudRepository<Product, Integer>, ProductRepositoryExtension {
}