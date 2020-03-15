package quiz.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import quiz.model.Role;
import quiz.repositories.RoleRepository;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public List<Role> allRoles(){
        return roleRepository.findAll();
    }

    public Role findById(Long id){
        return roleRepository.findById(id).get();
    }

}
