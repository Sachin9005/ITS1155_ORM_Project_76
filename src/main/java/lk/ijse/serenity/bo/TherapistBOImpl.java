package lk.ijse.serenity.bo;

import lk.ijse.serenity.dao.TherapistDAOImpl;
import lk.ijse.serenity.dto.TherapistDTO;
import lk.ijse.serenity.entity.Therapist;

import java.util.ArrayList;
import java.util.List;

public class TherapistBOImpl {

    private TherapistDAOImpl therapistDAO = new TherapistDAOImpl();

    public boolean saveTherapist(TherapistDTO therapist) {

        Therapist therapistEntity = Therapist.builder()
                .name(therapist.getName())
                .specialization(therapist.getSpecialization())
                .email(therapist.getEmail())
                .phone(therapist.getPhone())
                .availability(therapist.getAvailability())
                .qualification(therapist.getQualification())
                .build();

        return therapistDAO.save(therapistEntity);
    }

    public boolean updateTherapist(TherapistDTO therapist) {

        Therapist therapistEntity = Therapist.builder()
                .name(therapist.getName())
                .specialization(therapist.getSpecialization())
                .email(therapist.getEmail())
                .phone(therapist.getPhone())
                .availability(therapist.getAvailability())
                .qualification(therapist.getQualification())
                .build();

        return therapistDAO.update(therapistEntity);
    }

    public boolean deleteTherapist(TherapistDTO therapist) {

        Therapist therapistEntity = Therapist.builder()
                .name(therapist.getName())
                .specialization(therapist.getSpecialization())
                .email(therapist.getEmail())
                .phone(therapist.getPhone())
                .availability(therapist.getAvailability())
                .qualification(therapist.getQualification())
                .build();

        return therapistDAO.delete(therapistEntity);
    }

    public List<TherapistDTO> getAllTherapists() {

        List<Therapist> therapists =  therapistDAO.findAll();

        List<TherapistDTO> therapistDTOList = new ArrayList<>();
        for (Therapist therapist : therapists) {
            therapistDTOList.add(TherapistDTO.builder()
                    .id(therapist.getId())
                    .name(therapist.getName())
                    .specialization(therapist.getSpecialization())
                    .email(therapist.getEmail())
                    .phone(therapist.getPhone())
                    .availability(therapist.getAvailability())
                    .qualification(therapist.getQualification())
                    .build());
        }
        return therapistDTOList;


    }
}