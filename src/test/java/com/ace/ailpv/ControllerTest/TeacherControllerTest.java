package com.ace.ailpv.ControllerTest;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ace.ailpv.entity.Assignment;
import com.ace.ailpv.entity.AssignmentAnswer;
import com.ace.ailpv.entity.Batch;
import com.ace.ailpv.entity.BatchHasExam;
import com.ace.ailpv.entity.Course;
import com.ace.ailpv.entity.Exam;
import com.ace.ailpv.entity.User;
import com.ace.ailpv.service.AssignmentAnswerService;
import com.ace.ailpv.service.AssignmentResultService;
import com.ace.ailpv.service.AssignmentService;
import com.ace.ailpv.service.BatchHasExamService;
import com.ace.ailpv.service.ExamService;
import com.ace.ailpv.service.UserScheduleService;
import com.ace.ailpv.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc
public class TeacherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    HttpSession httpSession;

    @MockBean
    UserService userService;

    @MockBean
    AssignmentAnswerService assignmentAnswerService;

    @MockBean
    AssignmentService assignmentService;

    @MockBean
    ExamService examService;

    @MockBean
    BatchHasExamService batchHasExamService;

    @MockBean
    AssignmentResultService assignmentResultService;

    @MockBean
    UserScheduleService userScheduleService;

    String apiPath = "/teacher";

    @Test
    public void setupTeacherDashboardTest() throws Exception{
        //String teacherId = "tch001";
        HashMap<String, Object> sessionattr = new HashMap<String, Object>(); 
        sessionattr.put("teacherId", "tch001");
        List<Batch> batchList = getBatchList();
        when(userService.getTeacherBatchListById("tch001")).thenReturn(batchList);
        
        this.mockMvc.perform(get("/teacher/dashboard").sessionAttrs(sessionattr))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("batchList"))
                .andExpect(view().name("/teacher/TCH-DSB-01"));                                             
    }

    // @Test
    // public void setupTeacherPublicChatTest() throws Exception{
    //     String teacherId = "tch001";
    //     HashMap<String, Object> sessionattr = new HashMap<String, Object>(); 
    //     sessionattr.put("teacherId", teacherId);
    //     User teacherInfo = getTeacher();
    //     List<Batch> batchList = getBatchList();
    //     batchList = batchList.stream().filter(batch -> batch.getIsActive()).collect(Collectors.toList());
    //     Batch firstBatch = batchList.get(0);
    //     when(userService.getUserById(teacherId)).thenReturn(teacherInfo);
    //     when(userService.getTeacherBatchListById(teacherId)).thenReturn(batchList);

    //     this.mockMvc.perform(get("/teacher/teacher-public-chat").sessionAttrs(sessionattr))
    //             .andExpect(status().isOk())
    //             .andExpect(model().attribute("userId", teacherInfo.getId()))
    //             .andExpect(model().attribute("username", teacherInfo.getName()))
    //             .andExpect(model().attribute("batchId", firstBatch.getId()))
    //             .andExpect(model().attribute("batchName", firstBatch.getName()))
    //             .andExpect(model().attribute("batchList", batchList))
    //             .andExpect(view().name("/teacher/TCH-PBC-05"));

    // }

    // @Test
    // public void setupChatWithBatchTest() throws Exception{
    //     String teacherId = "tch001";
    //     HashMap<String, Object> sessionattr = new HashMap<String, Object>(); 
    //     sessionattr.put("teacherId", teacherId);
    //     User teacherInfo = getTeacher();
    //     List<Batch> batchList = getBatchList();
    //     when(userService.getUserById(teacherId)).thenReturn(teacherInfo);
    //     when(userService.getTeacherBatchListById(teacherInfo.getId())).thenReturn(batchList);

    //     this.mockMvc.perform(get("/teacher/chatWithBatch/{batchId}/{batchName}", 1L, "java")
    //             .sessionAttrs(sessionattr))
    //             .andExpect(status().isOk())
    //             .andExpect(model().attributeExists("userId"))
    //             .andExpect(model().attributeExists("userName"))
    //             .andExpect(model().attributeExists("batchList"))
    //             .andExpect(view().name("/teacher/TCH-PBC-05"));

    // }

    @Test
    public void modifyAttendanceTest() throws Exception{
        String teacherId = "tch001";
        HashMap<String, Object> sessionattr = new HashMap<String, Object>(); 
        sessionattr.put("teacherId", teacherId); 
        List<Batch> batchList = getBatchList();
        when(userService.getTeacherBatchListById(teacherId)).thenReturn(batchList);

        this.mockMvc.perform(get("/teacher/modifyAttendance").sessionAttrs(sessionattr))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("batchList"))
                .andExpect(view().name("/teacher/TCH-MDA-12"));
    }

    @Test
    public void setupAttendanceTableTest() throws Exception{
        String teacherId = "tch001";
        HashMap<String, Object> sessionattr = new HashMap<String, Object>(); 
        sessionattr.put("teacherId", teacherId);
        List<Batch> batchList = getBatchList();
        when(userService.getTeacherBatchListById(teacherId)).thenReturn(batchList);

        this.mockMvc.perform(get("/teacher/attendance-table").sessionAttrs(sessionattr))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("batchList"))
                .andExpect(model().attributeExists("data"))
                .andExpect(view().name("/teacher/TCH-ATB-11"));
    }

    // @Test
    // public void searchUserScheduleListDateNotNullTest(){
    //     UserSchedule userSchedule = new UserSchedule();
    // }

    // @Test
    // public void searchUserScheduleListDateEqualNullTest() throws Exception{
    //     String teacherId = "tch001";
    //     HashMap<String, Object> sessionattr = new HashMap<String, Object>(); 
    //     sessionattr.put("teacherId", teacherId);

    //     UserSchedule userSchedule = getUserSchedule();
    //     List<UserSchedule> scheduleList = new ArrayList<>();
    //     scheduleList.add(userSchedule);

    //     List<Batch> batchList = getBatchList();
    //     when(userScheduleService.getUserScheduleListByBatchIdOrScheduleId(1L)).thenReturn(scheduleList);
    //     when(userService.getTeacherBatchListById(teacherId)).thenReturn(batchList);

    //     this.mockMvc.perform(post("/teacher/searchUserScheduleList").sessionAttrs(sessionattr))
    //             .andExpect(status().isOk())
    //             .andExpect(model().attributeExists("scheduleList"))
    //             .andExpect(model().attributeExists("batchList"))
    //             .andExpect(model().attributeExists("data"))
    //             .andExpect(view().name("/teacher/TCH-ATB-08"));
    // }

    @Test
    public void setupPostResourceTest() throws Exception{
        String teacherId = "tch001";
        HashMap<String, Object> sessionattr = new HashMap<String, Object>(); 
        sessionattr.put("teacherId", teacherId); 
        List<Batch> batchList = getBatchList();
        when(userService.getTeacherBatchListById(teacherId)).thenReturn(batchList);

        this.mockMvc.perform(get("/teacher/postResource").sessionAttrs(sessionattr))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("batchList"))
                .andExpect(view().name("/teacher/TCH-UPR-02"));
    }

    @Test
    public void setupPostVideoTest() throws Exception{
        String teacherId = "tch001";
        HashMap<String, Object> sessionattr = new HashMap<String, Object>(); 
        sessionattr.put("teacherId", teacherId); 
        List<Batch> batchList = getBatchList();
        when(userService.getTeacherBatchListById(teacherId)).thenReturn(batchList);

        this.mockMvc.perform(get("/teacher/postVideo").sessionAttrs(sessionattr))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("batchList"))
                .andExpect(view().name("/teacher/TCH-UPV-03"));
    }

    @Test
    public void setupAssignmentTableTest() throws Exception{
        String teacherId = "tch001";
        HashMap<String, Object> sessionattr = new HashMap<String, Object>(); 
        sessionattr.put("teacherId", teacherId); 
        List<Batch> batchList = getBatchList();
        when(userService.getTeacherBatchListById(teacherId)).thenReturn(batchList);

        this.mockMvc.perform(get("/teacher/assignment-table").sessionAttrs(sessionattr))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("batchList"))
                .andExpect(view().name("/teacher/TCH-AST-05"));

    }

    @Test
    public void createAssignmentTest() throws Exception{
        Assignment assignment = getAssignment();
        doNothing().when(assignmentService).addAssignment(assignment);
        this.mockMvc.perform(post("/teacher/createAssignment"))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/teacher/assignment-table"));
    }

    @Test
    public void checkAssignmentTest() throws Exception{
        Long assignmentId = 1L;
        List<AssignmentAnswer> answerList = getAssignmentAnswerList();
        when(assignmentAnswerService.getAssignmentAnswerListByAssignmentId(assignmentId)).thenReturn(answerList);
        this.mockMvc.perform(get("/teacher/checkAssignment/{assignmentId}", assignmentId))
                .andExpect(status().isOk())
                .andExpect(model().attribute("answerList", answerList))
                .andExpect(view().name("/teacher/TCH-ASD-06"));
    }

    // @Test
    // public void giveAssignmentResultTest() throws Exception{
    //     AssignmentAnswer asgmAnswer = new AssignmentAnswer();
    //     asgmAnswer.setId(1L);
    //     asgmAnswer.setAssignment(getAssignment());
    //     asgmAnswer.setAssignemntAnswerResult(new AssignmentResult());
    //     asgmAnswer.setAnswerStudent(getUser());
    //     asgmAnswer.setAnswerFileName("answerFileName");
    //     asgmAnswer.setIsGraded(true);

    //     AssignmentResult asgmResult = new AssignmentResult();
    //     asgmResult.setId(1L);
    //     asgmResult.setMark("mark");
    //     asgmResult.setComment("comment");
    //     asgmResult.setAssignmentResultAnswer(asgmAnswer);
    //     asgmResult.setAssignmentResultTeacher(getTeacher());

    //     when(assignmentResultService.getAssignmentResultByAnswerId(asgmResult.getId())).thenReturn(asgmResult);
    //     when(assignmentAnswerService.getAssignmentAnswerById(asgmResult.getId())).thenReturn(asgmAnswer);

    //     this.mockMvc.perform(post("/teacher/giveAssignmentResult"))
    //             .andExpect(status().is(302))
    //             .andExpect(view().name("/teacher/assignment-table"));
    // }

    // @Test
    // public void showStudentTableTest() throws Exception{
    //     String teacherId = "tch001";
    //     String batchId = "1L";
    //     HashMap<String, Object> sessionattr = new HashMap<String, Object>(); 
    //     sessionattr.put("teacherId", teacherId); 
    //     sessionattr.put("batchId", batchId);
        
    //     List<Assignment> assignmentList = getAssignmentList();
    //     List<User> studentList = getUserList();
    //     List<Batch> batchList = getBatchList();
        
    //     when(assignmentService.getAllAssignmentByBatchId(Long.parseLong(batchId))).thenReturn(assignmentList);
    //     when(userService.getStudentListByBatchId(Long.parseLong(batchId))).thenReturn(studentList);
    //     when(userService.getTeacherBatchListById(teacherId)).thenReturn(batchList);

    //     // this.mockMvc.perform(get("/teacher/assignment-grade").sessionAttrs(sessionattr))
    //     //         .andExpect(status().isOk())
    //     //         .andExpect(model().attributeExists("assignmentList"))
    //     //         .andExpect(model().attributeExists("studentList"))
    //     //         .andExpect(model().attributeExists("batchList"))
    //     //         .andExpect(view().name("/teacher/TCH-ASG-00"));
    // }

    @Test
    public void searchStudentsByBatchTest() throws Exception{
        Batch batch = getBatch();
        String teacherId = "tch001";
        HashMap<String, Object> sessionattr = new HashMap<String, Object>(); 
        sessionattr.put("teacherId", teacherId);
        List<Assignment> assignmentList = getAssignmentList();
        List<User> studentList = getUserList();
        List<Batch> batchList = getBatchList();
        when(assignmentService.getAllAssignmentByBatchId(batch.getId())).thenReturn(assignmentList);
        when(userService.getStudentListByBatchId(batch.getId())).thenReturn(studentList);
        when(userService.getTeacherBatchListById(teacherId)).thenReturn(batchList);

        this.mockMvc.perform(post("/teacher/searchStudentsByBatch").sessionAttrs(sessionattr))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("data"))
                .andExpect(model().attributeExists("assignmentList"))
                .andExpect(model().attributeExists("studentList"))
                .andExpect(model().attributeExists("batchList"))
                .andExpect(view().name("/teacher/TCH-ASG-07"));

    }

    @Test
    public void setupUploadExamTest() throws Exception{
        String teacherId = "tch001";
        HashMap<String, Object> sessionattr = new HashMap<String, Object>(); 
        sessionattr.put("teacherId", teacherId); 
        List<Exam> examList = new ArrayList<>();
        Exam exam = new Exam();
        Exam exam1 = new Exam();
        examList.add(exam);
        examList.add(exam1);
        when(examService.getExamListByTeacherId(teacherId)).thenReturn(examList);

        this.mockMvc.perform(get("/teacher/exam-table").sessionAttrs(sessionattr))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("examList"))
                .andExpect(view().name("/teacher/TCH-ETB-08"));
    }

    @Test
    public void UploadExamTest() throws Exception{
        String teacherId = "tch001";
        HashMap<String, Object> sessionattr = new HashMap<String, Object>(); 
        sessionattr.put("teacherId", teacherId);
        Exam exam = new Exam();
        exam.setId(1L);
        exam.setExamCourse(examCourse());
        Long courseId = exam.getExamCourse().getId();
        List<Batch> batchList = getBatchList();
        when(examService.getExamById(exam.getId())).thenReturn(exam);
        when(userService.getTeacherBatchListByTeacherIdAndCourseId(teacherId, courseId)).thenReturn(batchList);

        this.mockMvc.perform(get("/teacher/uploadExam/{examId}", "1").sessionAttrs(sessionattr))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("exam"))
                .andExpect(model().attributeExists("batchList"))
                .andExpect(view().name("/teacher/TCH-UPE-09"));
    }

    // @Test
    // public void uploadExamPostMethodTest() throws Exception{
    //     Exam exam = new Exam();
    //     exam.setId(1L);
    //     exam.setName("name");
    //     Batch batch = new Batch();
    //     batch.setId(1L);
    //     batch.setName("name");
    //     BatchHasExam bhe =  new BatchHasExam();
    //     bhe.setId(1L);
    //     bhe.setBheExam(exam);
    //     bhe.setBheBatch(batch);
    //     bhe.setStartDateTime(LocalDateTime.now());
    //     bhe.setEndDateTime(LocalDateTime.now());
    //     bhe.setTotalTime("totalTime");
    //     when(batchHasExamService.getBatchHasExamByExamIdAndBatchId(1L, 1L)).thenReturn(bhe);

    //     this.mockMvc.perform(post("/teacher/uploadExam"))
    //             .andExpect(status().is(302))
    //             .andExpect(flash().attributeExists("isSuccess"))
    //             .andExpect(redirectedUrl("/teacher/uploadExam/"+bhe.getBheExam().getId()));

    // }

    @Test
    public void setupExamGradeTest() throws Exception{
        String teacherId = "tch001";
        HashMap<String, Object> sessionattr = new HashMap<String, Object>(); 
        sessionattr.put("teacherId", teacherId);
        sessionattr.put("batchId", "1");
        Long batchId = Long.parseLong("1");
        List<BatchHasExam> bheList = new ArrayList<>();
        List<User> studentList = getUserList();
        List<Batch> batchList = getBatchList();
        when(batchHasExamService.getBatchHasExamListByBatchId(batchId)).thenReturn(bheList);
        when(userService.getStudentListByBatchId(batchId)).thenReturn(studentList);
        when(userService.getTeacherBatchListById(teacherId)).thenReturn(batchList);

        this.mockMvc.perform(get("/teacher/exam-grade").sessionAttrs(sessionattr))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("bheList"))
                .andExpect(model().attributeExists("studentList"))
                .andExpect(model().attributeExists("batchList"))
                .andExpect(view().name("/teacher/TCH-EXG-10"));
    }

    @Test
    public void searchStudentExamsByBatchTest() throws Exception{
        Batch batch = getBatch();
        String teacherId = "tch001";
        HashMap<String, Object> sessionattr = new HashMap<String, Object>(); 
        sessionattr.put("teacherId", teacherId);
        List<BatchHasExam> bheList = batch.getBatchHasExamList();
        List<User> studentList = getUserList();
        List<Batch> batchList = getBatchList();
        when(batchHasExamService.getBatchHasExamListByBatchId(batch.getId())).thenReturn(bheList);
        when(userService.getStudentListByBatchId(batch.getId())).thenReturn(studentList);
        when(userService.getTeacherBatchListById(teacherId)).thenReturn(batchList);

        this.mockMvc.perform(post("/teacher/searchStudentExamsByBatch").sessionAttrs(sessionattr))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("bheList"))
                .andExpect(model().attributeExists("studentList"))
                .andExpect(model().attributeExists("batchList"))
                .andExpect(view().name("/teacher/TCH-EXG-10"));
    }
    


    private Course getCourse() {
        Course course1 = new Course();
        course1.setId(1L);
        course1.setName("java");
        course1.setFee(20000.00);
        course1.setDescription("create course");

        return course1;
    }

    private Batch getBatch(){
        List<BatchHasExam> bheList = new ArrayList<>();
        Batch batch = new Batch();
        batch.setId(1L);
        batch.setName("java batch 01");
        batch.setBatchCourse(getCourse());
        batch.setIsActive(true);
        batch.setStartDate(LocalDate.of(2022, 9, 06));
        batch.setEndDate(LocalDate.of(2022, 9, 22));
        batch.setBatchHasExamList(bheList);
        return batch;
    }

    private List<Batch> getBatchList(){
        List<Batch> batchList = new ArrayList<>();
        Batch batch1 = getBatch();
        batchList.add(batch1);
        return batchList;
    }

    // private BatchHasExam getBatchExam(){
    //     BatchHasExam batchExam = new BatchHasExam();
    //     Exam exam = new Exam();
    //     exam.setId(1L);
    //     exam.setExamCourse(examCourse());
    //     exam.setName("name");
    //     Batch batch = getBatch();
    //     batchExam.setBheExam(exam);
    //     batchExam.setBheBatch(batch);
    //     batchExam.setStartDateTime(LocalDateTime.now());
    //     batchExam.setEndDateTime(LocalDateTime.now());
    //     return batchExam;
    // }

    // private User getTeacher(){
    //     User user = new User();
    //     user.setId("tch001");
    //     user.setName("teacher");
    //     user.setPassword("password");
    //     user.setRole("ROLE_TEACHER");
    //     user.setProfile_pic("profile.png");
    //     user.setBatchList(getBatchList());
    //     return user;
    // }

    private Assignment getAssignment(){
        Assignment assignment = new Assignment();
        assignment.setId(1L);
        assignment.setName("hello");
        assignment.setDueDate(LocalDateTime.now().toString());
        assignment.setQuestionFileName("hello.zip");
        return assignment;
    }

    private List<Assignment> getAssignmentList(){
        List<Assignment> assignmentList = new ArrayList<>();
        assignmentList.add(getAssignment());
        assignmentList.add(getAssignment());
        return assignmentList;
    }

    private List<AssignmentAnswer> getAssignmentAnswerList(){
        List<AssignmentAnswer> asgmAnswerList = new ArrayList<>();
        AssignmentAnswer answer = new AssignmentAnswer();
        answer.setId(1L);
        answer.setAnswerFileName("answerFileName");
        asgmAnswerList.add(answer);

        return asgmAnswerList;
    }

    private User getUser(){
        User user = new User();
        user.setId("sut001");
        user.setName("student");
        user.setPassword("password");
        user.setRole("ROLE_STUDENT");
        user.setProfile_pic("profile.png");
        return user;
    }

    // private UserSchedule getUserSchedule(){
    //     UserSchedule userSchedule = new UserSchedule();
    //     userSchedule.setId(1L);
    //     userSchedule.setSchedule(new Schedule());
    //     userSchedule.setStatus("status");
    //     userSchedule.setStudentId("studentId");
    //     userSchedule.setDate(LocalDate.now());
    //     return userSchedule;
    // }

    private List<User> getUserList(){
        List<User> userList = new ArrayList<>();
        User u1 = getUser();
        User u2 = getUser();
        userList.add(u1);
        userList.add(u2);
        return userList;
    }

    private Course examCourse(){
        Course course = new Course();
        course.setId(1L);
        course.setName("Java");
        return course;
    }

    
}
