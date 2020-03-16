package quiz.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import quiz.model.Role;
import quiz.model.User;
import quiz.model.enums.Status;
import quiz.repositories.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<User> findUsersByStatus(Status status){
        return userRepository.findUsersByStatus(status);
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

    public User findById(Long id){
        return userRepository.findById(id).get();
    }

    public List<User> findUsersByRole(Role role){
        return userRepository.findUsersByRole(role);
    }

    public boolean isUsernameExist(String username){
        if (userRepository.findAll().stream().filter(user -> user.getUsername().equals(username))
                .collect(Collectors.toList()).size() == 0)
            return false;
        else
            return true;
    }

    public boolean isEmailExist(String email){
        if (userRepository.findAll().stream().filter(user -> user.getEmail().equals(email))
                .collect(Collectors.toList()).size() == 0)
            return false;
        else
            return true;
    }

    public boolean isMobileExist(String mobile){
        if (userRepository.findAll().stream().filter(user -> user.getMobileNumber().equals(mobile))
                .collect(Collectors.toList()).size() == 0)
            return false;
        else
            return true;
    }

    public boolean isNationalCodeExist(String nationalCode){
        if (userRepository.findAll().stream().filter(user -> user.getNationalCode().equals(nationalCode))
                .collect(Collectors.toList()).size() == 0)
            return false;
        else
            return true;
    }

}
