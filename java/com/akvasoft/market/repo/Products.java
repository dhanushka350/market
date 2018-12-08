package com.akvasoft.market.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Products extends JpaRepository<com.akvasoft.market.modal.Products, String> {
    com.akvasoft.market.modal.Products findTopByStatus(String status);
}
