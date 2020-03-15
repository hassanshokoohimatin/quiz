package quiz.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quiz.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User , Long> {

    public User findByUsername(String username);

    public List<User> findUsersByStatusEquals(String status);

    public List<User> findUsersByFirstNameAndLastName(String firstName , String lastName);

}
