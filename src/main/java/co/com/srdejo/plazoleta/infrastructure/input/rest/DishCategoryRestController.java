package co.com.srdejo.plazoleta.infrastructure.input.rest;

import co.com.srdejo.plazoleta.application.dto.request.DishCategoryRequestDto;
import co.com.srdejo.plazoleta.application.dto.response.DishCategoryResponseDto;
import co.com.srdejo.plazoleta.application.handler.IDishCategoryHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dish_categories")
@RequiredArgsConstructor
public class DishCategoryRestController {

    private final IDishCategoryHandler dishHandler;

    @Operation(summary = "Add a new dish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "DishCategory created", content = @Content),
            @ApiResponse(responseCode = "409", description = "DishCategory already exists", content = @Content)
    })
    @PostMapping("/")
    public ResponseEntity<Void> saveDishCategory(@RequestBody DishCategoryRequestDto dishRequestDto) {
        dishHandler.saveDishCategory(dishRequestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Get all dishs")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All dishs returned",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = DishCategoryResponseDto.class)))),
            @ApiResponse(responseCode = "404", description = "No data found", content = @Content)
    })
    @GetMapping("/")
    public ResponseEntity<List<DishCategoryResponseDto>> getAllDishCategorys() {
        return ResponseEntity.ok(dishHandler.getAllDishCategories());
    }

}