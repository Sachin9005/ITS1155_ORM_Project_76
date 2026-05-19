package lk.ijse.serenity.bo;

import lk.ijse.serenity.dao.TherapyProgramDAOImpl;
import lk.ijse.serenity.dto.TherapyProgramDTO;
import lk.ijse.serenity.entity.TherapyProgram;

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
                .programId(tp.getProgramId())
                .name(tp.getName())
                .duration(tp.getDuration())
                .fee(tp.getFee())
                .description(tp.getDescription())
                .build()).toList();
    }
}
