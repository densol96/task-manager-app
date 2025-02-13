package com.accenture.backend.repository;

import com.accenture.backend.entity.TaskLabel;
import com.accenture.backend.entity.Task;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class TaskLabelRepositoryTest {

    @Container
    private static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0");

    @Autowired
    private TaskLabelRepository taskLabelRepository;

    @Autowired
    private TaskRepository taskRepository;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", mySQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    private Long taskLabelId;

    @BeforeEach
    void setUp() {

        TaskLabel taskLabel = new TaskLabel();
        taskLabel.setTitle("Urgent");
        taskLabel.setColor("Red");

        TaskLabel savedTaskLabel = taskLabelRepository.save(taskLabel);
        taskLabelId = savedTaskLabel.getId();
    }

    @AfterEach
    void tearDown() {
        taskLabelRepository.deleteAll();
    }

    @Test
    void testFindById_Exists() {
        Optional<TaskLabel> taskLabelOptional = taskLabelRepository.findById(taskLabelId);
        assertTrue(taskLabelOptional.isPresent(), "TaskLabel should be found");
        assertEquals("Urgent", taskLabelOptional.get().getTitle(), "TaskLabel title should match");
    }

    @Test
    void testFindById_NotExists() {
        Optional<TaskLabel> taskLabelOptional = taskLabelRepository.findById(24L);
        assertFalse(taskLabelOptional.isPresent(), "TaskLabel should not be found");
    }

    @Test
    void testSaveTaskLabel() {
        TaskLabel taskLabel = new TaskLabel();
        taskLabel.setTitle("Important");
        taskLabel.setColor("Blue");

        TaskLabel savedTaskLabel = taskLabelRepository.save(taskLabel);

        assertNotNull(savedTaskLabel.getId(), "Saved TaskLabel should have a generated ID");
        assertEquals("Important", savedTaskLabel.getTitle(), "TaskLabel title should match");
    }
}