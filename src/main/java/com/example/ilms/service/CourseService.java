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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt")
                .and(Sort.by(Sort.Direction.ASC, "name"));
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Course> coursePage = courseRepository.findCoursesWithFilters(
                search, visibility, effectiveStatus, pageable);

        if (search != null && !search.isBlank() && coursePage.isEmpty()) {
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }

        List<CourseResponseDTO> items = coursePage.getContent().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());

        return PageResponse.<CourseResponseDTO>builder()
                .items(items)
                .pageNumber(coursePage.getNumber())
                .pageSize(coursePage.getSize())
                .totalElements(coursePage.getTotalElements())
                .totalPages(coursePage.getTotalPages())
                .isLast(coursePage.isLast())
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