package com.example.ilms.service;

import com.example.ilms.dto.common.PageResponse;
import com.example.ilms.specification.ClassSpecification;
import com.example.ilms.dto.classDTO.ClassCreateRequestDTO;
import com.example.ilms.dto.classDTO.ClassResponseDTO;
import com.example.ilms.entity.ClassEntity;
import com.example.ilms.entity.ClassMember;
import com.example.ilms.entity.User;
import com.example.ilms.exception.AppException;
import com.example.ilms.repository.ClassMemberRepository;
import com.example.ilms.repository.ClassRepository;
import com.example.ilms.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;

import java.util.UUID;

@Service

public class ClassService {

    private final ClassRepository classRepository;
    private final ClassMemberRepository classMemberRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public ClassService(ClassRepository classRepository,
                        ClassMemberRepository classMemberRepository,
                        UserRepository userRepository, ModelMapper modelMapper){

        this.classMemberRepository = classMemberRepository;
        this.classRepository = classRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public ClassResponseDTO createClass(ClassCreateRequestDTO request,User currentUser) {


        // Tạo thông tin lớp học
        ClassEntity newClass = modelMapper.map(request, ClassEntity.class);
        newClass.setOwner(currentUser);
        newClass.setClassCode(generateRandomClassCode());
        // Database nhận Entity và cấp phát ID
        newClass = classRepository.save(newClass);

        //  Giáo viên của lớp
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
    public PageResponse<ClassResponseDTO> getClasses(
            String search, String status, UUID semesterId, Boolean isArchived,
            int page, int size, String sort, User currentUser
    ){
        //check quyền Admin
        boolean isAdmin = currentUser.getRole() != null &&
                "ROLE_ADMIN".equals(currentUser.getRole().getCode());

        // Xử lý Sắp xếp(sort)
        Sort pageSort  = Sort.unsorted();
        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            String sortBy = sortParams[0];
            Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            pageSort = Sort.by(direction, sortBy);
        }

        //Xử lý phân trang
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, size, pageSort);

        Specification<ClassEntity> spec = ClassSpecification.filterClass(
                search, status, semesterId, isArchived, currentUser, isAdmin
        );

        //  Truy vấn cơ sở dữ liệu
        Page<ClassEntity> classPage = classRepository.findAll(spec, pageable);

        //  Dùng ModelMapper để chuyển danh sách ClassEntity sang ClassResponseDTO
        List<ClassResponseDTO> dtoList = classPage.getContent().stream()
                .map(entity -> modelMapper.map(entity, ClassResponseDTO.class))
                .toList();

        // Bọc kết quả vào PageResponse trả về cho Client
        return new PageResponse<>(
                dtoList,                       // 1. Danh sách items (List<T>)
                classPage.getNumber() + 1,     // 2. pageNumber (int)
                classPage.getSize(),           // 3. pageSize (int)
                classPage.getTotalElements(),  // 4. totalElements (long)
                classPage.getTotalPages(),     // 5. totalPages (int)
                classPage.isLast()             // 6. isLast (boolean)
        );
    }
}