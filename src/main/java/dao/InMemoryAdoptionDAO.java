package dao;

import model.Adoption;

import java.time.Month;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class InMemoryAdoptionDAO implements AdoptionDAO {
    private final List<Adoption> adoptions = new ArrayList<>();

    @Override
    public void save(Adoption adoption) {
        adoptions.add(adoption);
    }

    @Override
    public Map<Month, Integer> getMonthlyAdoptions(int year) {
        Map<Month, Integer> results = new EnumMap<>(Month.class);
        for (Adoption adoption : adoptions) {
            if (adoption.getPlacementDate().getYear() == year) {
                Month month = adoption.getPlacementDate().getMonth();
                results.put(month, results.getOrDefault(month, 0) + 1);
            }
        }
        return results;
    }
}
