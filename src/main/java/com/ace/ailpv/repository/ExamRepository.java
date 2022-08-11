package com.ace.ailpv.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ace.ailpv.entity.Exam;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    // List<Exam> getAllExams();
    // void deleteExamById(Long id);
}