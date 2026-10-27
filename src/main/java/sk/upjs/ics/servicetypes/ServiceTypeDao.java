package sk.upjs.ics.servicetypes;

import java.io.File;
import java.util.List;

public interface ServiceTypeDao {
    int loadFromCsv(File file);

    List<ServiceType> findAll();

    ServiceType findById(Long id);
}
