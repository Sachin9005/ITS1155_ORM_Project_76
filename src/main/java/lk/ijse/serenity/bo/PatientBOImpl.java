package lk.ijse.serenity.bo;

import lk.ijse.serenity.dao.PatientDAOImpl;
import lk.ijse.serenity.dto.PatientDTO;
import lk.ijse.serenity.entity.Patient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PatientBOImpl {
    private PatientDAOImpl patientDAO = new PatientDAOImpl();

    public boolean savePatient(PatientDTO patientDTO) {
        Patient patient = Patient.builder().
                name(patientDTO.getName()).
                email(patientDTO.getEmail()).
                phone(patientDTO.getPhone()).
                address(patientDTO.getAddress()).
                dateOfBirth(patientDTO.getDob()).
                registrationDate(LocalDate.now()).
                emergencyContact(patientDTO.getEmergencyContact()).
                build();

        return patientDAO.save(patient);

    }

    public boolean updatePatient(PatientDTO patientDTO) {
        Patient patient = Patient.builder().
                name(patientDTO.getName()).
                email(patientDTO.getEmail()).
                phone(patientDTO.getPhone()).
                address(patientDTO.getAddress()).
                dateOfBirth(patientDTO.getDob()).
                registrationDate(LocalDate.now()).
                emergencyContact(patientDTO.getEmergencyContact()).
                build();

        return patientDAO.update(patient);
    }

    public boolean deletePatient(PatientDTO patientDTO) {
        Patient patient = Patient.builder().
                name(patientDTO.getName()).
                email(patientDTO.getEmail()).
                phone(patientDTO.getPhone()).
                address(patientDTO.getAddress()).
                dateOfBirth(patientDTO.getDob()).
                registrationDate(LocalDate.now()).
                emergencyContact(patientDTO.getEmergencyContact()).
                build();
        return patientDAO.delete(patient);
    }

    public List<PatientDTO> getAllPatients() {
        List<Patient> patients = patientDAO.getAll();
        return patients.stream().map(p -> PatientDTO.builder()
                .name(p.getName())
                .email(p.getEmail())
                .phone(p.getPhone())
                .address(p.getAddress())
                .dob(p.getDateOfBirth())
                .emergencyContact(p.getEmergencyContact())
                .build()).toList();
    }

    public List<PatientDTO> findPatientsEnrolledInAllPrograms() {
        List<Patient> patients = patientDAO.findPatientsEnrolledInAllPrograms();

        List<PatientDTO> patientDTOList = new ArrayList<>();
        for (Patient patient : patients) {
            patientDTOList.add(PatientDTO.builder()
                    .name(patient.getName())
                    .email(patient.getEmail())
                    .phone(patient.getPhone())
                    .address(patient.getAddress())
                    .dob(patient.getDateOfBirth())
                    .emergencyContact(patient.getEmergencyContact())
                    .build());
        }
        return patientDTOList;
    }

    public List<PatientDTO> search(String keyword) {
        List<Patient> patients = patientDAO.search(keyword);

        List<PatientDTO> patientDTOList = new ArrayList<>();
        for (Patient patient : patients) {
            patientDTOList.add(PatientDTO.builder()
                    .name(patient.getName())
                    .email(patient.getEmail())
                    .phone(patient.getPhone())
                    .address(patient.getAddress())
                    .dob(patient.getDateOfBirth())
                    .emergencyContact(patient.getEmergencyContact())
                    .build());
    }
    return patientDTOList;
    }
}
