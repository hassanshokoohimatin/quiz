package quiz.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quiz.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role , Long> {
}
