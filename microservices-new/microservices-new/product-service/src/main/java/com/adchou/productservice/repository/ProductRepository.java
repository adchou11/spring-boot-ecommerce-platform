package com.adchou.productservice.repository;

import com.adchou.productservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface ProductRepository extends MongoRepository<Product, String>{
    
}
