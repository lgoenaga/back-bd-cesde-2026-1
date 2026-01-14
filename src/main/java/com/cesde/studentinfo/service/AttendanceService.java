package com.cesde.studentinfo.service;

import com.cesde.studentinfo.exception.BusinessException;
import com.cesde.studentinfo.exception.ResourceNotFoundException;
import com.cesde.studentinfo.model.Attendance;
import com.cesde.studentinfo.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service para gestión de asistencia
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    @Transactional(readOnly = true)
    public List<Attendance> getAllAttendance() {
        log.info("Fetching all attendance records");
        return attendanceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Attendance> getAttendanceById(Long id) {
        log.info("Fetching attendance by id: {}", id);
        return attendanceRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceByStudentId(Long studentId) {
        log.info("Fetching attendance for student: {}", studentId);
        return attendanceRepository.findByStudentId(studentId);
    }

    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceBySessionId(Long sessionId) {
        log.info("Fetching attendance for session: {}", sessionId);
        return attendanceRepository.findByClassSessionId(sessionId);
    }

    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceByEnrollmentId(Long enrollmentId) {
        log.info("Fetching attendance for enrollment: {}", enrollmentId);
        return attendanceRepository.findByEnrollmentId(enrollmentId);
    }

    @Transactional(readOnly = true)
    public List<Attendance> getAttendanceByDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("Fetching attendance between {} and {}", startDate, endDate);
        return attendanceRepository.findByDateRange(startDate, endDate);
    }

    public Attendance createAttendance(Attendance attendance) {
        log.info("Creating attendance record for subject enrollment: {}", attendance.getSubjectEnrollment().getId());

        // Validar que no exista duplicado
        if (attendanceRepository.existsBySubjectEnrollmentIdAndClassSessionId(
                attendance.getSubjectEnrollment().getId(),
                attendance.getClassSession().getId())) {
            throw new BusinessException("Attendance already registered for this student in this session");
        }

        Attendance saved = attendanceRepository.save(attendance);
        log.info("Attendance created successfully with id: {}", saved.getId());
        return saved;
    }

    public Attendance updateAttendance(Long id, Attendance attendance) {
        log.info("Updating attendance: {}", id);

        Attendance existing = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance", id));

        if (attendance.getStatus() != null) {
            existing.setStatus(attendance.getStatus());
        }
        if (attendance.getIsExcused() != null) {
            existing.setIsExcused(attendance.getIsExcused());
        }
        if (attendance.getExcuseReason() != null) {
            existing.setExcuseReason(attendance.getExcuseReason());
        }
        if (attendance.getNotes() != null) {
            existing.setNotes(attendance.getNotes());
        }

        return attendanceRepository.save(existing);
    }

    public void deleteAttendance(Long id) {
        log.info("Deleting attendance: {}", id);
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance", id));

        attendanceRepository.deleteById(id);
        log.info("Attendance deleted successfully");
    }

    @Transactional(readOnly = true)
    public long countAttendance() {
        return attendanceRepository.count();
    }
}

