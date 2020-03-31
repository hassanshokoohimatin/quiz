package quiz.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import quiz.dto.SearchDto;
import quiz.model.Course;
import quiz.model.User;
import quiz.model.enums.Status;
import quiz.services.CourseService;
import quiz.services.RoleService;
import quiz.services.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private CourseService courseService;

    @RequestMapping(value = "/listWaitingUsers")
    public String listWaitingUsers(Model model){

        List<User> allWaitingUsers = userService.findUsersByStatus(Status.Waiting);
        model.addAttribute("allWaitingUsers" , allWaitingUsers);
        return "list-waiting-users";
    }

    @RequestMapping("/backToAdmin")
    public String back(){
        return "admin";
    }

    @RequestMapping("/backToSignIn")
    public String backToSignIn(){
        return "index";
    }

    @RequestMapping(value = "/activateUser")
    public String activateUser(@RequestParam ("id") Long id){

        User userToModifyStatus = userService.findById(id);
        userToModifyStatus.setStatus(Status.Active);
        userService.saveUser(userToModifyStatus);
        return "redirect:/admin/listWaitingUsers";
    }

    @RequestMapping(value = "/inactivateUser")
    public String inactivateUser(@RequestParam ("id") Long id){

        User userToModifyStatus = userService.findById(id);
        userToModifyStatus.setStatus(Status.Inactive);
        userService.saveUser(userToModifyStatus);
        return "redirect:/admin/listWaitingUsers";
    }

    @RequestMapping(value = "/courses")
    public String courses(Model model){

        List<Course> allCourses = courseService.findAllCourses();

        model.addAttribute("allCourses" , allCourses);

        return "list-all-courses";
    }

    @RequestMapping(value = "/listStudentsOfACourse")
    public String listStudentsOfACourse(Model model , @RequestParam("id") Long id){

        Course course = courseService.findCourseById(id);
        List<User> students = course.getStudents();

        model.addAttribute("course" , course);
        model.addAttribute("students" , students);

        return "list-students-of-a-course";
    }

    @RequestMapping(value = "/addNewCourse" , method = RequestMethod.GET)
    public String addNewCourse(Model model){

        Course newCourse = new Course();
        model.addAttribute("newCourse" , newCourse);

        return "add-new-course";
    }

    @RequestMapping(value = "/addNewCourse" , method = RequestMethod.POST)
    public String addCourse(Model model , @ModelAttribute Course newCourse){

        courseService.saveCourse(newCourse);

        model.addAttribute("course" , newCourse);
        model.addAttribute("allTeachers" , userService.findUsersByRole(roleService.findById(3L)));

        return "add-members-to-course";
    }

    @RequestMapping(value = "/addTeacherToCourse")
    public String addTeacherToCourse(Model model ,
                                     @RequestParam("teacherId") Long teacherId ,
                                     @RequestParam("courseId") Long courseId){

        Course course = courseService.findCourseById(courseId);

        course.setTeacher(userService.findById(teacherId));
        courseService.saveCourse(course);

        List<User> allActiveStudents = userService.findUsersByRole(roleService.findById(2L)).stream()
                .filter(user -> user.getStatus().equals(Status.Active))
                .collect(Collectors.toList());

        model.addAttribute("course" , course);
        model.addAttribute("allActiveStudents" , allActiveStudents);

        return "add-students-to-new-course";

    }

    @RequestMapping(value = "/addStudentToNewCourse")
    public String addStudentToNewCourse(Model model ,
                                     @RequestParam("studentId") Long studentId ,
                                     @RequestParam("courseId") Long courseId){

        User student = userService.findById(studentId);
        Course course = courseService.findCourseById(courseId);

        course.getStudents().add(student);
        courseService.saveCourse(course);

        List<User> allActiveStudents = userService.findUsersByRole(roleService.findById(2L)).stream()
                .filter(user -> user.getStatus().equals(Status.Active))
                .collect(Collectors.toList());

        model.addAttribute("course" , course);
        model.addAttribute("allActiveStudents" , allActiveStudents);

        return "add-students-to-new-course";

    }

    @RequestMapping(value = "/addStudentToCourse")
    public String addStudentToCourse(Model model , @RequestParam("courseId") Long courseId){

        Course course = courseService.findCourseById(courseId);

        List<User> allActiveStudents = userService.findUsersByRole(roleService.findById(2L)).stream()
                .filter(user -> user.getStatus().equals(Status.Active))
                .collect(Collectors.toList());

        model.addAttribute("allActiveStudents" , allActiveStudents);
        model.addAttribute("course" , course);

        return "add-students-to-course";

    }

    @RequestMapping(value = "/addStudentToCourse2")
    public String addStudentToCourse2(Model model ,
                                        @RequestParam("studentId") Long studentId ,
                                        @RequestParam("courseId") Long courseId){

        User student = userService.findById(studentId);
        Course course = courseService.findCourseById(courseId);

        course.getStudents().add(student);
        courseService.saveCourse(course);

        List<User> allActiveStudents = userService.findUsersByRole(roleService.findById(2L)).stream()
                .filter(user -> user.getStatus().equals(Status.Active))
                .collect(Collectors.toList());

        model.addAttribute("course" , course);
        model.addAttribute("allActiveStudents" , allActiveStudents);

        return "add-students-to-course";

    }

    @RequestMapping(value = "/searchUsers")
    public String searchUsers(Model model){

        SearchDto searchDto = new SearchDto();

        model.addAttribute("searchDto" , searchDto);
        model.addAttribute("allRoles" , roleService.allRoles());

        return "search-users";
    }

    @RequestMapping(value = "/submitSearch" , method = RequestMethod.GET)
    public String submitSearch(Model model , @ModelAttribute SearchDto searchDto) {

        String firstName = searchDto.getFirstName();
        String lastName = searchDto.getLastName();

        List<User> users = userService.findAll().stream().filter(user -> user.getStatus().equals(Status.Active))
                .filter(user -> user.getRole().getRoleType().toString().equals(searchDto.getRole()))
                .collect(Collectors.toList());

        List<User> searchUserResult = new ArrayList<>();

            if (firstName.isEmpty() && lastName.isEmpty()){
                searchUserResult = users;

                model.addAttribute("searchUserResult" , searchUserResult);
                return "search-users-result";
            }
            if ( !firstName.isEmpty() && !lastName.isEmpty() ){
                searchUserResult = users.stream().filter(user -> user.getLastName().equals(lastName))
                        .filter(user -> user.getFirstName().equals(firstName))
                        .collect(Collectors.toList());

                model.addAttribute("searchUserResult" , searchUserResult);
                return "search-users-result";
            }
            if (firstName.isEmpty() && !lastName.isEmpty()){
                searchUserResult = users.stream().filter(user -> user.getLastName().equals(lastName))
                        .collect(Collectors.toList());

                model.addAttribute("searchUserResult" , searchUserResult);
                return "search-users-result";
            }
            else {
                searchUserResult = users.stream().filter(user -> user.getFirstName().equals(firstName))
                        .collect(Collectors.toList());

                model.addAttribute("searchUserResult" , searchUserResult);
                return "search-users-result";
            }

    }

    @RequestMapping(value = "/editUser")
    public String editUser(@RequestParam("id") Long id){

        //TODO : complete this action
        return "";
    }

    @RequestMapping(value = "/inActivateUser")
    public String inActivateUser(Model model ,
                                 @RequestParam("id") Long id){

        User user = userService.findById(id);
        user.setStatus(Status.Inactive);
        userService.saveUser(user);

        SearchDto searchDto = new SearchDto();

        model.addAttribute("searchDto" , searchDto);
        model.addAttribute("allRoles" , roleService.allRoles());

        return "search-users";
    }

}
