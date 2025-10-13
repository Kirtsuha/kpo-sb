package com.zoohse.factory;

import com.zoohse.domain.animals.Animal;
import com.zoohse.domain.animals.Monkey;
import com.zoohse.domain.animals.Rabbit;
import com.zoohse.domain.animals.Tiger;
import com.zoohse.domain.animals.Wolf;
import com.zoohse.factory.impl.DefaultAnimalAbstractFactory;
import com.zoohse.factory.impl.HerbivoreFactory;
import com.zoohse.factory.impl.PredatorFactory;
import com.zoohse.factory.types.AnimalType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultAnimalAbstractFactoryTest {

    @Mock
    private AnimalFactory mockFactory1;

    @Mock
    private AnimalFactory mockFactory2;

    private DefaultAnimalAbstractFactory router;
    private HerbivoreFactory herbivoreFactory;
    private PredatorFactory predatorFactory;

    @BeforeEach
    void setUp() {
        herbivoreFactory = new HerbivoreFactory();
        predatorFactory = new PredatorFactory();
    }

    @Test
    void createAnimalWithHerbivoreTypeReturnsHerbivoreAnimal() {
        List<AnimalFactory> factories = Arrays.asList(herbivoreFactory, predatorFactory);
        router = new DefaultAnimalAbstractFactory(factories);

        Animal animal = router.createAnimal(AnimalType.MONKEY, "Charlie", 100, 80, 1, 5);

        assertNotNull(animal);
        assertInstanceOf(Monkey.class, animal);
        assertEquals("Charlie", animal.getName());
        assertEquals(100, animal.getFood());
        assertEquals(80, animal.getHealth());
        assertEquals(1, animal.getNumber());
    }

    @Test
    void createAnimalWithPredatorTypeReturnsPredatorAnimal() {
        List<AnimalFactory> factories = Arrays.asList(herbivoreFactory, predatorFactory);
        router = new DefaultAnimalAbstractFactory(factories);

        Animal animal = router.createAnimal(AnimalType.TIGER, "Tigra", 150, 90, 2);

        assertNotNull(animal);
        assertInstanceOf(Tiger.class, animal);
        assertEquals("Tigra", animal.getName());
        assertEquals(150, animal.getFood());
        assertEquals(90, animal.getHealth());
        assertEquals(2, animal.getNumber());
    }

    @Test
    void createAnimalWithAllAnimalTypesReturnsCorrectAnimals() {
        List<AnimalFactory> factories = Arrays.asList(herbivoreFactory, predatorFactory);
        router = new DefaultAnimalAbstractFactory(factories);

        Animal monkey = router.createAnimal(AnimalType.MONKEY, "Milo", 100, 80, 1, 3);
        Animal rabbit = router.createAnimal(AnimalType.RABBIT, "Bunny", 50, 70, 2, 4);
        Animal tiger = router.createAnimal(AnimalType.TIGER, "Stripes", 200, 95, 3);
        Animal wolf = router.createAnimal(AnimalType.WOLF, "Fang", 180, 85, 4);

        assertInstanceOf(Monkey.class, monkey);
        assertInstanceOf(Rabbit.class, rabbit);
        assertInstanceOf(Tiger.class, tiger);
        assertInstanceOf(Wolf.class, wolf);
    }

    @Test
    void constructorWithDuplicateFactorySupportThrowsIllegalStateException() {
        when(mockFactory1.supports(AnimalType.MONKEY)).thenReturn(true);
        when(mockFactory2.supports(AnimalType.MONKEY)).thenReturn(true);

        List<AnimalFactory> factories = Arrays.asList(mockFactory1, mockFactory2);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> new DefaultAnimalAbstractFactory(factories));

        assertTrue(exception.getMessage().contains("Multiple factories support type: MONKEY"));
    }

    @Test
    void constructorWithEmptyFactoriesCreatesEmptyMap() {
        List<AnimalFactory> emptyFactories = Collections.emptyList();

        router = new DefaultAnimalAbstractFactory(emptyFactories);

        assertNotNull(router);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> router.createAnimal(AnimalType.MONKEY, "Test", 100, 80, 1));

        assertEquals("No factory found for animal type: MONKEY", exception.getMessage());
    }

    @Test
    void createAnimalWithFactoryThatThrowsExceptionPropagatesException() {
        when(mockFactory1.supports(AnimalType.MONKEY)).thenReturn(true);
        when(mockFactory1.createAnimal(eq(AnimalType.MONKEY), anyString(), anyInt(), anyInt(), anyInt(), any()))
                .thenThrow(new IllegalArgumentException("Factory error"));

        List<AnimalFactory> factories = Collections.singletonList(mockFactory1);
        router = new DefaultAnimalAbstractFactory(factories);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> router.createAnimal(AnimalType.MONKEY, "Test", 100, 80, 1));

        assertEquals("Factory error", exception.getMessage());
    }

    @Test
    void factoryMappingCorrectlyMapsAllAnimalTypes() {
        List<AnimalFactory> factories = Arrays.asList(herbivoreFactory, predatorFactory);

        router = new DefaultAnimalAbstractFactory(factories);

        assertDoesNotThrow(() -> router.createAnimal(AnimalType.MONKEY, "M", 100, 80, 1, 1));
        assertDoesNotThrow(() -> router.createAnimal(AnimalType.RABBIT, "R", 100, 80, 1, 1));
        assertDoesNotThrow(() -> router.createAnimal(AnimalType.TIGER, "T", 100, 80, 1));
        assertDoesNotThrow(() -> router.createAnimal(AnimalType.WOLF, "W", 100, 80, 1));
    }

    @Test
    void createAnimalWithDifferentParametersPassesParametersCorrectly() {
        List<AnimalFactory> factories = Arrays.asList(herbivoreFactory, predatorFactory);
        router = new DefaultAnimalAbstractFactory(factories);

        Animal monkey = router.createAnimal(AnimalType.MONKEY, "Smart", 100, 80, 1, 7);

        assertInstanceOf(Monkey.class, monkey);
        Monkey specificMonkey = (Monkey) monkey;
        assertEquals(7, specificMonkey.getKindness());

        Animal tiger = router.createAnimal(AnimalType.TIGER, "Strong", 200, 90, 2);

        assertInstanceOf(Tiger.class, tiger);
    }
}