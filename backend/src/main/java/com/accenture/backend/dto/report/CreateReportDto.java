package com.accenture.backend.dto.report;

import com.accenture.backend.enums.report.ReportType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CreateReportDto {

    @Email
    private String accused;

    private ReportType reportType;

    @Size(max = 3, message = "The evidences list cannot contain more than 3 files.")
    private List<MultipartFile> evidences;
}
