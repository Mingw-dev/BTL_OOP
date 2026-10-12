package com.example.ilms.service;

import com.example.ilms.dto.announcement.AnnouncementCreateRequestDTO;
import com.example.ilms.dto.announcement.AnnouncementResponseDTO;
import com.example.ilms.dto.common.PageResponse;
import com.example.ilms.entity.ClassAnnouncement;
import com.example.ilms.entity.ClassEntity;
import com.example.ilms.entity.User;
import com.example.ilms.exception.AppException;
import com.example.ilms.exception.ErrorCode;
import com.example.ilms.repository.ClassAnnouncementReposity;
import com.example.ilms.repository.ClassRepository;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Server
@RequiredArgsConstructor
public class ClassAnnouncementService {
    private final ClassAnnouncementReposity classAnnouncementReposity;
    private final ModelMapper modelMapper;
    private final ClassRepository classRepository;

    public void createAnnouncement(AnnouncementCreateRequestDTO requestDTO, UUID classId, User currentUser) {
        ClassEntity classEntity = classRepository.findById(classId).orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        ClassAnnouncement classAnnouncement = ClassAnnouncement.builder()
                .classEntity(classEntity)
                .title(requestDTO.getTitle())
                .content(requestDTO.getContent())
                .isPinned(false)
                .author(currentUser)
                .build();
        classAnnouncementReposity.save(classAnnouncement);
    }

    public PageResponse<AnnouncementResponseDTO> getAnouncements(UUID classId, int page, int size) {
        //sắp xếp theo isPinned giảm dần, rồi đến createdAt giảm dần
        Sort sort = Sort.by(Sort.Direction.DESC, "isPinned")
                .and(Sort.by(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ClassAnnouncement> announcementPage = classAnnouncementReposity.findByClassEntityId(classId, pageable);
        List<AnnouncementResponseDTO> content = announcementPage.getContent().stream()
                .map(entity ->{
                    AnnouncementResponseDTO dto = modelMapper.map(entity, AnnouncementResponseDTO.class);
                    dto.setAuthorId(entity.getAuthor().getId());
                    return dto;
                })
                .collect(Collectors.toList());

        return PageResponse.<AnnouncementResponseDTO>builder()
                .items(content)
                .pageNumber(announcementPage.getNumber())
                .pageSize(announcementPage.getSize())
                .totalElements(announcementPage.getTotalElements())
                .totalPages(announcementPage.getTotalPages())
                .isLast(announcementPage.isLast())
                .build();
    }
}


