package com.example.ilms.service;

import com.example.ilms.dto.course.CourseCreateRequestDTO;
import com.example.ilms.dto.course.CourseResponseDTO;
import com.example.ilms.entity.Course;
import com.example.ilms.entity.User;
import com.example.ilms.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public CourseResponseDTO createCourse(CourseCreateRequestDTO request, User currentUser) {
        Course course = modelMapper.map(request, Course.class);

        String randomCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        course.setCourseCode(randomCode);

        course.setOwner(currentUser);


        Course savedCourse = courseRepository.save(course);

        CourseResponseDTO responseDTO = modelMapper.map(savedCourse, CourseResponseDTO.class);

        if (savedCourse.getOwner() != null) {
            responseDTO.setOwnerId(savedCourse.getOwner().getId());
        }

        return responseDTO;
    }
}