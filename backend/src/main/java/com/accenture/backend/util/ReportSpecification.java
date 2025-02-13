package com.accenture.backend.util;

import com.accenture.backend.dto.report.FilterDto;
import com.accenture.backend.entity.Report;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ReportSpecification implements Specification<Report> {

    private final FilterDto filterDto;

    @Override
    public Predicate toPredicate(Root<Report> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (filterDto.getReporterId() != null){
            predicates.add(criteriaBuilder.equal(root.get("reporter").get("id"), filterDto.getReporterId()));
        }

        if (filterDto.getAccusedId() != null){
            predicates.add(criteriaBuilder.equal(root.get("accused").get("id"), filterDto.getAccusedId()));
        }

        if (filterDto.getReportType() != null){
            predicates.add(criteriaBuilder.equal(root.get("reportType"), filterDto.getReportType()));
        }

        if (filterDto.getReportStatus() != null){
            predicates.add(criteriaBuilder.equal(root.get("reportStatus"), filterDto.getReportStatus()));
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
