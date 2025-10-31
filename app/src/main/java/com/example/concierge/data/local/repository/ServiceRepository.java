package com.example.concierge.data.local.repository;

import android.content.Context;
import com.example.concierge.data.local.DatabaseClient;
import com.example.concierge.data.local.entity.Service;
import java.util.List;

public class ServiceRepository {
    private final DatabaseClient databaseClient;

    public ServiceRepository(Context context) {
        this.databaseClient = DatabaseClient.getInstance(context);
    }

    public long insertService(Service service) {
        return databaseClient.getAppDatabase().serviceDao().insert(service);
    }

    public void updateService(Service service) {
        databaseClient.getAppDatabase().serviceDao().update(service);
    }

    public void deleteService(Service service) {
        databaseClient.getAppDatabase().serviceDao().delete(service);
    }

    public Service getServiceById(int id) {
        return databaseClient.getAppDatabase().serviceDao().getById(id);
    }

    public List<Service> getServicesByCategory(int categoryId) {
        return databaseClient.getAppDatabase().serviceDao().getByCategory(categoryId);
    }

    public List<Service> getAllAvailableServices() {
        return databaseClient.getAppDatabase().serviceDao().getAllAvailable();
    }

    public List<Service> searchServices(String search) {
        return databaseClient.getAppDatabase().serviceDao().searchServices(search);
    }

    // Бизнес-логика: переключение доступности услуги
    public void toggleServiceAvailability(int serviceId, boolean available) {
        Service service = getServiceById(serviceId);
        if (service != null) {
            service.is_available = available;
            updateService(service);
        }
    }

    // Бизнес-логика: получение популярных услуг
    public List<Service> getPopularServices() {
        List<Service> services = getAllAvailableServices();
        // Здесь можно добавить логику сортировки по популярности
        return services.subList(0, Math.min(services.size(), 5)); // Топ-5 услуг
    }
}
