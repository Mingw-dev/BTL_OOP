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
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.example.ilms.event.AnnouncementCreateEvent;
import org.springframework.context.ApplicationEventPublisher;
@Service
@RequiredArgsConstructor
public class ClassAnnouncementService {
    private final ClassAnnouncementReposity classAnnouncementReposity;
    private final ModelMapper modelMapper;
    private final ClassRepository classRepository;
    private final AnnouncementAccessService announcementAccessService;
    private final ApplicationEventPublisher eventPublisher;
    @Transactional
    public void createAnnouncement(AnnouncementCreateRequestDTO requestDTO, UUID classId, User currentUser) {
        announcementAccessService.requiredRoleMember(classId, currentUser);
        ClassEntity classEntity = classRepository.findById(classId).orElseThrow(() -> new AppException(ErrorCode.CLASS_NOT_FOUND));
        ClassAnnouncement classAnnouncement = ClassAnnouncement.builder()
                .classEntity(classEntity)
                .title(requestDTO.getTitle())
                .content(requestDTO.getContent())
                .isPinned(false)
                .author(currentUser)
                .build();
        ClassAnnouncement saved =
                classAnnouncementReposity.save(classAnnouncement);

        eventPublisher.publishEvent(
                new AnnouncementCreateEvent(
                        classId,
                        saved.getId()
                )
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<AnnouncementResponseDTO> getAnouncements(UUID classId, int page, int size, User currentUser) {
        //sắp xếp theo isPinned giảm dần, rồi đến createdAt giảm dần

        announcementAccessService.getActiveMember(classId, currentUser);
        Sort sort = Sort.by(Sort.Direction.DESC, "isPinned")
                .and(Sort.by(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ClassAnnouncement> announcementPage = classAnnouncementReposity.findByClassEntityId(classId, pageable);
        // chuyển từng thông báo từ entity sang DTO, rồi gom thành danh sách content
        List<AnnouncementResponseDTO> content = announcementPage.getContent().stream()
                .map(entity -> {
                    AnnouncementResponseDTO dto = new AnnouncementResponseDTO();

                    dto.setId(entity.getId());
                    dto.setTitle(entity.getTitle());
                    dto.setContent(entity.getContent());
                    dto.setPinned(entity.getIsPinned());

                    User author = entity.getAuthor();

                    if (author != null) {
                        dto.setAuthorId(author.getId());
                        dto.setAuthorName(author.getFullName());
                    }

                    dto.setCreateTime(entity.getCreatedAt());
                    dto.setUpdateTime(entity.getUpdatedAt());

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

    @Transactional
    public void TogglePinStatus(UUID classId, UUID announcementId, boolean isPinned, User currentUser) {
        ClassAnnouncement classAnnouncement = classAnnouncementReposity.findById(announcementId)
                .orElseThrow(() -> new AppException(ErrorCode.ANNOUNCEMENT_NOT_FOUND));
        if (!classAnnouncement.getClassEntity().getId().equals(classId)) {
            throw new AppException(ErrorCode.ANNOUNCEMENT_NOT_FOUND);
        }
        announcementAccessService.requiredRoleMember(classAnnouncement.getClassEntity().getId(), currentUser);
        classAnnouncement.setIsPinned(isPinned);

        classAnnouncementReposity.save(classAnnouncement);
    }
}


