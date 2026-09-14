package co.com.srdejo.plazoleta.infrastructure.input.rest;

import co.com.srdejo.plazoleta.application.dto.request.OrderPinRequestDto;
import co.com.srdejo.plazoleta.application.dto.request.OrderRequestDto;
import co.com.srdejo.plazoleta.application.dto.response.DishResponseDto;
import co.com.srdejo.plazoleta.application.dto.response.FullOrderResponseDto;
import co.com.srdejo.plazoleta.application.dto.response.OrderResponseDto;
import co.com.srdejo.plazoleta.application.dto.response.PageResponseDto;
import co.com.srdejo.plazoleta.application.handler.IOrderHandler;
import co.com.srdejo.plazoleta.domain.model.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderRestController {

    private final IOrderHandler orderHandler;

    @Operation(summary = "Make a order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurant created", content = @Content),
            @ApiResponse(responseCode = "409", description = "Restaurant already exists", content = @Content)
    })
    @PostMapping()
    public ResponseEntity<OrderResponseDto> saveRestaurant(@RequestBody OrderRequestDto orderRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderHandler.createOrder(orderRequestDto));
    }

    @Operation(summary = "Get all orders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All orders returned",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = DishResponseDto.class)))),
            @ApiResponse(responseCode = "404", description = "No data found", content = @Content)
    })
    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping()
    public ResponseEntity<PageResponseDto<FullOrderResponseDto>> getAllOrders(
            @Parameter(description = "Page number", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of restaurants per page", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(description = "Sort direction", example = "asc")
            @RequestParam(defaultValue = "asc") String direction,

            @Parameter(description = "Order status; omit to return orders in any status", example = "PENDING")
            @RequestParam(required = false) OrderStatus orderStatus

    ) {
        return ResponseEntity.ok(orderHandler.getAllOrders(page, size, orderStatus, "asc".equalsIgnoreCase(direction)));
    }

    @Operation(
            summary = "Take an order",
            description = "Allows the authenticated chef to take an available order for preparation."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order successfully taken", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Order cannot be taken because it is already assigned or is not available", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not authorized to take orders", content = @Content)
    })
    @PostMapping("/{orderId}/take")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Void> takeOrder(@PathVariable Long orderId) {
        orderHandler.takeOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Mark an order as ready",
            description = "Allows the authenticated chef to mark an order as ready for delivery."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order successfully marked as ready", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Order cannot be marked as ready because it is not in preparation", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not authorized to mark orders as ready", content = @Content)
    })
    @PatchMapping("/{orderId}/ready")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Void> markOrderAsReady(@PathVariable Long orderId) {
        orderHandler.markOrderAsReady(orderId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Mark an order as delivered",
            description = "Allows the authenticated employee to mark an order as delivered."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order successfully marked as delivered", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Order cannot be marked as ready because it is not ready", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not authorized to mark orders as ready", content = @Content)
    })
    @PatchMapping("/{orderId}/delivered")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<Void> markOrderAsDelivered(@PathVariable Long orderId, @RequestBody OrderPinRequestDto orderPinRequestDto) {
        orderHandler.markOrderAsDelivered(orderId, orderPinRequestDto.pin());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Cancel an order",
            description = "Allows the authenticated customer to cancel an order that is still pending."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order successfully cancelled", content = @Content),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Order cannot be cancelled because it is not pending", content = @Content),
            @ApiResponse(responseCode = "403", description = "User is not authorized to cancel orders", content = @Content)
    })
    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderHandler.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}
