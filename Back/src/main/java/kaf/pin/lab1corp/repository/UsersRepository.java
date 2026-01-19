// UsersRepository.java
package kaf.pin.lab1corp.repository;

import kaf.pin.lab1corp.entity.Users;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


public interface UsersRepository extends CrudRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
}