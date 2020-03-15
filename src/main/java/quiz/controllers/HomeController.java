package quiz.controllers;

import org.apache.catalina.connector.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import quiz.dto.UserDto;
import quiz.model.Role;
import quiz.model.User;
import quiz.model.enums.Gender;
import quiz.model.enums.RoleType;
import quiz.model.enums.Status;
import quiz.services.RoleService;
import quiz.services.UserService;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;

    @RequestMapping(value = "/signUp" , method = RequestMethod.GET)
    public String signUp(Model model){

        UserDto userDto = new UserDto();

        model.addAttribute("userDto" , userDto);
        model.addAttribute("allRoles" , roleService.allRoles());

        return "sign-up";
    }

    @RequestMapping(value = "/submitSignUp" , method = RequestMethod.POST)
    public String submitSignUp(@ModelAttribute UserDto userDto){

        if (userService.isUsernameExist(userDto.getUsername()) || userService.isEmailExist(userDto.getEmail()))
            return "usernameOrEmail-already-exist";//TODO:add to templates
        else {

            User newUser = new User();

            newUser.setUsername(userDto.getUsername());
            newUser.setPassword(userDto.getPassword());
            newUser.setFirstName(userDto.getFirstName());
            newUser.setLastName(userDto.getLastName());
            newUser.setEmail(userDto.getEmail());
            newUser.setNationalCode(userDto.getNationalCode());
            newUser.setMobileNumber(userDto.getMobileNumber());
            newUser.setStatus(Status.Waiting);
            for (Role role : roleService.allRoles()) {
                if (role.getRoleType().toString().equals(userDto.getRole())) {
                    newUser.setRole(role);
                    break;
                }
            }
            if (userDto.getGender().equals("Male"))
                newUser.setGender(Gender.Male);
            else
                newUser.setGender(Gender.Female);

            userService.saveUser(newUser);

            return "success-signup";
        }
    }

    @RequestMapping(value = "/signIn" , method = RequestMethod.GET)
    public String signIn(@RequestParam("username") String username ,
                         @RequestParam("password") String password){

        if (userService.isUsernameExist(username)){

            User signedInUser = new User();
            signedInUser = userService.findUserByUsername(username);
            if (signedInUser.getPassword().equals(password)){

                if (signedInUser.getRole().getRoleType().equals(RoleType.Admin))
                    return "admin";
                else if (signedInUser.getRole().getRoleType().equals(RoleType.Student))
                    return "student";
                else
                    return "teacher";
            }
            else
                return "wrong-password";//TODO:add to templates
        }
        else
            return "wrong-username";//TODO:add to templates
    }
}
