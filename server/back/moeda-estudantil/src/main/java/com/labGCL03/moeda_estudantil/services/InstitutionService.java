package com.labGCL03.moeda_estudantil.services;

import com.labGCL03.moeda_estudantil.dto.InstitutionDTO;
import com.labGCL03.moeda_estudantil.dto.InstitutionRequestDTO;
import com.labGCL03.moeda_estudantil.entities.Institution;
import com.labGCL03.moeda_estudantil.exception.BusinessException;
import com.labGCL03.moeda_estudantil.exception.ResourceNotFoundException;
import com.labGCL03.moeda_estudantil.repositories.InstitutionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InstitutionService {

    private final InstitutionRepository institutionRepository;

    public List<InstitutionDTO> findAll() {
        return institutionRepository.findAll()
                .stream()
                .map(InstitutionDTO::new)
                .collect(Collectors.toList());
    }

    public InstitutionDTO findById(Long id) {
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instituição", id));
        return new InstitutionDTO(institution);
    }

    public InstitutionDTO findByName(String name) {
        Institution institution = institutionRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Instituição com nome '" + name + "' não encontrada"));
        return new InstitutionDTO(institution);
    }

    public InstitutionDTO create(InstitutionRequestDTO dto) {
        // Validar nome único
        if (institutionRepository.existsByName(dto.getName())) {
            throw new BusinessException("Já existe uma instituição cadastrada com este nome");
        }

        Institution institution = new Institution();
        institution.setName(dto.getName());

        Institution saved = institutionRepository.save(institution);
        return new InstitutionDTO(saved);
    }

    public InstitutionDTO update(Long id, InstitutionRequestDTO dto) {
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instituição", id));

        // Validar nome único (exceto para a própria instituição)
        institutionRepository.findByName(dto.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new BusinessException("Já existe outra instituição cadastrada com este nome");
            }
        });

        institution.setName(dto.getName());
        Institution updated = institutionRepository.save(institution);
        return new InstitutionDTO(updated);
    }

    public void delete(Long id) {
        Institution institution = institutionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Instituição", id));

        // Verificar se há alunos ou professores vinculados
        Long studentCount = institutionRepository.countStudentsByInstitutionId(id);
        Long teacherCount = institutionRepository.countTeachersByInstitutionId(id);

        if (studentCount > 0 || teacherCount > 0) {
            throw new BusinessException(
                String.format("Não é possível excluir esta instituição. " +
                    "Há %d aluno(s) e %d professor(es) vinculado(s)", studentCount, teacherCount)
            );
        }

        institutionRepository.delete(institution);
    }

    public Long countStudents(Long institutionId) {
        if (!institutionRepository.existsById(institutionId)) {
            throw new ResourceNotFoundException("Instituição", institutionId);
        }
        return institutionRepository.countStudentsByInstitutionId(institutionId);
    }

    public Long countTeachers(Long institutionId) {
        if (!institutionRepository.existsById(institutionId)) {
            throw new ResourceNotFoundException("Instituição", institutionId);
        }
        return institutionRepository.countTeachersByInstitutionId(institutionId);
    }
}
