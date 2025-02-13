package com.accenture.backend.service.serviceimpl;

import com.accenture.backend.dto.report.InternalReportDto;
import com.accenture.backend.dto.report.ReportInfoDto;
import com.accenture.backend.dto.report.ReportSearchDto;
import com.accenture.backend.dto.report.ShortReportInfoDto;
import com.accenture.backend.entity.Evidence;
import com.accenture.backend.entity.Report;
import com.accenture.backend.entity.User;
import com.accenture.backend.enums.report.ReportStatus;
import com.accenture.backend.enums.report.ReportType;
import com.accenture.backend.exception.ReportNotFoundException;
import com.accenture.backend.mappper.ReportMapper;
import com.accenture.backend.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceImplTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ReportMapper reportMapper;

    @InjectMocks
    private ReportServiceImpl reportService;

    @Captor
    private ArgumentCaptor<Report> reportCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateReportSuccessfully() {
        long reporterId = 1L;
        long accusedId = 2L;
        ReportType reportType = ReportType.RULES_VIOLATION;
        List<Evidence> evidences = List.of(new Evidence());

        reportService.createReport(reporterId, accusedId, reportType, evidences);

        verify(reportRepository, times(1)).save(reportCaptor.capture());
        Report capturedReport = reportCaptor.getValue();

        assertNotNull(capturedReport);
        assertEquals(reporterId, capturedReport.getReporter().getId());
        assertEquals(accusedId, capturedReport.getAccused().getId());
        assertEquals(reportType, capturedReport.getReportType());
        assertEquals(ReportStatus.OPEN, capturedReport.getReportStatus());
        assertEquals(evidences, capturedReport.getEvidences());
    }

    @Test
    void shouldReturnReportsSuccessfully() {
        ReportSearchDto searchDto = new ReportSearchDto();
        searchDto.setReporterId(1L);
        searchDto.setAccusedId(2L);
        searchDto.setReportType(ReportType.SPAM);
        searchDto.setReportStatus(ReportStatus.OPEN);
        searchDto.setSortBy("id");
        searchDto.setSortOrder("asc");
        searchDto.setPage(0);
        searchDto.setSize(10);

        Report report = new Report();
        ShortReportInfoDto shortReportInfoDto = new ShortReportInfoDto();

        Page<Report> reportPage = mock(Page.class);
        when(reportPage.map(any())).thenReturn(Page.empty());

        when(reportRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(reportPage);

        List<ShortReportInfoDto> result = reportService.getReports(searchDto);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(reportRepository, times(1)).findAll(any(Specification.class), any(PageRequest.class));
    }


    @Test
    void shouldReturnReportDetailSuccessfully() {
        long reportId = 1L;
        Report report = new Report();
        ReportInfoDto reportInfoDto = new ReportInfoDto();

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(reportMapper.toReportInfoDto(report)).thenReturn(reportInfoDto);

        ReportInfoDto result = reportService.getReportDetail(reportId);

        assertNotNull(result);
        assertEquals(reportInfoDto, result);
        verify(reportRepository, times(1)).findById(reportId);
        verify(reportMapper, times(1)).toReportInfoDto(report);
    }

    @Test
    void shouldThrowExceptionWhenReportDetailNotFound() {
        long reportId = 1L;

        when(reportRepository.findById(reportId)).thenReturn(Optional.empty());

        assertThrows(ReportNotFoundException.class, () -> reportService.getReportDetail(reportId));
    }

    @Test
    void shouldReturnInternalReportDtoSuccessfully() {
        long reportId = 1L;
        User user = new User();
        user.setId(2L);
        user.setEmail("test@example.com");
        Report report = new Report();
        report.setAccused(user);
        report.setReportType(ReportType.SPAM);

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));

        InternalReportDto result = reportService.accusedIdAndReportReason(reportId);

        assertNotNull(result);
        assertEquals(2L, result.getAccusedId());
        assertEquals("test@example.com", result.getAccusedEmail());
        assertEquals(ReportType.SPAM, result.getReportType());
        verify(reportRepository, times(1)).findById(reportId);
    }

    @Test
    void shouldUpdateReportInfoSuccessfully() {
        long reportId = 1L;
        ReportStatus newStatus = ReportStatus.RESOLVED;
        Report report = new Report();
        report.setId(reportId);

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));

        reportService.updateReportInfo(reportId, newStatus);

        verify(reportRepository, times(1)).save(reportCaptor.capture());
        Report updatedReport = reportCaptor.getValue();

        assertEquals(newStatus, updatedReport.getReportStatus());
        verify(reportRepository, times(1)).findById(reportId);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentReport() {
        long reportId = 1L;
        ReportStatus newStatus = ReportStatus.RESOLVED;

        when(reportRepository.findById(reportId)).thenReturn(Optional.empty());

        assertThrows(ReportNotFoundException.class, () -> reportService.updateReportInfo(reportId, newStatus));
    }
}
