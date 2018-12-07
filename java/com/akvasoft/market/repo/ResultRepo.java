package com.akvasoft.market.repo;

import com.akvasoft.market.modal.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepo extends JpaRepository<Result, Integer> {
    List<Result> findAllByCodeEqualsAndWebsiteEquals(String code, String website);

    Result findFirstByOrderByIdDesc();
}
