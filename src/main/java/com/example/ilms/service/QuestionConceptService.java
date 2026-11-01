package com.example.ilms.service;

import com.example.ilms.dto.question.QuestionConceptRequestDTO;
import com.example.ilms.dto.question.QuestionConceptResponseDTO;
import com.example.ilms.entity.Question;
import com.example.ilms.entity.QuestionConcept;
import com.example.ilms.exception.AppException;
import com.example.ilms.exception.ErrorCode;
import com.example.ilms.repository.QuestionConceptRepository;
import com.example.ilms.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionConceptService {

    private final QuestionConceptRepository conceptRepository;
    private final QuestionRepository questionRepository;

    // 1. Lấy danh sách toàn bộ khái niệm (có hỗ trợ lọc theo từ khóa hoặc questionId)
    @Transactional(readOnly = true)
    public List<QuestionConceptResponseDTO> getAllConcepts(String keyword, UUID questionId) {
        List<QuestionConcept> concepts;

        if (questionId != null) {
            concepts = conceptRepository.findByQuestionId(questionId);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            concepts = conceptRepository.findByNameContainingIgnoreCase(keyword.trim());
        } else {
            concepts = conceptRepository.findAll();
        }

        return concepts.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    // 2. Lấy chi tiết 1 khái niệm theo ID (UUID của bảng question_concepts)
    @Transactional(readOnly = true)
    public QuestionConceptResponseDTO getConceptById(UUID id) {
        QuestionConcept concept = conceptRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONCEPT_NOT_FOUND));
        return mapToResponseDTO(concept);
    }

    // 3. Tạo mới khái niệm kiến thức
    @Transactional
    public QuestionConceptResponseDTO createConcept(QuestionConceptRequestDTO request) {
        // 1. Kiểm tra câu hỏi có tồn tại và đang active không
        Question question = questionRepository.findById(request.getQuestionId())
                .filter(q -> Boolean.TRUE.equals(q.getIsActive()))
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));

        // 2. Bắt lỗi trùng lặp (nếu câu hỏi đã có nhãn này rồi thì ném lỗi 409)
        if (conceptRepository.existsByQuestionIdAndConceptId(request.getQuestionId(), request.getConceptId())) {
            throw new AppException(ErrorCode.CONCEPT_ALREADY_EXISTS);
        }

        // 3. Khởi tạo và lưu
        QuestionConcept concept = new QuestionConcept();
        concept.setQuestion(question);
        concept.setConceptId(request.getConceptId());
        concept.setName(request.getName());
        concept.setDescription(request.getDescription());
        concept.setRelevanceWeight(
                request.getRelevanceWeight() != null ? request.getRelevanceWeight() : new BigDecimal("1.00")
        );

        QuestionConcept savedConcept = conceptRepository.save(concept);
        return mapToResponseDTO(savedConcept);
    }

    // 4. Cập nhật khái niệm kiến thức
    @Transactional
    public QuestionConceptResponseDTO updateConcept(UUID id, QuestionConceptRequestDTO request) {
        QuestionConcept concept = conceptRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONCEPT_NOT_FOUND));

        // Xác định questionId mục tiêu (lấy từ request, nếu không có thì giữ nguyên hiện tại)
        UUID targetQuestionId = request.getQuestionId() != null ? request.getQuestionId() : concept.getQuestion().getId();

        // Kiểm tra xem người dùng có đang thay đổi conceptId hoặc questionId không
        boolean isConceptIdChanged = !concept.getConceptId().equals(request.getConceptId());
        boolean isQuestionIdChanged = !concept.getQuestion().getId().equals(targetQuestionId);

        // Nếu có sự thay đổi, phải kiểm tra xem cặp ID mới này đã bị trùng lặp chưa
        if (isConceptIdChanged || isQuestionIdChanged) {
            if (conceptRepository.existsByQuestionIdAndConceptId(targetQuestionId, request.getConceptId())) {
                throw new AppException(ErrorCode.CONCEPT_ALREADY_EXISTS);
            }
        }

        // Cập nhật thông tin
        concept.setConceptId(request.getConceptId());
        concept.setName(request.getName());
        concept.setDescription(request.getDescription());
        if (request.getRelevanceWeight() != null) {
            concept.setRelevanceWeight(request.getRelevanceWeight());
        }

        // Nếu có đổi câu hỏi, gán lại Question entity
        if (isQuestionIdChanged) {
            Question question = questionRepository.findById(targetQuestionId)
                    .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));
            concept.setQuestion(question);
        }

        QuestionConcept updatedConcept = conceptRepository.save(concept);
        return mapToResponseDTO(updatedConcept);
    }

    // 5. Xóa khái niệm kiến thức
    @Transactional
    public void deleteConcept(UUID id) {
        QuestionConcept concept = conceptRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CONCEPT_NOT_FOUND));
        conceptRepository.delete(concept);
    }

    // Hàm phụ trợ chuyển Entity sang ResponseDTO
    private QuestionConceptResponseDTO mapToResponseDTO(QuestionConcept entity) {
        return QuestionConceptResponseDTO.builder()
                .id(entity.getId())
                .conceptId(entity.getConceptId())
                .name(entity.getName())
                .description(entity.getDescription())
                .relevanceWeight(entity.getRelevanceWeight())
                .questionId(entity.getQuestion() != null ? entity.getQuestion().getId() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}