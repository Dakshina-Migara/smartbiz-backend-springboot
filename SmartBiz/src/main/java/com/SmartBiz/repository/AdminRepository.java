package com.SmartBiz.repository;

import com.SmartBiz.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * AdminRepository - Data Access Layer for the Admin entity.
 *
 * Extends JpaRepository<Admin, Long> which provides built-in CRUD methods:
 * - save() : Insert or update an Admin record
 * - findById() : Find an Admin by its primary key (admin_Id)
 * - findAll() : Retrieve all Admin records
 * - deleteById() : Delete an Admin by its primary key
 * - count() : Count total Admin records
 *
 * @Repository marks this as a Spring Data repository bean, enabling
 *             automatic exception translation from SQL exceptions to Spring's
 *             DataAccessException hierarchy.
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    /**
     * Finds an Admin by their email address.
     * Spring Data JPA auto-generates the SQL query from the method name:
     * → SELECT * FROM admin WHERE email = ?
     *
     * Returns Optional<Admin> to safely handle the case where no admin
     * exists with the given email (avoids NullPointerException).
     *
     * @param email the email address to search for
     * @return Optional containing the Admin if found, or empty if not
     */
    Optional<Admin> findByEmail(String email);

    /**
     * Finds an Admin by their associated business ID using a custom JPQL query.
     * JPQL (Java Persistence Query Language) operates on entity objects, not
     * tables:
     * → "a.business.business_id" navigates the Admin → Businesses relationship
     *
     * @Param binds the method parameter to the named parameter in the query.
     *
     * @param business_id the business ID to search for
     * @return Optional containing the Admin if found, or empty if not
     */
    @Query("SELECT a FROM Admin a WHERE a.business.business_id = :businessId")
    Optional<Admin> findByBusinessId(@Param("businessId") Long businessId);

    /**
     * Checks if an Admin with the given email already exists in the database.
     * Spring Data JPA auto-generates:
     * → SELECT COUNT(*) > 0 FROM admin WHERE email = ?
     *
     * Useful for validation before registering a new admin (prevent duplicates).
     *
     * @param email the email address to check
     * @return true if an Admin with this email exists, false otherwise
     */
    Boolean existsByEmail(String email);
}