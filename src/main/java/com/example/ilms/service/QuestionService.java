package com.example.ilms.service;

import com.example.ilms.dto.common.PageResponse;
import com.example.ilms.dto.common.ConceptMappingDTO; // Import thêm DTO này
import com.example.ilms.dto.question.QuestionRequestDTO;
import com.example.ilms.dto.question.QuestionResponseDTO;
import com.example.ilms.entity.Question;
import com.example.ilms.entity.QuestionConcept;
import com.example.ilms.entity.User;
import com.example.ilms.exception.AppException;
import com.example.ilms.exception.ErrorCode;
import com.example.ilms.repository.QuestionConceptRepository;
import com.example.ilms.repository.QuestionRepository;
import com.example.ilms.specification.QuestionSpecification;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionConceptRepository conceptRepository;
    private final ModelMapper modelMapper;

    // 1. Lấy danh sách câu hỏi
    @Transactional(readOnly = true)
    public PageResponse<QuestionResponseDTO> getQuestions(
            String search, String difficulty, UUID conceptId,
            int page, int size, String sort, User currentUser
    ) {
        Sort pageSort = Sort.unsorted();
        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            String sortBy = sortParams[0];
            Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            pageSort = Sort.by(direction, sortBy);
        }

        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, size, pageSort);
        Specification<Question> spec = QuestionSpecification.filterQuestions(search, difficulty, conceptId);
        Page<Question> questionPage = questionRepository.findAll(spec, pageable);

        List<QuestionResponseDTO> dtoList = questionPage.getContent().stream()
                .map(this::mapToResponseDTO)
                .toList();

        return new PageResponse<>(
                dtoList,
                questionPage.getNumber() + 1,
                questionPage.getSize(),
                questionPage.getTotalElements(),
                questionPage.getTotalPages(),
                questionPage.isLast()
        );
    }

    // 2. Lấy chi tiết một câu hỏi theo ID
    @Transactional(readOnly = true)
    public QuestionResponseDTO getQuestionById(UUID id, User currentUser) {
        Question question = questionRepository.findById(id)
                .filter(q -> Boolean.TRUE.equals(q.getIsActive()))
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));
        return mapToResponseDTO(question);
    }

    // 3. Tạo mới câu hỏi
    @Transactional
    public QuestionResponseDTO createQuestion(QuestionRequestDTO request, User currentUser) {
        Question question = modelMapper.map(request, Question.class);
        question.setId(null);

        question.setIsActive(true);
        question.setIsVerifiedByTeacher(false);

        if (question.getSourceType() == null) {
            question.setSourceType("MANUAL");
        }
        if (question.getQuestionType() == null) {
            question.setQuestionType("SINGLE_CHOICE");
        }
        Question savedQuestion = questionRepository.save(question);

        // Đã cập nhật: Lấy danh sách ConceptMappingDTO thay vì String ID
        if (request.getConcepts() != null && !request.getConcepts().isEmpty()) {
            List<QuestionConcept> conceptsToSave = request.getConcepts().stream().map(conceptDto -> {
                QuestionConcept qc = new QuestionConcept();
                qc.setQuestion(savedQuestion);
                qc.setConceptId(conceptDto.getConceptId());
                qc.setName(conceptDto.getName()); // Lưu trực tiếp tên thật
                qc.setRelevanceWeight(new BigDecimal("1.00"));
                return qc;
            }).toList();

            conceptRepository.saveAll(conceptsToSave);
        }

        return mapToResponseDTO(savedQuestion);
    }

    // 4. Cập nhật thông tin câu hỏi
    @Transactional
    public QuestionResponseDTO updateQuestion(UUID id, QuestionRequestDTO request, User currentUser) {
        Question question = questionRepository.findById(id)
                .filter(q -> Boolean.TRUE.equals(q.getIsActive()))
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));

        question.setContent(request.getContent());
        question.setOptionA(request.getOptionA());
        question.setOptionB(request.getOptionB());
        question.setOptionC(request.getOptionC());
        question.setOptionD(request.getOptionD());
        question.setCorrectAnswer(request.getCorrectAnswer());
        question.setDefaultPoints(request.getDefaultPoints());
        question.setDifficultyLevel(request.getDifficultyLevel());

        Question updatedQuestion = questionRepository.save(question);

        // Đã cập nhật: Xử lý danh sách ConceptMappingDTO
        if (request.getConcepts() != null) {
            conceptRepository.deleteByQuestionId(updatedQuestion.getId());

            if (!request.getConcepts().isEmpty()) {
                List<QuestionConcept> conceptsToSave = request.getConcepts().stream().map(conceptDto -> {
                    QuestionConcept qc = new QuestionConcept();
                    qc.setQuestion(updatedQuestion);
                    qc.setConceptId(conceptDto.getConceptId());
                    qc.setName(conceptDto.getName()); // Lưu trực tiếp tên thật
                    qc.setRelevanceWeight(new BigDecimal("1.00"));
                    return qc;
                }).toList();

                conceptRepository.saveAll(conceptsToSave);
            }
        }

        return mapToResponseDTO(updatedQuestion);
    }

    // 5. Xóa mềm câu hỏi (Soft-delete)
    @Transactional
    public void deleteQuestion(UUID id, User currentUser) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.QUESTION_NOT_FOUND));

        question.setIsActive(false);
        questionRepository.save(question);
    }

    // Hàm phụ trợ map ra DTO
    private QuestionResponseDTO mapToResponseDTO(Question entity) {
        QuestionResponseDTO dto = modelMapper.map(entity, QuestionResponseDTO.class);

        // Đã cập nhật: Trả về danh sách ConceptMappingDTO
        if (entity.getId() != null) {
            List<ConceptMappingDTO> conceptMappings = conceptRepository.findByQuestionId(entity.getId())
                    .stream()
                    .map(qc -> new ConceptMappingDTO(qc.getConceptId(), qc.getName()))
                    .toList();
            dto.setConcepts(conceptMappings);
        }

        return dto;
    }
}