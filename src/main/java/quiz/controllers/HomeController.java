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

    @RequestMapping(value = "/signUp", method = RequestMethod.GET)
    public String signUp(Model model) {

        UserDto userDto = new UserDto();

        model.addAttribute("userDto", userDto);
        model.addAttribute("allRoles", roleService.allRoles());

        return "sign-up";
    }

    @RequestMapping(value = "/submitSignUp", method = RequestMethod.POST)
    public String submitSignUp(@ModelAttribute UserDto userDto) {

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

    @RequestMapping(value = "/signIn", method = RequestMethod.GET)//TODO:add different account situations to sign in
    public String signIn(Model model, @RequestParam("username") String username,
                         @RequestParam("password") String password) {

        if (userService.isUsernameExist(username) && userService.findUserByUsername(username).getPassword().equals(password)){

            User signedInUser = userService.findUserByUsername(username);
            Status signedInUserStatus = signedInUser.getStatus();

            if (signedInUserStatus.equals(Status.Active)){

                if (signedInUser.getRole().equals(roleService.findById(1L))){return "admin";}
                if (signedInUser.getRole().equals(roleService.findById(2L))){

                    model.addAttribute("studentId" , signedInUser.getId());
                    return "student";
                }
                else {

                    model.addAttribute("teacherId" , signedInUser.getId());
                    return "teacher";

                }


            }
            if (signedInUserStatus.equals(Status.Inactive)){

                return "inactive-account";//TODO:add to templates
            }
            else {

                return "waiting-account";//TODO : add template
            }

        }else {
            return "notExistedUsernameOrWrongPassword";//TODO:add template
        }

    }

}