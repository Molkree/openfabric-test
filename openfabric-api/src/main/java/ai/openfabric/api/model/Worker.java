package ai.openfabric.api.model;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity()
public class Worker extends Datable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "of-uuid")
    @GenericGenerator(name = "of-uuid", strategy = "ai.openfabric.api.model.IDGenerator")
    @Getter
    @Setter
    public String id;

    @Getter
    @Setter
    public String name;

    @Getter
    @Setter
    public String port;

    @Getter
    @Setter
    public String protocol;

    @Getter
    @Setter
    public String address;

    @Getter
    @Setter
    public String domain;

    @Getter
    @Setter
    public String specifications;

    @Getter
    @Setter
    public String cpu;

    @Getter
    @Setter
    public String gpu;

    @Getter
    @Setter
    public String memory;

    @Getter
    @Setter
    public Boolean recommended = true;

    @Getter
    @Setter
    public String containerId;

    public static class Info {
        @Getter
        @Setter
        public String name;

        @Getter
        @Setter
        public String port = "8080";

        @Getter
        @Setter
        public String protocol;

        @Getter
        @Setter
        public String address;

        @Getter
        @Setter
        public String domain;

        @Getter
        @Setter
        public String specifications;

        @Getter
        @Setter
        public String cpu;

        @Getter
        @Setter
        public String gpu;

        @Getter
        @Setter
        public String memory;
    }

    public Worker(Info info) {
        this.name = info.getName();
        this.port = info.getPort();
        this.protocol = info.getProtocol();
        this.address = info.getAddress();
        this.domain = info.getDomain();
        this.specifications = info.getSpecifications();
        this.cpu = info.getCpu();
        this.gpu = info.getGpu();
        this.memory = info.getMemory();
    }

    public Worker() {
    }
}
