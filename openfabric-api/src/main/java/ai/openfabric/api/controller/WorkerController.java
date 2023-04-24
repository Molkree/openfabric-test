package ai.openfabric.api.controller;

import ai.openfabric.api.model.Worker;
import ai.openfabric.api.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("${node.api.path}/worker")
public class WorkerController {

    @Autowired
    private WorkerRepository workerRepository;

    @PostMapping("/start")
    public String startWorker() {
        Worker worker = new Worker();
        workerRepository.save(worker);
        return worker.id;
    }

    @GetMapping("/list")
    public Iterable<Worker> getWorkers() {
        return workerRepository.findAll();
    }

    @DeleteMapping("/stop/{id}")
    public String stopWorker(@PathVariable String id) {
        Optional<Worker> optionalWorker = workerRepository.findById(id);
        if (optionalWorker.isPresent()) {
            Worker worker = optionalWorker.get();
            worker.deletedAt = new Date();
            workerRepository.save(worker);
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No worker with such ID found");
        }
        return String.format("Stopped worker %s", id);
    }
}
