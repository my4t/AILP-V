package com.ace.ailpv.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ace.ailpv.SecretConfigProperties;
import com.ace.ailpv.entity.Batch;
import com.ace.ailpv.entity.Course;
import com.ace.ailpv.entity.Resource;
import com.ace.ailpv.entity.User;
import com.ace.ailpv.entity.Video;
import com.ace.ailpv.service.BatchService;
import com.ace.ailpv.service.CourseService;
import com.ace.ailpv.service.ExamService;
import com.ace.ailpv.service.FileService;
import com.ace.ailpv.service.ResourceService;
import com.ace.ailpv.service.UserScheduleService;
import com.ace.ailpv.service.UserService;
import com.ace.ailpv.service.VideoService;

import ws.schild.jave.EncoderException;
import ws.schild.jave.InputFormatException;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SecretConfigProperties secretConfigProperties;

    @Autowired
    private CourseService courseService;

    @Autowired
    private FileService fileService;

    @Autowired
    private BatchService batchService;

    @Autowired
    private ExamService examService;

    @Autowired
    private UserService userService;

    @Autowired
    private VideoService videoService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private UserScheduleService userScheduleService;

    String path = "src\\main\\resources\\static\\courses\\";

    @GetMapping("/dashboard")
    public String setupDashborad(ModelMap model) {
        model.addAttribute("studentCount", userService.getUserCountByUserRole("ROLE_STUDENT"));
        model.addAttribute("teacherCount", userService.getUserCountByUserRole("ROLE_TEACHER"));
        model.addAttribute("batchCount", batchService.getBatchCount());
        model.addAttribute("courseCount", courseService.getCourseCount());
        return "/admin/ADM-DSB-01";
    }

    @GetMapping("/course-table")
    public String setupCourseTable(ModelMap model) {
        model.addAttribute("courseList", courseService.getAllCourses());
        model.addAttribute("course", new Course());
        return "/admin/ADM-CTB-02";
    }

    @PostMapping("/addCourse")
    public String addCourse(@ModelAttribute("course") Course course, RedirectAttributes redirectAttrs)
            throws IllegalStateException, IOException, InputFormatException, EncoderException {
        if (courseService.checkCourseName(course.getName())) {
            redirectAttrs.addFlashAttribute("errorMsg", true);
            return "redirect:/admin/course-table";
        }
        courseService.addCourse(course);
        return "redirect:/admin/course-table";
    }

    @PostMapping("/editCourse")
    public String editCourse(@RequestParam("id") String id, @RequestParam("name") String name,
            @RequestParam("fee") String fee, @RequestParam("desc") String description, RedirectAttributes redirectAttrs)
            throws IllegalStateException, IOException {

        String oldCourseName = courseService.getCourseById(Long.parseLong(id)).getName();

        Course updateCourse = new Course();
        updateCourse.setId(Long.parseLong(id));
        updateCourse.setName(name);
        updateCourse.setFee(Double.parseDouble(fee));
        updateCourse.setDescription(description);
        courseService.updateCourse(updateCourse);

        fileService.renameDir(oldCourseName, updateCourse.getName());

        return "redirect:/admin/course-table";
    }

    @GetMapping("/editVideo/{id}")
    public String editVideo(@PathVariable("id") Long id, ModelMap model) {
        Course course = courseService.getCourseById(id);
        List<Video> videoList = videoService.getVideoByCourseId(id);
        model.addAttribute("videoList", videoList);
        model.addAttribute("course", course);
        return "/admin/ADM-VTB-03";

    }

    @GetMapping("/editResource/{id}")
    public String editResource(@PathVariable("id") Long id, ModelMap model) {
        Course course = courseService.getCourseById(id);
        List<Resource> resourceList = resourceService.getResourceByCourseId(id);
        model.addAttribute("resourceList", resourceList);
        model.addAttribute("course", course);
        return "/admin/ADM-RTB-04";
    }

    @GetMapping("/deleteVideo/{vidId}/{courseName}")
    public String deleteVideo(@PathVariable("vidId") String vidId, @PathVariable("courseName") String courseName,
            ModelMap model) throws IOException {

        Video video = videoService.getVideoById(Long.parseLong(vidId));
        Course course = courseService.getCourseByName(courseName);
        fileService.deleteFile(path + courseName + "\\video\\" + video.getName());
        videoService.deleteVideoById(Long.parseLong(vidId));
        List<Video> videoList = videoService.getAllVideos();
        model.addAttribute("videoList", videoList);
        model.addAttribute("course", courseName);
        model.addAttribute("msg", "Deleted!!");
        return "redirect:/admin/editVideo/" + course.getId();
    }

    @GetMapping("/deleteResource/{resourceId}/{courseName}")
    public String deleteResource(@PathVariable("resourceId") String resourceId,
            @PathVariable("courseName") String courseName,
            ModelMap model) throws IOException {

        Resource resource = resourceService.getResourceById(Long.parseLong(resourceId));
        Course course = courseService.getCourseByName(courseName);
        fileService.deleteFile(path + courseName + "\\resource\\" + resource.getName());
        resourceService.deleteResourceById(Long.parseLong(resourceId));
        List<Resource> resourceList = resourceService.getAllResources();
        model.addAttribute("resourceList", resourceList);
        model.addAttribute("course", courseName);
        model.addAttribute("msg", "Deleted!!");
        return "redirect:/admin/editResource/" + course.getId();
    }

    @PostMapping("/uploadCourseVideo")
    public String uploadCourseVideo(ModelMap modal, @RequestParam("file") MultipartFile[] files,
            @RequestParam("courseId") String courseId)
            throws IllegalStateException, IOException, InputFormatException, EncoderException {
        String courseName = courseService.getCourseById(Long.parseLong(courseId)).getName();
        Course course = courseService.getCourseById(Long.parseLong(courseId));
        for (MultipartFile file : files) {
            fileService.createFile(file, courseName + "\\video");
            if (!videoService.isExistByVideoNameAndCourseId(file.getOriginalFilename(), Long.parseLong(courseId))) {
                Video newVideo = new Video();
                newVideo.setName(file.getOriginalFilename());
                newVideo.setVideoCourse(course);
                newVideo.setLength(videoService.getVideoLength(file));
                videoService.addVideo(newVideo);
            }
        }
        return "redirect:/admin/editVideo/" + courseId;
    }

    @PostMapping("/uploadCourseResource")
    public String uploadCourseResource(ModelMap modal, @RequestParam("file") MultipartFile[] files,
            @RequestParam("courseId") String courseId) throws IllegalStateException, IOException {
        String courseName = courseService.getCourseById(Long.parseLong(courseId)).getName();
        Course course = courseService.getCourseById(Long.parseLong(courseId));
        for (MultipartFile file : files) {
            fileService.createFile(file, courseName + "\\resource");
            if (!resourceService.isExistByResourceNameAndCourseId(file.getOriginalFilename(),
                    Long.parseLong(courseId))) {
                Resource newResource = new Resource();
                newResource.setName(file.getOriginalFilename());
                newResource.setResourceCourse(course);
                resourceService.addResource(newResource);
            }
        }
        return "redirect:/admin/editResource/" + courseId;
    }

    @GetMapping("/deleteCourse/{id}/{courseName}")
    public String deleteCourse(@PathVariable("id") Long id, @PathVariable("courseName") String courseName)
            throws IOException {
        courseService.deleteCourseById(id, courseName);
        return "redirect:/admin/course-table";
    }

    @GetMapping("/batch-table")
    public String setupBatchTable(ModelMap model) {
        model.addAttribute("courseList", courseService.getAllCourses());
        model.addAttribute("batch", new Batch());
        model.addAttribute("editBatch", new Batch());
        model.addAttribute("batchList", batchService.getAllBatches());
        return "/admin/ADM-BTB-05";
    }

    @PostMapping("/addBatch")
    public String addBatch(@ModelAttribute("batch") Batch batch) {
        batch.setIsActive(true);
        Course course = courseService.getCourseById(batch.getBatchCourse().getId());
        batchService.addBatch(batch, course.getName());
        return "redirect:/admin/batch-table";
    }

    @GetMapping("/deleteBatch/{id}")
    public String deleteBatch(@PathVariable("id") Long id) throws IOException {
        Batch batch = batchService.getBatchById(id);
        batchService.deleteBatchById(id, batch.getBatchCourse().getName());
        return "redirect:/admin/batch-table";
    }

    @GetMapping("/toggleDisableBatch/{id}")
    public String disableBatch(@PathVariable("id") Long id) {
        Batch batch = batchService.getBatchById(id);
        List<User> studentList = batch.getUserList().stream()
                .filter(user -> user.getRole().equals("ROLE_STUDENT"))
                .collect(Collectors.toList());

        if (batch.getIsActive()) {
            batch.setIsActive(false);
            for (User user : studentList) {
                boolean isEnable = true;
                if (user.getBatchList().size() == 1) {
                    isEnable = false;
                } else {
                    for (int i = 1; i <= user.getBatchList().size(); i++) {
                        if (user.getBatchList().get(i).getIsActive()) {
                            isEnable = true;
                            break;
                        } else {
                            isEnable = false;
                        }

                    }
                }

                if (!isEnable)
                    userService.doToggleAccountStatus(isEnable, user.getId());

            }

        } else {
            batch.setIsActive(true);

            for (User user : studentList) {
                if (!user.getEnabled())
                    userService.doToggleAccountStatus(true, user.getId());
            }
        }
        batchService.updateBatch(batch);
        return "redirect:/admin/batch-table";
    }

    @GetMapping("/editBatch/{id}")
    public String setupEditBatch(@PathVariable("id") Long id, ModelMap model) {
        Batch batch = batchService.getBatchById(id);
        List<Course> courseList = courseService.getAllCourses();
        model.addAttribute("courseList", courseList);
        model.addAttribute("batch", batch);
        return "/admin/ADM-EDB-06";
    }

    @PostMapping("/editBatch")
    public String editBatch(@ModelAttribute("batch") Batch batch, ModelMap model) {
        Batch resBatch = batchService.getBatchById(batch.getId());
        if (resBatch.getIsActive()) {
            batch.setIsActive(true);
        } else {
            batch.setIsActive(false);
        }
        batchService.updateBatch(batch);
        // model.addAttribute("successMsg", true);
        // return "/admin/ADM-EDB-11";
        return "redirect:/admin/batch-table";
    }

    @GetMapping("/student-table")
    public String setupStudentTable(ModelMap model) {
        List<User> studentList = userService.getAllStudents();
        for (User student : studentList) {
            Float attendancePercentage = userScheduleService.avgAttendaceOfStudent(student.getId()).floatValue();
            student.setAttendancePercentage(Math.round(attendancePercentage));
        }
        model.addAttribute("studentList", studentList);
        return "/admin/ADM-STB-07";
    }

    @GetMapping("/studentRegister")
    public String setupStudentRegister(ModelMap model) {
        model.addAttribute("batchList", batchService.getAllBatches());
        return "/admin/ADM-STG-08";
    }

    @GetMapping("/deleteStudent/{id}")
    public String deleteStudent(@PathVariable("id") String id)
            throws IOException {
        userService.deleteUserById(id);
        return "redirect:/admin/student-table";
    }

    @GetMapping("/editStudent/{id}")
    public String setupEditStudent(@PathVariable("id") String id, ModelMap model) {
        User student = userService.getUserById(id);
        model.addAttribute("batchList", batchService.getAllBatches());
        model.addAttribute("student", student);
        return "/admin/ADM-EDS-09";
    }

    @PostMapping("/editStudent")
    public String editStudent(@ModelAttribute("student") User student) {
        User resStudent = userService.getUserById(student.getId());
        student.setEnabled(resStudent.getEnabled());
        student.setPassword(resStudent.getPassword());
        student.setProfile_pic(resStudent.getProfile_pic());
        userService.addUser(student);

        return "redirect:/admin/student-table";
    }

    @GetMapping("/deleteExam/{id}")
    public String deleteExam(@PathVariable("id") Long id)
            throws IOException {
        examService.deleteExamById(id);
        return "redirect:/admin/exam-table";
    }

    @GetMapping("/teacher-table")
    public String setupTeacherTable(ModelMap model) {
        model.addAttribute("teacherList", userService.getAllTeachers());
        return "/admin/ADM-TTB-10";
    }

    @GetMapping("/teacherRegister")
    public String setupTeacherRegister(ModelMap model) {
        model.addAttribute("batchList", batchService.getAllBatches());
        return "/admin/ADM-TTG-11";
    }

    @GetMapping("/deleteTeacher/{id}")
    public String deleteTeacher(@PathVariable("id") String id) {
        userService.deleteUserById(id);
        return "redirect:/admin/teacher-table";
    }

    @GetMapping("/editTeacher/{id}")
    public String setupEditTeacher(@PathVariable("id") String id, ModelMap model) {
        User teacher = userService.getUserById(id);
        model.addAttribute("batchList", batchService.getAllBatches());
        model.addAttribute("teacher", teacher);
        return "/admin/ADM-EDT-12.html";
    }

    @PostMapping("/editTeacher")
    public String editTeacher(@ModelAttribute("teacher") User teacher) {
        User resTeacher = userService.getUserById(teacher.getId());
        teacher.setEnabled(resTeacher.getEnabled());
        teacher.setPassword(resTeacher.getPassword());
        teacher.setProfile_pic(resTeacher.getProfile_pic());
        userService.addUser(teacher);
        return "redirect:/admin/teacher-table";
    }

    @GetMapping("/exam-table")
    public String setupExamTable(ModelMap model) {
        model.addAttribute("examList", examService.getAllExams());
        return "/admin/ADM-ETB-13";
    }

    @GetMapping("/create-exam")
    public String setupCreateExam(ModelMap model) {
        model.addAttribute("courseList", courseService.getAllCourses());
        return "/admin/ADM-CRE-14";
    }

    @GetMapping("/changePassword/{userId}")
    public String changePassword(@PathVariable("userId") String userId) {

        User user = userService.getUserById(userId);
        if (user.getRole().equals("ROLE_STUDENT")) {
            user.setPassword(secretConfigProperties.getDefaultStdPassword());
            userService.updatePasswordByUserId(passwordEncoder.encode(user.getPassword()), userId);
            return "redirect:/admin/student-table";
        } else if (user.getRole().equals("ROLE_TEACHER")) {
            user.setPassword(secretConfigProperties.getDefaultTchPassword());
            userService.updatePasswordByUserId(passwordEncoder.encode(user.getPassword()), userId);
            return "redirect:/admin/teacher-table";
        }
        return "redirect:/admin/dashboard";
    }

    // to delete after admin account created
    @GetMapping("/register")
    public String setupRegister() {
        return "/admin/ADM-REG-16";
    }

    @PostMapping("/register")
    public String setupRegister(@RequestParam("id") String id, @RequestParam("name") String name,
            @RequestParam("password") String password, @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("pin") String pin, RedirectAttributes redirectAttr) {
        if (password.equals(confirmPassword)) {

            if (pin.equals(secretConfigProperties.getDefaultPin())) {
                System.out.println("equal pin");
                User user = new User();
                user.setId(id);
                user.setName(name);
                user.setPassword(passwordEncoder.encode(password));
                user.setEnabled(true);
                user.setRole("ROLE_ADMIN");
                user.setProfile_pic("profile.png");
                userService.addUser(user);
                return "redirect:/auth/login";
            } else {
                redirectAttr.addFlashAttribute("errorMsg", "Invalid Pin!");
            }
        } else {
            redirectAttr.addFlashAttribute("errorMsg", "Unmatch Password!");
        }

        return "redirect:/admin/register";
    }

}