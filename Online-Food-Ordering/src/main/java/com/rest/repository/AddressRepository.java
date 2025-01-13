package com.rest.repository;

import com.rest.model.Address;
import com.rest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(User user);

    List<Address> findByUserId(long id);
}
