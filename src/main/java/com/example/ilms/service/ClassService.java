package com.example.ilms.service;

import com.example.ilms.dto.classDTO.ClassCreateRequestDTO;
import com.example.ilms.dto.classDTO.ClassResponseDTO;
import com.example.ilms.entity.ClassEntity;
import com.example.ilms.entity.ClassMember;
import com.example.ilms.entity.User;
import com.example.ilms.exception.AppException;
import com.example.ilms.exception.ErrorCode;
import com.example.ilms.repository.ClassMemberRepository;
import com.example.ilms.repository.ClassRepository;
import com.example.ilms.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}