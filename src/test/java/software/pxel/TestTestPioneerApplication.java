package software.pxel;

import org.springframework.boot.SpringApplication;

public class TestTestPioneerApplication {

    public static void main(String[] args) {
        SpringApplication.from(TestPioneerApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
