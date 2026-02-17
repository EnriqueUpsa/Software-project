package dao;

import model.Adoption;

import java.time.Month;
import java.util.Map;

/**
 * Data access contract for adoptions and adoption statistics.
 */
public interface AdoptionDAO {
    void save(Adoption adoption);

    Map<Month, Integer> getMonthlyAdoptions(int year);
}
