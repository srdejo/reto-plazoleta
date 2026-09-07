package co.com.srdejo.plazoleta.infrastructure.input.rest;

import co.com.srdejo.plazoleta.application.dto.request.DishPatchRequestDto;
import co.com.srdejo.plazoleta.application.dto.request.DishRequestDto;
import co.com.srdejo.plazoleta.application.dto.response.DishResponseDto;
import co.com.srdejo.plazoleta.application.dto.response.PageResponseDto;
import co.com.srdejo.plazoleta.application.handler.IDishHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dishes")
@RequiredArgsConstructor
public class DishRestController {

    private final IDishHandler dishHandler;

    @Operation(summary = "Add a new dish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dish created", content = @Content),
            @ApiResponse(responseCode = "409", description = "Dish already exists", content = @Content)
    })
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/")
    public ResponseEntity<Void> saveDish(@Valid @RequestBody DishRequestDto dishRequestDto) {
        dishHandler.saveDish(dishRequestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "Get all dishes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All dishs returned",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = DishResponseDto.class)))),
            @ApiResponse(responseCode = "404", description = "No data found", content = @Content)
    })
    @GetMapping()
    public ResponseEntity<PageResponseDto<DishResponseDto>> getAllDishes(
            @Parameter(description = "Page number", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of restaurants per page", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Sort direction", example = "asc")
            @RequestParam(defaultValue = "asc") String direction,

            @Parameter(description = "Category Id", example = "1")
            @RequestParam(defaultValue = "0") Long categoryId

            ) {
        return ResponseEntity.ok(dishHandler.getAllDishes(page, size, categoryId, "asc".equalsIgnoreCase(direction)));
    }

    @Operation(summary = "Partially update a dish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dish updated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DishResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Dish not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content)
    })
    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("/{id}")
    public ResponseEntity<DishResponseDto> patchDish(
            @PathVariable Long id,
            @Valid @RequestBody DishPatchRequestDto request) {
        return ResponseEntity.ok(dishHandler.patchDish(id, request));
    }


    @Operation(summary = "Enable or disable a dish")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Dish status updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DishResponseDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Dish not found", content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<DishResponseDto> updateDishStatus(
            @PathVariable Long id,
            @RequestParam boolean enabled) {

        return ResponseEntity.ok(dishHandler.updateDishStatus(id, enabled));
    }


}