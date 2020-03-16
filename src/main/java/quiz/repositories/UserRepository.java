package quiz.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quiz.model.Role;
import quiz.model.User;
import quiz.model.enums.Status;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User , Long> {

    public User findByUsername(String username);

    public List<User> findUsersByStatus(Status status);

    public List<User> findUsersByFirstNameAndLastName(String firstName , String lastName);

    public List<User> findUsersByRole(Role role);

}
