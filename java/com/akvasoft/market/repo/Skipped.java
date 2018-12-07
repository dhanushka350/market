package com.akvasoft.market.repo;

import com.akvasoft.market.modal.SkippedProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

@Repository
public interface Skipped extends JpaRepository<SkippedProducts, Integer> {
}
