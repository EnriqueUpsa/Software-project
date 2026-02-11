package dao;

import model.Adoption;

import java.time.Month;
import java.util.Map;

public interface AdoptionDAO {
    void save(Adoption adoption);

    Map<Month, Integer> getMonthlyAdoptions(int year);
}
