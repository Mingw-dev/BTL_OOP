package com.example.ilms.service;

import com.example.ilms.dto.common.PageResponse;
import com.example.ilms.dto.course.CourseCreateRequestDTO;
import com.example.ilms.dto.course.CourseResponseDTO;
import com.example.ilms.entity.Course;
import com.example.ilms.entity.User;
import com.example.ilms.exception.AppException;
import com.example.ilms.exception.ErrorCode;
import com.example.ilms.repository.CourseRepository;
import com.example.ilms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public void createCourse(CourseCreateRequestDTO request, User owner) {

        Course course = modelMapper.map(request, Course.class);

        String randomCode = UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();

        course.setCourseCode(randomCode);

        course.setOwner(owner);

        courseRepository.save(course);
    }

    public PageResponse<CourseResponseDTO> getCourses(
            String search,
            Boolean status,
            boolean isArchived,
            int page,
            int size,
            User currentUser) {

        Boolean effectiveStatus = isArchived ? Boolean.FALSE : status;

        boolean isAdmin = currentUser.getRole() != null
                && "ADMIN".equalsIgnoreCase(currentUser.getRole().getCode());

        String visibility = isAdmin ? null : "PUBLIC";

        int limit = size;
        int offset = page * size;

        List<Course> courses = courseRepository.findCoursesWithFilters(
                search, visibility, effectiveStatus, limit, offset);

        long totalElements = courseRepository.countCoursesWithFilters(
                search, visibility, effectiveStatus);

        if (search != null && !search.isBlank() && courses.isEmpty()) {
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }

        int totalPages = (int) Math.ceil((double) totalElements / size);
        boolean isLast = (page + 1) >= totalPages;

        List<CourseResponseDTO> items = courses.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        return PageResponse.<CourseResponseDTO>builder()
                .items(items)
                .pageNumber(page)
                .pageSize(size)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .isLast(isLast)
                .build();
    }


    private CourseResponseDTO toResponseDTO(Course course) {
        CourseResponseDTO dto = modelMapper.map(course, CourseResponseDTO.class);
        if (course.getOwner() != null) {
            dto.setOwnerId(course.getOwner().getId());
        }
        return dto;
    }
}