package com.accenture.backend.service.project;

import java.util.*;
import com.accenture.backend.dto.response.*;
import com.accenture.backend.repository.*;
import org.springframework.data.domain.*;

import org.springframework.stereotype.Service;

import com.accenture.backend.entity.Project;
import com.accenture.backend.entity.ProjectMember;
import com.accenture.backend.exception.custom.EntityNotFoundException;
import com.accenture.backend.exception.custom.InvalidInputException;
import com.accenture.backend.exception.custom.PageOutOfRangeException;
import com.accenture.backend.exception.custom.ServiceUnavailableException;
import com.accenture.backend.model.ProjectSortBy;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepo;
    private final ProjectMemberRepository projectMemberRepo;

    @Override
    public Page<ProjectDto> getPublicProjects(Integer page, Integer size, String sortBy, String sortDirection) {
        Long resultsTotal = projectRepo.countAllByConfigIsPublicTrue();
        // Easier to handle the empty array on the client, than an exception
        // if (resultsTotal == 0)
        // return new ArrayList<>();
        Pageable pageable = validatePageableInput(page, size, resultsTotal, sortBy, sortDirection);
        Page<Project> projects = projectRepo.findAllByConfigIsPublicTrue(pageable);
        return projects.map(project -> projectMapper(project));
    }

    @Override
    public Page<ProjectDto> getUserProjects(Integer page, Integer size, String sortBy, String sortDirection) {
        Long userIdPlaceholder = 1L;
        Long resultsTotal = projectMemberRepo.countAllByUserId(userIdPlaceholder);
        Pageable pageable = validatePageableInput(page, size, resultsTotal, sortBy, sortDirection);

        /*
         * findProjectsByUserId uses a join with projectMembers via JPQL => above check
         * is acceptable
         */
        Page<Project> projects = projectRepo.findProjectsByUserId(userIdPlaceholder, pageable);
        return projects.map(project -> projectMapper(project));
    }

    @Override
    public OwnerShortDto getProjectOwner(Long projectId) {
        if (projectId <= 0)
            throw new InvalidInputException("project ID", projectId);

        if (!projectRepo.existsById(projectId))
            throw new EntityNotFoundException("Project", projectId);

        List<ProjectMember> owners = projectMemberRepo.findByProjectIdAndProjectRole(projectId,
                ProjectMember.ProjectRole.OWNER);
        /*
         * In the current implementation the is only 1 owner, but in case it will be
         * change to multi-ownership later, it makes sense to perform an additional
         * check and return the 1st owner (in our version, though, it is the only owner
         * as well).
         * 
         * If no owner found => This should not happen, however if, for whatever reason
         * it does happen, throw ServiceUnavailableException.
         * It will log actual exception's cause but return a general error message to
         * the user.
         */
        if (owners.size() == 0)
            throw new ServiceUnavailableException("No owner found for the project with the id of " + projectId);

        if (owners.size() > 1) {
            System.out.println("Multiple owners found for project " + projectId);
        }

        return OwnerShortDto.fromEntity(owners.get(0));
    }

    private Pageable validatePageableInput(Integer page, Integer size, Long resultsTotal, String sortBy,
            String sortDirection) {
        if (page != null && page < 1)
            throw new InvalidInputException("page", page);
        if (size < 1)
            throw new InvalidInputException("size", size);

        long totalPages = (long) Math.ceil((double) resultsTotal / size);

        if (page != null && page > totalPages)
            throw new PageOutOfRangeException(page, totalPages);

        ProjectSortBy sortByEnum;
        try {
            sortByEnum = ProjectSortBy.valueOf(sortBy.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Projects can only be sorted by either date or title.");
        }
        Sort sort = sortDirection.toLowerCase().equals("desc") ? Sort.by(sortByEnum.fieldName()).descending()
                : Sort.by(sortByEnum.fieldName()).ascending();
        return page == null ? PageRequest.of(0, Integer.MAX_VALUE, sort) : PageRequest.of(page - 1, size, sort);
    }

    private ProjectDto projectMapper(Project project) {
        return new ProjectDto(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getCreatedAt(),
                getProjectOwner(project.getId()));
    }
}
