package ai.openfabric.api.repository;

import ai.openfabric.api.model.Worker;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface WorkerRepository extends CrudRepository<Worker, String>, PagingAndSortingRepository<Worker, String> {

}
