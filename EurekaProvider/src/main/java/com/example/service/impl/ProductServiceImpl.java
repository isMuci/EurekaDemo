package com.example.service.impl;

import com.example.entity.Product;
import com.example.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Override
    public List<Product> get() {
        return Arrays.asList(new Product(1,"a"));
    }
}
