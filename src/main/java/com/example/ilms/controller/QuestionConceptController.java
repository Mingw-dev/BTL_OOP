package com.example.ilms.controller;

import com.example.ilms.constant.SuccessCode;
import com.example.ilms.dto.question.QuestionConceptRequestDTO;
import com.example.ilms.dto.question.QuestionConceptResponseDTO;
import com.example.ilms.dto.response.ApiResponse;
import com.example.ilms.service.QuestionConceptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/question-concepts")
@RequiredArgsConstructor
public class QuestionConceptController {

    private final QuestionConceptService conceptService;

    @GetMapping
    public ApiResponse<List<QuestionConceptResponseDTO>> getAllConcepts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UUID questionId
    ) {
        List<QuestionConceptResponseDTO> response = conceptService.getAllConcepts(keyword, questionId);
        return ApiResponse.of(SuccessCode.GET_CONCEPTS_SUCCESS, response);
    }

    @GetMapping("/{id}")
    public ApiResponse<QuestionConceptResponseDTO> getConceptById(@PathVariable UUID id) {
        QuestionConceptResponseDTO response = conceptService.getConceptById(id);
        return ApiResponse.of(SuccessCode.GET_CONCEPT_SUCCESS, response);
    }

    @PostMapping
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_TEACHER')")
    public ApiResponse<QuestionConceptResponseDTO> createConcept(
            @Valid @RequestBody QuestionConceptRequestDTO request
    ) {
        QuestionConceptResponseDTO createdConcept = conceptService.createConcept(request);
        return ApiResponse.of(SuccessCode.CONCEPT_CREATED, createdConcept);
    }

    @PutMapping("/{id}")
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_TEACHER')")
    public ApiResponse<QuestionConceptResponseDTO> updateConcept(
            @PathVariable UUID id,
            @Valid @RequestBody QuestionConceptRequestDTO request
    ) {
        QuestionConceptResponseDTO updatedConcept = conceptService.updateConcept(id, request);
        return ApiResponse.of(SuccessCode.CONCEPT_UPDATED, updatedConcept);
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_TEACHER')")
    public ApiResponse<Void> deleteConcept(@PathVariable UUID id) {
        conceptService.deleteConcept(id);
        return ApiResponse.of(SuccessCode.CONCEPT_DELETED, null);
    }
}