/**
 * The User Module is responsible for:
 * <ul>
 *     <li>Managing user CRUD operations.</li>
 *     <li>Handling validation for user data.</li>
 *     <li>Ensuring unique email constraints.</li>
 *     <li>Publishing event for audit and logging modules.</li>
 * </ul>
 * 
 * <p>
 * This module follows the **Spring Boot Modulith** architecture,
 * ensuring encapsulation and event-driven communication.
 * </p>
 * 
 * <p>
 * This module is based on **MongoDB** (not JPA), utilizing **Spring Data MongoDB**.
 * </p>
 *
 * @author Your Name
 * @version 1.0
 * @since 2025-03-06
 */
@NonNullApi
@NonNullFields
package com.yosri.defensy.backend.modules.user;

import org.springframework.lang.NonNullApi;
import org.springframework.lang.NonNullFields;
