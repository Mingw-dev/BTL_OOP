package com.example.ilms.service;

import com.example.ilms.dto.classDTO.ClassCreateRequestDTO;
import com.example.ilms.dto.classDTO.ClassResponseDTO;
import com.example.ilms.dto.common.PageResponse;
import com.example.ilms.entity.ClassEntity;
import com.example.ilms.entity.ClassMember;
import com.example.ilms.entity.User;
import com.example.ilms.exception.AppException;
import com.example.ilms.exception.ErrorCode;
import com.example.ilms.repository.ClassMemberRepository;
import com.example.ilms.repository.ClassRepository;
import com.example.ilms.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClassService {

    private final ClassRepository classRepository;
    private final ClassMemberRepository classMemberRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public ClassService(ClassRepository classRepository,
            ClassMemberRepository classMemberRepository,
            UserRepository userRepository, ModelMapper modelMapper) {

        this.classMemberRepository = classMemberRepository;
        this.classRepository = classRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public ClassResponseDTO createClass(ClassCreateRequestDTO request, User currentUser) {

        // Tạo thông tin lớp học
        ClassEntity newClass = modelMapper.map(request, ClassEntity.class);
        newClass.setOwner(currentUser);
        newClass.setClassCode(generateRandomClassCode());
        // Database nhận Entity và cấp phát ID
        newClass = classRepository.save(newClass);

        // Giáo viên của lớp
        ClassMember classMember = ClassMember.builder()
                .user(currentUser)
                .classEntity(newClass)
                .role("TEACHER")
                .status("ACTIVE")
                .build();

        classMemberRepository.save(classMember);

        return modelMapper.map(newClass, ClassResponseDTO.class);
    }

    private String generateRandomClassCode() {
        String code;
        do {
            code = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (classRepository.existsByClassCode(code)); // Hỏi DB xem mã này xài chưa

        return code;
    }

    @Transactional(readOnly = true)
    public PageResponse<ClassResponseDTO> getClasses(String search, String visibility, int page, int size,
            User currentUser) {
        // Khởi tạo đối tượng phân trang, sắp xếp theo thời gian (nếu Entity có trường
        // createdAt hoặc startAt)
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        // Gọi hàm HQL từ Repository để lọc dữ liệu
        Page<ClassEntity> classPage = classRepository.findClassesWithFilter(search, visibility, currentUser.getId(),
                pageable);

        List<ClassResponseDTO> content = classPage.getContent().stream()
                .map(entity -> {
                    ClassResponseDTO dto = modelMapper.map(entity, ClassResponseDTO.class);
                    if (entity.getOwner() != null) {
                        dto.setOwnerId(entity.getOwner().getId());
                    }
                    return dto;
                })
                .collect(Collectors.toList());

        return PageResponse.<ClassResponseDTO>builder()
                .items(content)
                .pageNumber(classPage.getNumber())
                .pageSize(classPage.getSize())
                .totalElements(classPage.getTotalElements())
                .totalPages(classPage.getTotalPages())
                .isLast(classPage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public ClassResponseDTO getClassById(UUID classId, User currentUser) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        boolean isMember = classMemberRepository.existsByClassEntityIdAndUserId(classId, currentUser.getId());
        if (!isMember) {
            throw new AppException(ErrorCode.CLASS_ACCESS_DENIED);
        }
        ClassResponseDTO response = modelMapper.map(classEntity, ClassResponseDTO.class);
        if (classEntity.getOwner() != null) {
            response.setOwnerId(classEntity.getOwner().getId());
        }
        return response;
    }

    @Transactional
    public void updateClassStatus(UUID classId, boolean isClosed, User currentUser) {
        ClassEntity classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));

        // Kiểm tra quyền: Chỉ giảng viên tạo lớp mới được thao tác
        if (!classEntity.getOwner().getId().equals(currentUser.getId())) {

            throw new AppException(ErrorCode.CLASS_ACCESS_DENIED);
        }

        // Đảm bảo ClassEntity có trường isClosed và hàm setClosed()
        classEntity.setIsCodeActive(isClosed);
        classRepository.save(classEntity);
    }
}