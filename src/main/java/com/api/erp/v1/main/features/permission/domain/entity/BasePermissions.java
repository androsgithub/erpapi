package com.api.erp.v1.main.features.permission.domain.entity;

/**
 * Base class containing standard permission operations.
 * <p>
 * Defines common permission operations that are reused across all feature-specific
 * permission classes (User, Product, BusinessPartner, etc).
 * <p>
 * Each feature-specific permission class should define its own PREFIX and construct
 * permission constants using these base operations.
 *
 * @author ERP System
 * @version 1.0
 * @since 1.0
 */
public final class BasePermissions {

    private BasePermissions() {
    }

    // ==================== BASIC CRUD OPERATIONS ====================
    /**
     * Create operation constant
     */
    public static final String OPERATION_CREATE = "create";

    /**
     * Update operation constant
     */
    public static final String OPERATION_UPDATE = "update";

    /**
     * View/Read operation constant
     */
    public static final String OPERATION_VIEW = "view";

    /**
     * Delete operation constant
     */
    public static final String OPERATION_DELETE = "delete";

    // ==================== ACTIVATION/DEACTIVATION OPERATIONS ====================
    /**
     * Activate operation constant
     */
    public static final String OPERATION_ACTIVATE = "activate";

    /**
     * Deactivate operation constant
     */
    public static final String OPERATION_DEACTIVATE = "deactivate";

    // ==================== LISTING OPERATIONS ====================
    /**
     * List operation constant
     */
    public static final String OPERATION_LIST = "list";

    // ==================== MANAGEMENT OPERATIONS ====================
    /**
     * Manage operation constant (for managing sub-resources)
     */
    public static final String OPERATION_MANAGE = "manage";

    /**
     * Add/Assign operation constant
     */
    public static final String OPERATION_ADD = "add";

    /**
     * Remove operation constant
     */
    public static final String OPERATION_REMOVE = "remove";

    /**
     * Assign operation constant
     */
    public static final String OPERATION_ASSIGN = "assign";

    // ==================== WORKFLOW OPERATIONS ====================
    /**
     * Approve operation constant
     */
    public static final String OPERATION_APPROVE = "approve";

    /**
     * Reject operation constant
     */
    public static final String OPERATION_REJECT = "reject";

    /**
     * Search operation constant
     */
    public static final String OPERATION_SEARCH = "search";

    // ==================== PRODUCT-SPECIFIC OPERATIONS ====================
    /**
     * Lock operation constant (prevent modifications)
     */
    public static final String OPERATION_LOCK = "lock";

    /**
     * Discontinue operation constant (mark as discontinued)
     */
    public static final String OPERATION_DISCONTINUE = "discontinue";

    /**
     * Generate operation constant
     */
    public static final String OPERATION_GENERATE = "generate";
}
