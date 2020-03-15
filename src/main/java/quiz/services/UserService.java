package quiz.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import quiz.model.User;
import quiz.repositories.UserRepository;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public List<User> findAllUsersByName(String firstName , String lastName){
        return userRepository.findUsersByFirstNameAndLastName(firstName , lastName);
    }

    public User findUserByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public List<User> findUsersByStatus(String status){
        return userRepository.findUsersByStatusEquals(status);
    }

    public void saveUser(User user){
        userRepository.save(user);
    }

    public void removeUser(User user){
        userRepository.delete(user);
    }

    public void removeUserById(Long id){
        userRepository.deleteById(id);
    }

}
