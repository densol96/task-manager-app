package com.accenture.backend.service.project;

import java.util.*;
import com.accenture.backend.dto.response.*;
import com.accenture.backend.repository.*;

import org.springframework.stereotype.Service;

import com.accenture.backend.entity.ProjectMember;
import com.accenture.backend.exception.custom.EntityNotFoundException;
import com.accenture.backend.exception.custom.InvalidInputException;
import com.accenture.backend.exception.custom.ServiceUnavailableException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepo;
    private final ProjectMemberRepository projectMemberRepo;

    @Override
    public List<ProjectDto> getAllPublicProjects() {
        /*
         * If there are no public repositories => return an empty list to client
         * since it is easier to handle it such way in React codebase.
         */
        return projectRepo.findAllByConfigIsPublicTrue().stream().map(project -> {
            return new ProjectDto(
                    project.getId(),
                    project.getTitle(),
                    project.getDescription(),
                    project.getCreatedAt(),
                    getProjectOwner(project.getId()));
        }).toList();
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

}
