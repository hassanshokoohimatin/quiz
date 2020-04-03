package quiz.controllers;

import org.apache.catalina.connector.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import quiz.dto.EditAccountDto;
import quiz.dto.UserDto;
import quiz.model.Role;
import quiz.model.User;
import quiz.model.enums.Gender;
import quiz.model.enums.RoleType;
import quiz.model.enums.Status;
import quiz.services.ForgotPasswordQuestionService;
import quiz.services.RoleService;
import quiz.services.UserService;

import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ForgotPasswordQuestionService fpqService;

    @RequestMapping(value = "/signUp", method = RequestMethod.GET)
    public String signUp(Model model) {

        UserDto userDto = new UserDto();

        model.addAttribute("userDto", userDto);
        model.addAttribute("allRoles", roleService.allRoles());
        model.addAttribute("allSecurityQuestions" , fpqService.findAll());

        return "sign-up";
    }

    @RequestMapping("/signUpMessage")
    public String signUpMessage(Model model ,
                              @RequestParam("message") int message){
        model.addAttribute("message" , message);

        return "signUpMessage";
    }

    @RequestMapping("/forgot")
    public String forgotPass(){
        return "forgot-password";
    }

    @RequestMapping("/forgotPassword")
    public String forgot(Model model ,
                         RedirectAttributes ra ,
                         @RequestParam("username") String username){

        if (userService.isUsernameExist(username)) {
            User user = userService.findUserByUsername(username);

            model.addAttribute("user", user);

            return "forgot-password-result";
        }else {
            ra.addAttribute("message" , 5);
            return "redirect:/signInMessage";
        }
    }

    @RequestMapping("/submitForgotPassword/{userId}")
    public String forgotPasswordResult(@PathVariable("userId") Long userId ,
                                       RedirectAttributes ra ,
                                       @RequestParam("securityQuestionAnswer") String answer){

        User user = userService.findById(userId);

        if (answer.equals(user.getSecurityAnswer())){

            ra.addAttribute("username" , user.getUsername());
            ra.addAttribute("password" , user.getPassword());
            return "redirect:/signIn";

        }else {

            ra.addAttribute("message" , 4);
            return "redirect:/signInMessage";
        }

    }

    @RequestMapping(value = "/submitSignUp", method = RequestMethod.POST)
    public String submitSignUp(RedirectAttributes ra ,
                               @ModelAttribute UserDto userDto) {

        if (userDto.getUsername() != "" && userService.isUsernameExist(userDto.getUsername())) {
            ra.addAttribute("message" , 1);
            return "redirect:/signUpMessage";
        } else if (userDto.getEmail() != "" && userService.isEmailExist(userDto.getEmail())) {
            ra.addAttribute("message" , 2);
            return "redirect:/signUpMessage";
        } else {

            User newUser = new User();

            if (userDto.getFirstName() != "")
                newUser.setFirstName(userDto.getFirstName());
            else{
                ra.addAttribute("message" , 3);
                return "redirect:/signUpMessage";
            }
            if (userDto.getLastName() != "")
                newUser.setLastName(userDto.getLastName());
            else {
                ra.addAttribute("message" , 4);
                return "redirect:/signUpMessage";
            }
            if (userDto.getEmail() != "")
                newUser.setEmail(userDto.getEmail());
            else {
                ra.addAttribute("message" , 5);
                return "redirect:/signUpMessage";
            }

            if (userDto.getMobileNumber() != "") {

                if (userDto.getMobileNumber().startsWith("0") && userDto.getMobileNumber().charAt(1)=='9' && userDto.getMobileNumber().length()==11)
                    newUser.setMobileNumber(userDto.getMobileNumber());
                else{
                    ra.addAttribute("errorNumber" , 6);
                    return "redirect:/signUpMessage";
                }
            }
            else {
                ra.addAttribute("message" , 7);
                return "redirect:/signUpMessage";
            }

            if (userDto.getNationalCode() != ""){

                if (userDto.getNationalCode().length() != 10){
                    ra.addAttribute("message" , 8);
                    return "redirect:/signUpMessage";
                }else
                    newUser.setNationalCode(userDto.getNationalCode());
            }
            else {
                ra.addAttribute("message" , 9);
                return "redirect:/signUpMessage";
            }

            if (userDto.getUsername() != "") {

                Character[] chars = {'!','@','#','$','%','^','&','*','(',')',':',';','"',',','/','~','`','+','=','<','>'};
                for (Character c : chars){
                    if (userDto.getUsername().indexOf(c) != -1) {
                        ra.addAttribute("message" , 10);
                        return "redirect:/signUpMessage";
                    }
                }

                if (userDto.getUsername().length()<6) {
                    ra.addAttribute("message" , 11);
                    return "redirect:/signUpMessage}";
                }
                newUser.setUsername(userDto.getUsername());
            }
            else {
                ra.addAttribute("message" , 12);
                return "redirect:/signUpMessage";
            }

            if (userDto.getPassword() != ""){

                if (userDto.getPassword().equals(userDto.getPasswordConfirm())){
                    if (userDto.getPassword().length()<8){
                        ra.addAttribute("message" , 13);
                        return "redirect:/signUpMessage";
                    }else {
                        newUser.setPassword(userDto.getPassword());
                    }

                }else {

                    ra.addAttribute("message" , 14);
                    return "redirect:/signUpMessage";
                }
            }
            else {
                ra.addAttribute("message" , 15);
                return "redirect:/signUpMessage";
            }

            newUser.setStatus(Status.Waiting);

            if (userDto.getSecurityQuestion() != ""){

                if (userDto.getSecurityQuestionAnswer() != ""){

                    newUser.setSecurityQuestion(userDto.getSecurityQuestion());
                    newUser.setSecurityAnswer(userDto.getSecurityQuestionAnswer());

                }else {
                    ra.addAttribute("message" , 18);
                    return "redirect:/signUpMessage";
                }

            }else {
                ra.addAttribute("message" , 19);
                return "redirect:/signUpMessage";
            }

            for (Role role : roleService.allRoles()) {
                if (role.getRoleType().toString().equals(userDto.getRole())) {
                    newUser.setRole(role);
                    break;
                }
            }
            if (userDto.getGender() == ""){
                ra.addAttribute("message" , 16);
                return "redirect:/signUpMessage";
            }
            if (userDto.getGender().equals("Male"))
                newUser.setGender(Gender.Male);
            else
                newUser.setGender(Gender.Female);

            userService.saveUser(newUser);

            ra.addAttribute("message" , 17);
            return "redirect:/signUpMessage";
        }
    }

    @RequestMapping(value = "/signIn", method = RequestMethod.GET)
    public String signIn(Model model,
                         RedirectAttributes ra ,
                         @RequestParam("username") String username,
                         @RequestParam("password") String password) {

        if (userService.isUsernameExist(username) && userService.findUserByUsername(username).getPassword().equals(password)) {

            User signedInUser = userService.findUserByUsername(username);
            Status signedInUserStatus = signedInUser.getStatus();

            if (signedInUserStatus.equals(Status.Active)) {

                if (signedInUser.getRole().equals(roleService.findById(1L))) {
                    return "admin";
                }
                if (signedInUser.getRole().equals(roleService.findById(2L))) {

                    model.addAttribute("studentId", signedInUser.getId());
                    return "student";
                } else {

                    model.addAttribute("teacherId", signedInUser.getId());
                    return "teacher";

                }

            }
            if (signedInUserStatus.equals(Status.Inactive)) {
                ra.addAttribute("message" , 1);
                return "redirect:/signInMessage";
            } else {

                ra.addAttribute("message" , 2);
                return "redirect:/signInMessage";
            }

        } else {
            ra.addAttribute("message" , 3);
            return "redirect:/signInMessage";
        }

    }

    @RequestMapping("/signInMessage")
    public String signInMessage(Model model ,
                                @RequestParam("message") int message){

        model.addAttribute("message" , message);

        return "signInMessage";
    }

    @RequestMapping("/editAccount/{userId}")
    public String edit(Model model, @PathVariable("userId") Long userId) {

        User user = userService.findById(userId);

        EditAccountDto accountDto = new EditAccountDto();

        model.addAttribute("user", user);
        model.addAttribute("accountDto", accountDto);

        return "edit-account";
    }

    @RequestMapping("/editAccountError/{userId}")
    public String editError(Model model,
                            @RequestParam("errorNumber") int errorNumber,
                            @PathVariable("userId") Long userId) {

        model.addAttribute("errorNumber", errorNumber);
        model.addAttribute("userId", userId);

        return "edit-account-error";
    }

    @RequestMapping("/submitEditAccount/{userId}")
    public String edit(@PathVariable("userId") Long userId,
                       RedirectAttributes ra,
                       @ModelAttribute("accountDto") EditAccountDto accountDto) {

        User user = userService.findById(userId);

        if (accountDto.getFirstName() != "")
            user.setFirstName(accountDto.getFirstName());

        if (accountDto.getLastName() != "")
            user.setLastName(accountDto.getLastName());

        if (accountDto.getEmail() != "")
            user.setEmail(accountDto.getEmail());

        if (accountDto.getMobileNumber() != "") {

            if (accountDto.getMobileNumber().startsWith("0") && accountDto.getMobileNumber().charAt(1)=='9' && accountDto.getMobileNumber().length()==11)
                user.setMobileNumber(accountDto.getMobileNumber());
            else{
                ra.addAttribute("errorNumber" , 1);
            return "redirect:/editAccountError/{userId}";
            }
        }

        if (accountDto.getUsername() != "") {

            Character[] chars = {'!','@','#','$','%','^','&','*','(',')',':',';','"',',','/','~','`','+','=','<','>'};
            for (Character c : chars){
                    if (accountDto.getUsername().indexOf(c) != -1) {
                        ra.addAttribute("errorNumber" , 2);
                        return "redirect:/editAccountError/{userId}";
                    }
            }

            if (accountDto.getUsername().length()<6) {
                ra.addAttribute("errorNumber" , 3);
                return "redirect:/editAccountError/{userId}";
            }
            user.setUsername(accountDto.getUsername());
        }

        if (accountDto.getOldPassword() != "") {

            if (accountDto.getOldPassword().equals(user.getPassword())){

                if (accountDto.getNewPassword().equals(accountDto.getNewPasswordConfirm())){
                    if (accountDto.getNewPassword().length()<8){
                        ra.addAttribute("errorNumber" , 4);
                        return "redirect:/editAccountError/{userId}";
                    }else {
                        user.setPassword(accountDto.getNewPassword());
                    }
                }else {
                         ra.addAttribute("errorNumber" , 5);
                         return "redirect:/editAccountError/{userId}";
                }
            }else {
                ra.addAttribute("errorNumber" , 6);
                return "redirect:/editAccountError/{userId}";
            }
        }

        userService.saveUser(user);

        return "successful-edit-account";//TODO:add template

    }

}