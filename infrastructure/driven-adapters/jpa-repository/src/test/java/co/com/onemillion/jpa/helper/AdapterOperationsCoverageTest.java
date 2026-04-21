package co.com.onemillion.jpa.helper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdapterOperationsCoverageTest {
    @Mock
    private TestRepository repository;

    @Mock
    private ObjectMapper mapper;

    private TestAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adapter = new TestAdapter(repository, mapper);
    }

    @Test
    void shouldSaveEntity() {
        TestEntity entity = new TestEntity("Ana");
        TestData data = new TestData("Ana");

        when(mapper.map(entity, TestData.class)).thenReturn(data);
        when(repository.save(data)).thenReturn(data);

        TestEntity result = adapter.save(entity);

        assertEquals("Ana", result.name());
        verify(mapper).map(entity, TestData.class);
        verify(repository).save(data);
    }

    @Test
    void shouldSaveAllEntities() {
        TestEntity first = new TestEntity("Ana");
        TestEntity second = new TestEntity("Luis");
        TestData firstData = new TestData("Ana");
        TestData secondData = new TestData("Luis");

        when(mapper.map(first, TestData.class)).thenReturn(firstData);
        when(mapper.map(second, TestData.class)).thenReturn(secondData);
        when(repository.saveAll(List.of(firstData, secondData))).thenReturn(List.of(firstData, secondData));

        List<TestEntity> result = adapter.saveAll(List.of(first, second));

        assertEquals(List.of("Ana", "Luis"), result.stream().map(TestEntity::name).toList());
    }

    @Test
    void shouldFindById() {
        TestData data = new TestData("Ana");
        when(repository.findById(1L)).thenReturn(Optional.of(data));

        TestEntity result = adapter.findById(1L);

        assertEquals("Ana", result.name());
    }

    @Test
    void shouldReturnNullWhenFindByIdDoesNotExist() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        TestEntity result = adapter.findById(99L);

        assertNull(result);
    }

    @Test
    void shouldFindByExample() {
        TestEntity entity = new TestEntity("Ana");
        TestData data = new TestData("Ana");
        when(mapper.map(entity, TestData.class)).thenReturn(data);
        when(repository.findAll(any(Example.class))).thenReturn(List.of(data));

        List<TestEntity> result = adapter.findByExample(entity);

        assertEquals(1, result.size());
        assertEquals("Ana", result.getFirst().name());
    }

    @Test
    void shouldFindAll() {
        when(repository.findAll()).thenReturn(List.of(new TestData("Ana"), new TestData("Luis")));

        List<TestEntity> result = adapter.findAll();

        assertEquals(List.of("Ana", "Luis"), result.stream().map(TestEntity::name).toList());
    }

    private interface TestRepository extends CrudRepository<TestData, Long>, QueryByExampleExecutor<TestData> {
    }

    private static final class TestAdapter extends AdapterOperations<TestEntity, TestData, Long, TestRepository> {
        TestAdapter(TestRepository repository, ObjectMapper mapper) {
            super(repository, mapper, data -> data == null ? null : new TestEntity(data.name()));
        }

        List<TestEntity> saveAll(List<TestEntity> entities) {
            return saveAllEntities(entities);
        }
    }

    private record TestEntity(String name) {
    }

    private record TestData(String name) {
    }
}
