package com.mightyjava.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mightyjava.model.Address;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
}
