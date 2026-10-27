package sk.upjs.ics.servicetypes;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MemoryServiceTypeDao implements ServiceTypeDao {

    private final List<ServiceType> serviceTypes;

    public MemoryServiceTypeDao(List<ServiceType> serviceTypes) {
        this.serviceTypes = serviceTypes;
    }

    @Override
    public int loadFromCsv(File file) {
        var existingIds = serviceTypes.stream()
                .map(ServiceType::getId)
                .collect(Collectors.toSet());

        var serviceTypes = new ArrayList<ServiceType>();
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        scanner.nextLine();
        while (scanner.hasNextLine()) {
            var line = scanner.nextLine();
            if (line.isBlank()) {
                continue;
            }

            var parts = line.split(",");

            var id = Long.parseLong(parts[0]);
            if (existingIds.contains(id)) {
                continue;
            }

            var serviceType = new ServiceType();
            serviceType.setId(id);

            serviceType.setName(parts[1]);

            serviceType.setPrice(Double.parseDouble(parts[2]));
            serviceTypes.add(serviceType);
        }

        this.serviceTypes.addAll(serviceTypes);
        return serviceTypes.size();
    }

    @Override
    public List<ServiceType> findAll() {
        return serviceTypes;
    }

    @Override
    public ServiceType findById(Long id) {
        return serviceTypes.stream()
                .filter(serviceType -> serviceType.getId() == id)
                .findFirst()
                .orElse(null);
    }
}
