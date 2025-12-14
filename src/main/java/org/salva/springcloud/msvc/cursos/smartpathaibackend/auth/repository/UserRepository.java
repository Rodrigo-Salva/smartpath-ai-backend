package org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.repository;

// backend/src/main/java/com/smartpath/auth/repository/UserRepository.java

import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);
}

