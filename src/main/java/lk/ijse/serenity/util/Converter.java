package lk.ijse.serenity.util;

import lk.ijse.serenity.dto.*;
import lk.ijse.serenity.entity.*;

public class Converter {

    public static PatientDTO toPatientDTO(Patient patient) {
        if (patient == null) return null;
        return PatientDTO.builder()
                .id(patient.getId())
                .name(patient.getName())
                .email(patient.getEmail())
                .phone(patient.getPhone())
                .address(patient.getAddress())
                .dob(patient.getDateOfBirth())
                .registrationDate(patient.getRegistrationDate())
                .emergencyContact(patient.getEmergencyContact())
                .build();
    }

    public static Patient toPatientEntity(PatientDTO dto) {
        if (dto == null) return null;
        Patient patient = new Patient();
        patient.setId(dto.getId());
        patient.setName(dto.getName());
        patient.setEmail(dto.getEmail());
        patient.setPhone(dto.getPhone());
        patient.setAddress(dto.getAddress());
        patient.setDateOfBirth(dto.getDob());
        patient.setRegistrationDate(dto.getRegistrationDate());
        patient.setEmergencyContact(dto.getEmergencyContact());
        return patient;
    }

    public static TherapyProgramDTO toTherapyProgramDTO(TherapyProgram therapyProgram) {
       if (therapyProgram == null) return null;
       return TherapyProgramDTO.builder()
                .programId(String.valueOf(therapyProgram.getId()))
                .name(therapyProgram.getName())
                .duration(therapyProgram.getDuration())
                .fee(therapyProgram.getFee())
                .description(therapyProgram.getDescription())
                .build();
    }

    public static TherapyProgram  toTherapyProgramEntity(TherapyProgramDTO dto) {
        if (dto == null) return null;
        TherapyProgram therapyProgram = new TherapyProgram();
        therapyProgram.setId(dto.getProgramId() != null ? Long.parseLong(dto.getProgramId()) : null);
        therapyProgram.setName(dto.getName());
        therapyProgram.setDuration(dto.getDuration());
        therapyProgram.setFee(dto.getFee());
        therapyProgram.setDescription(dto.getDescription());
        return therapyProgram;
    }

    public static TherapistDTO toTherapistDTO(Therapist therapist) {
        if (therapist == null) return null;
        return TherapistDTO.builder()
                .id(therapist.getId())
                .name(therapist.getName())
                .specialization(therapist.getSpecialization())
                .email(therapist.getEmail())
                .phone(therapist.getPhone())
                .availability(therapist.getAvailability())
                .qualification(therapist.getQualification())
                .build();
    }

    public static Therapist toTherapistEntity(TherapistDTO dto) {
        if (dto == null) return null;
        Therapist therapist = new Therapist();
        therapist.setId(dto.getId());
        therapist.setName(dto.getName());
        therapist.setSpecialization(dto.getSpecialization());
        therapist.setEmail(dto.getEmail());
        therapist.setPhone(dto.getPhone());
        therapist.setAvailability(dto.getAvailability());
        therapist.setQualification(dto.getQualification());
        return therapist;
    }
    public static TherapySessionDTO  toTherapySession(TherapySession therapySession) {
        if (therapySession == null) return null;
        return TherapySessionDTO.builder()
                .id(therapySession.getId())
                .patient(therapySession.getPatient())
                .therapist(therapySession.getTherapist())
                .therapyProgram(therapySession.getTherapyProgram())
                .scheduledAt(therapySession.getScheduledAt())
                .status(therapySession.getStatus())
                .build();
    }

    public static TherapySession toTherapySessionEntity(TherapySessionDTO dto) {
        if (dto == null) return null;
        TherapySession therapySession = new TherapySession();
        therapySession.setId(dto.getId());
        therapySession.setPatient(dto.getPatient());
        therapySession.setTherapist(dto.getTherapist());
        therapySession.setTherapyProgram(dto.getTherapyProgram());
        therapySession.setScheduledAt(dto.getScheduledAt());
        therapySession.setStatus(dto.getStatus());
        return therapySession;
    }

    public static UserDTO toUserDTO(User user) {
        if (user == null) return null;
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .passwordHash(user.getPasswordHash())
                .role(user.getRole())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();
    }
    public static User toUserEntity(UserDTO dto) {
        if (dto == null) return null;
        return User.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .passwordHash(dto.getPasswordHash())
                .role(dto.getRole())
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .build();
    }



}
