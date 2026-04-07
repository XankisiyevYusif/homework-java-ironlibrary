package com.example.homework.repository;

import com.example.homework.model.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueRepository extends JpaRepository<Issue, Integer> {
    List<Issue> findByIssueStudent_Usn(String usn);
    List<Issue> findByReturnDate(String returnDate);
}
