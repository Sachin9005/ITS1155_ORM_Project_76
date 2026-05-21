package lk.ijse.serenity.bo;

import lk.ijse.serenity.dao.TherapyProgramDAOImpl;
import lk.ijse.serenity.dto.TherapyProgramDTO;
import lk.ijse.serenity.entity.TherapyProgram;

import java.math.BigDecimal;
import java.util.List;

public class TherapyProgramBOImpl {

    TherapyProgramDAOImpl  therapyProgramDAO =  new TherapyProgramDAOImpl();

    public boolean saveTherapyProgram(TherapyProgramDTO therapyProgram){

        TherapyProgram therapyProgramEntity = TherapyProgram.builder()
                .programId(therapyProgram.getProgramId())
                .name(therapyProgram.getName())
                .duration(therapyProgram.getDuration())
                .fee(therapyProgram.getFee())
                .description(therapyProgram.getDescription())
                .build();
        return therapyProgramDAO.save(therapyProgramEntity);
    }

    public boolean updateTherapyProgram(TherapyProgramDTO therapyProgram){

        TherapyProgram therapyProgramEntity = TherapyProgram.builder()
                .programId(therapyProgram.getProgramId())
                .name(therapyProgram.getName())
                .duration(therapyProgram.getDuration())
                .fee(therapyProgram.getFee())
                .description(therapyProgram.getDescription())
                .build();
        return therapyProgramDAO.update(therapyProgramEntity);
    }

     public boolean deleteTherapyProgram(TherapyProgramDTO therapyProgramDTO){

        TherapyProgram therapyProgram = TherapyProgram.builder()
                .programId(therapyProgramDTO.getProgramId())
                .name(therapyProgramDTO.getName())
                .duration(therapyProgramDTO.getDuration())
                .fee(therapyProgramDTO.getFee())
                .description(therapyProgramDTO.getDescription())
                .build();
        return therapyProgramDAO.delete(therapyProgram);
    }

    public List<TherapyProgramDTO> getAllTherapyPrograms(){
        List<TherapyProgram> therapyPrograms = therapyProgramDAO.getAll();
        return therapyPrograms.stream().map(tp -> TherapyProgramDTO.builder()
                .id(tp.getId())
                .programId(tp.getProgramId())
                .name(tp.getName())
                .duration(tp.getDuration())
                .fee(tp.getFee())
                .description(tp.getDescription())
                .build()).toList();
    }

    public void seedDefaultPrograms() {
        if (therapyProgramDAO.getAll().isEmpty()) {
            saveTherapyProgram(new TherapyProgramDTO("MT1001", "Cognitive Behavioral Therapy", "12 weeks",
                    new BigDecimal("80000.00"), "Evidence-based talk therapy."));
            saveTherapyProgram(new TherapyProgramDTO("MT1002", "Mindfulness-Based Stress Reduction", "8 weeks",
                    new BigDecimal("50000.00"), "Mindfulness techniques for stress."));
            saveTherapyProgram(new TherapyProgramDTO("MT1003", "Dialectical Behavior Therapy", "16 weeks",
                    new BigDecimal("100000.00"), "DBT for emotional regulation."));
            saveTherapyProgram(new TherapyProgramDTO("MT1004", "Group Therapy Sessions", "6 months",
                    new BigDecimal("120000.00"), "Peer-supported group sessions."));
            saveTherapyProgram(new TherapyProgramDTO("MT1005", "Family Counseling", "3 months",
                    new BigDecimal("40000.00"), "Family-focused counseling."));
        }
    }
}
