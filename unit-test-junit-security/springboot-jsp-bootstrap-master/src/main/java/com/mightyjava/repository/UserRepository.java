package com.mightyjava.repository;

import com.mightyjava.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
	
	@Query("FROM Users WHERE userName=:username")
	Users findByUsername(@Param("username") String username);

	@Query("FROM Users WHERE id = :id")
	Optional<Users> findById(@Param("id") Long id);

	@Modifying
	@Transactional
	@Query("DELETE FROM Users WHERE id = :id")
	void deleteById(@Param("id") Long id);
}
