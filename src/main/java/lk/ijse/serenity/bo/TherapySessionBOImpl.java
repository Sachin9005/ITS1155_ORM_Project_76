package lk.ijse.serenity.bo;

import lk.ijse.serenity.dao.TherapySessionDAOImpl;
import lk.ijse.serenity.dto.TherapySessionDTO;
import lk.ijse.serenity.entity.TherapySession;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TherapySessionBOImpl {

    private final TherapySessionDAOImpl dao = new TherapySessionDAOImpl();

    public boolean book(TherapySessionDTO therapySessionDTO) throws Exception {
        if (therapySessionDTO.getScheduledAt() == null) throw new Exception("Scheduled date/time");
        if (therapySessionDTO.getScheduledAt().isBefore(LocalDateTime.now())) {
            throw new Exception("Session date must be in the future.");
        }
        if (dao.hasConflict(therapySessionDTO.getTherapist().getId(), therapySessionDTO.getScheduledAt(), null)) {
            throw new Exception("Scheduling conflict for " + therapySessionDTO.getTherapist().getName() + " at " + therapySessionDTO.getScheduledAt());
        }
        TherapySession therapySession = TherapySession.builder()
                .patient(therapySessionDTO.getPatient())
                .therapist(therapySessionDTO.getTherapist())
                .therapyProgram(therapySessionDTO.getTherapyProgram())
                .scheduledAt(therapySessionDTO.getScheduledAt())
                .status(TherapySession.Status.SCHEDULED)
                .build();
        return dao.save(therapySession);
    }

    public boolean reschedule(TherapySessionDTO session, LocalDateTime newTime) throws Exception {
        if (dao.hasConflict(session.getTherapist().getId(), newTime, session.getId())) {
            throw new Exception("Scheduling conflict for " + session.getTherapist().getName() + " at " + newTime);
        }
        session.setScheduledAt(newTime);
        session.setStatus(TherapySession.Status.RESCHEDULED);

        TherapySession therapySession = TherapySession.builder()
                .id(session.getId())
                .patient(session.getPatient())
                .therapist(session.getTherapist())
                .therapyProgram(session.getTherapyProgram())
                .scheduledAt(session.getScheduledAt())
                .status(session.getStatus())
                .build();
        return dao.update(therapySession);
    }

    public boolean cancel(TherapySessionDTO session){
        session.setStatus(TherapySession.Status.CANCELLED);
        TherapySession therapySession = TherapySession.builder()
                .id(session.getId())
                .patient(session.getPatient())
                .therapist(session.getTherapist())
                .therapyProgram(session.getTherapyProgram())
                .scheduledAt(session.getScheduledAt())
                .status(session.getStatus())
                .build();
        return dao.update(therapySession);
    }

    public boolean complete(TherapySessionDTO session) {
        session.setStatus(TherapySession.Status.COMPLETED);
        TherapySession therapySession = TherapySession.builder()
                .id(session.getId())
                .patient(session.getPatient())
                .therapist(session.getTherapist())
                .therapyProgram(session.getTherapyProgram())
                .scheduledAt(session.getScheduledAt())
                .status(session.getStatus())
                .build();
        return dao.update(therapySession);
    }

    public List<TherapySessionDTO> getAllSessions() {

        List<TherapySession> therapySession = dao.findAll();

        List<TherapySessionDTO> therapySessionDTOList = new ArrayList<>();
        for (TherapySession session : therapySession) {
            therapySessionDTOList.add(TherapySessionDTO.builder()
                    .id(session.getId())
                    .patient(session.getPatient())
                    .therapist(session.getTherapist())
                    .therapyProgram(session.getTherapyProgram())
                    .scheduledAt(session.getScheduledAt())
                    .status(session.getStatus())
                    .build());
        }
        return therapySessionDTOList;

    }

    public List<TherapySessionDTO> findByPatient(Long patientId)  {

        List<TherapySession> therapySessions = dao.findByPatient(patientId);
        List<TherapySessionDTO> therapySessionDTOList = new ArrayList<>();
        for (TherapySession session : therapySessions) {
            therapySessionDTOList.add(TherapySessionDTO.builder()
                    .id(session.getId())
                    .patient(session.getPatient())
                    .therapist(session.getTherapist())
                    .therapyProgram(session.getTherapyProgram())
                    .scheduledAt(session.getScheduledAt())
                    .status(session.getStatus())
                    .build());
        }
        return therapySessionDTOList;
    }

    public List<TherapySessionDTO> findByTherapist(Long therapistId) {
        List<TherapySession> therapySessions = dao.findByTherapist(therapistId);
        List<TherapySessionDTO> therapySessionDTOList = new ArrayList<>();
        for (TherapySession session : therapySessions) {
            therapySessionDTOList.add(TherapySessionDTO.builder()
                    .id(session.getId())
                    .patient(session.getPatient())
                    .therapist(session.getTherapist())
                    .therapyProgram(session.getTherapyProgram())
                    .scheduledAt(session.getScheduledAt())
                    .status(session.getStatus())
                    .build());
        }
        return therapySessionDTOList;
    }

    public boolean update(TherapySessionDTO session) {
        TherapySession therapySession = TherapySession.builder()
                .id(session.getId())
                .patient(session.getPatient())
                .therapist(session.getTherapist())
                .therapyProgram(session.getTherapyProgram())
                .scheduledAt(session.getScheduledAt())
                .status(session.getStatus())
                .build();
        return dao.update(therapySession);
    }
}
