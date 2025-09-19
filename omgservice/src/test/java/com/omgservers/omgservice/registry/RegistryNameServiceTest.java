package com.omgservers.omgservice.registry;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
class RegistryNameServiceTest extends Assertions {

    @Inject
    RegistryNameService registryNameService;

    @Test
    void test() {
        assertEquals("myproject", registryNameService.prepare("MyProject", "default"));
        assertEquals("my-project", registryNameService.prepare("My-Project", "default"));
        assertEquals("my-project", registryNameService.prepare("My_Project", "default"));
        assertEquals("my-project", registryNameService.prepare("My.Project", "default"));
        assertEquals("myproject", registryNameService.prepare("MYPROJECT", "default"));
        assertEquals("my-project", registryNameService.prepare("  MY PROJECT ", "default"));
        assertEquals("my-pr-o-ject", registryNameService.prepare("@MY###pr?O(JECT!", "default"));
        assertEquals("myproject", registryNameService.prepare("...mypRoject..", "default"));
        assertEquals("myproject", registryNameService.prepare("-mypRoject--", "default"));
        assertEquals("mypr-oject", registryNameService.prepare("__mypR___oject_", "default"));
        assertEquals("myproject", registryNameService.prepare("Myproject", "default"));
        assertEquals("default", registryNameService.prepare("工具测试", "default"));
        assertEquals("default", registryNameService.prepare("ПроЕкт!", "default"));
        assertEquals("super", registryNameService.prepare("Super.ПроЕкт!", "default"));
        assertEquals("123name", registryNameService.prepare("123name", "default"));
        assertEquals("default", registryNameService.prepare("", "default"));
    }
}