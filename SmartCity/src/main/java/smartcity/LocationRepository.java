package smartcity;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface LocationRepository extends CrudRepository<Location, Long> {
	List<Location> findByLatitudeAndLongitude(double latitude, double longitude);
}