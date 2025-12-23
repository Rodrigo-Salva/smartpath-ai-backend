package org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.entity.User;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.auth.repository.UserRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.dto.AddSkillRequest;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.dto.SkillDTO;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.dto.UserSkillDTO;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.entity.Skill;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.entity.UserSkill;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.repository.SkillRepository;
import org.salva.springcloud.msvc.cursos.smartpathaibackend.skill.repository.UserSkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<SkillDTO> getAllActiveSkills() {
        return skillRepository.findByIsActiveTrueOrderByNameAsc()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SkillDTO> getSkillsByCategory(String category) {
        return skillRepository.findByCategoryAndIsActiveTrue(category)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserSkillDTO> getUserSkills(Long userId) {
        return userSkillRepository.findByUserIdOrderByPriorityAsc(userId)
                .stream()
                .map(this::mapUserSkillToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserSkillDTO addUserSkill(Long userId, AddSkillRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Buscar o crear skill
        Skill skill = skillRepository.findByNameIgnoreCase(request.getSkillName())
                .orElseGet(() -> {
                    Skill newSkill = Skill.builder()
                            .name(request.getSkillName())
                            .category("TECHNICAL")
                            .isActive(true)
                            .build();
                    return skillRepository.save(newSkill);
                });

        // Verificar si ya existe
        if (userSkillRepository.findByUserIdAndSkillId(userId, skill.getId()).isPresent()) {
            throw new RuntimeException("Esta skill ya está en tu perfil");
        }

        UserSkill userSkill = UserSkill.builder()
                .user(user)
                .skill(skill)
                .currentLevel(request.getCurrentLevel())
                .targetLevel(request.getTargetLevel())
                .priority(request.getPriority())
                .isLearning(true)
                .build();

        UserSkill saved = userSkillRepository.save(userSkill);
        log.info("Skill {} agregada para usuario {}", skill.getName(), userId);

        return mapUserSkillToDTO(saved);
    }

    private SkillDTO mapToDTO(Skill skill) {
        return SkillDTO.builder()
                .id(skill.getId())
                .name(skill.getName())
                .description(skill.getDescription())
                .category(skill.getCategory())
                .isActive(skill.getIsActive())
                .build();
    }

    private UserSkillDTO mapUserSkillToDTO(UserSkill userSkill) {
        return UserSkillDTO.builder()
                .id(userSkill.getId())
                .skillId(userSkill.getSkill().getId())
                .skillName(userSkill.getSkill().getName())
                .currentLevel(userSkill.getCurrentLevel())
                .targetLevel(userSkill.getTargetLevel())
                .priority(userSkill.getPriority())
                .isLearning(userSkill.getIsLearning())
                .build();
    }
}

