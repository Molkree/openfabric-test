package ai.openfabric.api.controller;

import ai.openfabric.api.model.Worker;
import ai.openfabric.api.repository.WorkerRepository;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping("${node.api.path}/worker")
public class WorkerController {
    private final DockerClient dockerClient;

    public WorkerController() {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        DockerHttpClient dockerHttpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(30))
                .responseTimeout(Duration.ofSeconds(45)).build();
        this.dockerClient = DockerClientImpl.getInstance(config, dockerHttpClient);
    }

    @Autowired
    private WorkerRepository workerRepository;

    @PostMapping("/start")
    public String startWorker(@RequestBody Worker.Info workerInfo) {
        Worker worker = new Worker(workerInfo);
        String serializedPort = worker.getPort() + "/" + worker.getProtocol();
        ExposedPort exposedPort = ExposedPort.parse(serializedPort);
        Ports portBindings = new Ports();
        portBindings.bind(exposedPort, Ports.Binding.bindPort(Integer.parseInt(worker.getPort())));
        CreateContainerResponse container = this.dockerClient.createContainerCmd("java:latest")
                .withName(worker.getName() + "+" + worker.getId())
                .withHostName(worker.getDomain())
                .withExposedPorts(exposedPort)
                .withHostConfig(new HostConfig()
                        .withPortBindings(portBindings))
                .exec();
        worker.setContainerId(container.getId());
        this.dockerClient.startContainerCmd(container.getId()).exec();
        workerRepository.save(worker);
        return worker.getId();
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
            this.dockerClient.stopContainerCmd(worker.getContainerId()).exec();
            worker.deletedAt = new Date();
            workerRepository.save(worker);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No worker with such ID found");
        }
        return String.format("Stopped worker %s", id);
    }

    @GetMapping("/info/{id}")
    public Worker getWorker(@PathVariable String id) {
        Optional<Worker> optionalWorker = workerRepository.findById(id);
        if (optionalWorker.isPresent()) {
            return optionalWorker.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No worker with such ID found");
        }
    }
}
