package com.example.ecommerce.repository;

import com.example.ecommerce.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface AddressRepository extends JpaRepository<Address,Long> {
}
