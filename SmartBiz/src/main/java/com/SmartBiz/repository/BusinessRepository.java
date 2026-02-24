package com.SmartBiz.repository;

import com.SmartBiz.entity.Businesses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * BusinessRepository - Data Access Layer for the Businesses entity.
 *
 * Extends JpaRepository<Businesses, Long> which provides built-in CRUD methods:
 * - save() : Insert or update a Business record
 * - findById() : Find a Business by its primary key (business_id)
 * - findAll() : Retrieve all Business records
 * - deleteById() : Delete a Business by its primary key
 * - count() : Count total Business records
 *
 * No custom query methods are defined here — all operations use
 * the standard JpaRepository methods inherited from the parent interface.
 *
 * Used by both AdminServiceImpl (to list all businesses) and
 * BusinessOwnerServiceImpl (to look up a business when adding inventory/sales).
 */
@Repository
public interface BusinessRepository extends JpaRepository<Businesses, Long> {
}