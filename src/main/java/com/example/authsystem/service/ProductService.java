package com.example.authsystem.service;

import com.example.authsystem.dto.ProductDTO;
import com.example.authsystem.entity.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    // 创建商品
    ProductDTO createProduct(ProductDTO productDTO);

    // 更新商品
    ProductDTO updateProduct(Long id, ProductDTO productDTO);

    // 删除商品
    void deleteProduct(Long id);

    // 根据ID查询商品
    Optional<ProductDTO> getProductById(Long id);

    // 根据名称查询商品列表
    List<ProductDTO> getProductsByName(String name);

    // 根据多个ID查询商品
    List<ProductDTO> getProductsByIds(List<Long> ids);

    // 获取所有商品
    List<ProductDTO> getAllProducts();

    // 实体转DTO
    ProductDTO convertToDTO(Product product);

    // DTO转实体
    Product convertToEntity(ProductDTO productDTO);
}