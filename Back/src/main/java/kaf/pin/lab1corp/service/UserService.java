package kaf.pin.lab1corp.service;

import kaf.pin.lab1corp.entity.Users;
import kaf.pin.lab1corp.repository.UsersRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    private final UsersRepository usersRepository;

    public UserService(UsersRepository userRepository) {
        this.usersRepository = userRepository;
    }

    public Users saveUser(Users user) {
        return usersRepository.save(user);
    }

    public Optional<Users> findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }
}
