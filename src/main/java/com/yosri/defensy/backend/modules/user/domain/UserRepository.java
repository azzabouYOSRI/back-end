package com.yosri.defensy.backend.modules.user.domain;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
@SuppressWarnings("unchecked")
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
