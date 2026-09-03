package co.com.srdejo.plazoleta.application.dto.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class DishRequestDtoValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    private DishRequestDto validDish() {
        DishRequestDto dto = new DishRequestDto();
        dto.setName("Hamburguesa Mixta");
        dto.setPrice(new BigDecimal(15000));
        dto.setDishCategoryId(1L);
        dto.setDescription("Hamburguesa con carne y pollo");
        dto.setUrlImage("http://image.png");
        dto.setRestaurantId(1L);
        return dto;
    }

    @Test
    void validDish_hasNoViolations() {
        Set<ConstraintViolation<DishRequestDto>> violations = validator.validate(validDish());

        assertThat(violations).isEmpty();
    }

    static Stream<Arguments> missingRequiredFields() {
        return Stream.of(
                Arguments.of("name", (java.util.function.Consumer<DishRequestDto>) dto -> dto.setName(" ")),
                Arguments.of("description", (java.util.function.Consumer<DishRequestDto>) dto -> dto.setDescription(" ")),
                Arguments.of("urlImage", (java.util.function.Consumer<DishRequestDto>) dto -> dto.setUrlImage(" ")),
                Arguments.of("dishCategoryId", (java.util.function.Consumer<DishRequestDto>) dto -> dto.setDishCategoryId(null)),
                Arguments.of("restaurantId", (java.util.function.Consumer<DishRequestDto>) dto -> dto.setRestaurantId(null))
        );
    }

    @ParameterizedTest(name = "missing {0} produces a violation")
    @MethodSource("missingRequiredFields")
    void missingRequiredFields_eachProducesAViolation(String property, java.util.function.Consumer<DishRequestDto> mutator) {
        DishRequestDto dto = validDish();
        mutator.accept(dto);

        Set<ConstraintViolation<DishRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains(property);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void price_zeroOrNegative_producesViolation(int price) {
        DishRequestDto dto = validDish();
        dto.setPrice(new BigDecimal(price));

        Set<ConstraintViolation<DishRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("price");
    }

    @Test
    void price_nonInteger_producesViolation() {
        DishRequestDto dto = validDish();
        dto.setPrice(new BigDecimal("10.5"));

        Set<ConstraintViolation<DishRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .contains("price");
    }

    @Test
    void price_positiveInteger_isValid() {
        DishRequestDto dto = validDish();
        dto.setPrice(BigDecimal.ONE);

        Set<ConstraintViolation<DishRequestDto>> violations = validator.validate(dto);

        assertThat(violations)
                .extracting(v -> v.getPropertyPath().toString())
                .doesNotContain("price");
    }
}
