package lk.ijse.serenity.dao;

import lk.ijse.serenity.config.FactoryConfiguration;
import lk.ijse.serenity.entity.TherapyProgram;
import org.hibernate.Session;

import java.util.List;

public class TherapyProgramDAOImpl extends CrudDAOImpl<TherapyProgram>{
    public TherapyProgramDAOImpl() { super(TherapyProgram.class); }

}
