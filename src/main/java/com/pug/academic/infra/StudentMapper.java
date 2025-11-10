package com.pug.academic.infra;

import com.pug.academic.domain.Student;
import com.pug.academic.domain.enums.Campi;
import com.pug.academic.domain.vos.AcademicRegistration;
import com.pug.academic.domain.vos.CounterpartHours;
import com.pug.academic.domain.vos.Period;
import com.pug.academic.infra.persistence.StudentEntity;

public final class StudentMapper {
    private StudentMapper() {
    }

    public static Student toDomain(StudentEntity e) {
        if (e == null) return null;
        Campi campusEnum = decodeCampus(e.getCampus());
        return Student.builder()
                .userId(e.getUserId())
                .academicRegistration(new AcademicRegistration(e.getAcademicRegistration()))
                .campus(campusEnum)
                .courseId(e.getCourseId())
                .counterpartHours(new CounterpartHours(e.getRequiredHours(), e.getCompletedHours()))
                .period(new Period(e.getStartDate(), e.getDueDate()))
                .build();
    }

    public static StudentEntity toEntity(Student d) {
        if (d == null) return null;
        StudentEntity e = new StudentEntity();
        copy(d, e);
        return e;
    }

    public static void copy(Student d, StudentEntity e) {
        if (d == null || e == null) return;
        e.setUserId(d.getUserId());
        e.setAcademicRegistration(d.getAcademicRegistration().toString());
        e.setCampus(encodeCampus(d.getCampus()));
        e.setCourseId(d.getCourseId());
        e.setRequiredHours(d.getCounterpartHours().requiredHours());
        e.setCompletedHours(d.getCounterpartHours().completedHours());
        e.setStartDate(d.getPeriod().startDate());
        e.setDueDate(d.getPeriod().dueDate());
    }

    private static String encodeCampus(Campi c) {
        return c.name();
    } // store enum name for reversibility

    private static Campi decodeCampus(String raw) {
        if (raw == null) return null;
        try {
            return Campi.valueOf(raw);
        } catch (IllegalArgumentException ex) {
            // fallback by description
            for (Campi c : Campi.values()) if (c.getDescription().equalsIgnoreCase(raw)) return c;
            return null;
        }
    }
}
