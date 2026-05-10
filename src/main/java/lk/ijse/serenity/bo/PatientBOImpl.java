package lk.ijse.serenity.bo;

import lk.ijse.serenity.dao.PatientDAOImpl;
import lk.ijse.serenity.dto.PatientDTO;
import lk.ijse.serenity.entity.Patient;

import java.time.LocalDate;

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
}
