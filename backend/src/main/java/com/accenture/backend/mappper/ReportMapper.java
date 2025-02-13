package com.accenture.backend.mappper;

import com.accenture.backend.dto.report.ReportInfoDto;
import com.accenture.backend.dto.report.ShortReportInfoDto;
import com.accenture.backend.dto.user.IdNameDto;
import com.accenture.backend.entity.Evidence;
import com.accenture.backend.entity.Report;
import com.accenture.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReportMapper {

    @Mapping(source = "accused", target = "accused", qualifiedByName = "mapUserToIdNameDto")
    @Mapping(source = "reportType", target = "reportType")
    @Mapping(source = "reportStatus", target = "reportStatus")
    @Mapping(source = "createdAt", target = "createdAt")
    ShortReportInfoDto toShortReportInfoDto(Report report);

    @Mapping(source = "reporter", target = "reporter", qualifiedByName = "mapUserToIdNameDto")
    @Mapping(source = "accused", target = "accused", qualifiedByName = "mapUserToIdNameDto")
    @Mapping(source = "evidences", target = "evidencesUrls", qualifiedByName = "mapEvidencesToUrls")
    ReportInfoDto toReportInfoDto(Report report);

    @Named("mapUserToIdNameDto")
    default IdNameDto mapUserToIdNameDto(User user) {
        if (user == null) {
            return null;
        }
        return new IdNameDto(user.getId(), user.getFirstName() + " " + user.getLastName());
    }

    @Named("mapEvidencesToUrls")
    default List<String> mapEvidencesToUrls(List<Evidence> evidences) {
        return evidences.stream()
                .map(Evidence::getUrl)
                .toList();
    }
}
