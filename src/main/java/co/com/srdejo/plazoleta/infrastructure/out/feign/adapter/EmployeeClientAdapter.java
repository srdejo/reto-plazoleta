package co.com.srdejo.plazoleta.infrastructure.out.feign.adapter;

import co.com.srdejo.plazoleta.domain.exception.EmployeeNotFoundException;
import co.com.srdejo.plazoleta.domain.exception.ErrorCodesEnum;
import co.com.srdejo.plazoleta.domain.spi.IEmployeeClientPort;
import co.com.srdejo.plazoleta.infrastructure.exception.ServiceUnavailableException;
import co.com.srdejo.plazoleta.infrastructure.out.feign.client.EmployeeClient;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EmployeeClientAdapter implements IEmployeeClientPort {

    private final EmployeeClient employeeClient;

    @Override
    public Long getAuthenticatedEmployeeRestaurantId() {
        try {
            return employeeClient.getAuthenticatedEmployee().restaurantId();
        } catch (feign.FeignException.NotFound ex) {
            throw new EmployeeNotFoundException(ErrorCodesEnum.EMPLOYEE_NOT_FOUND);
        } catch (feign.RetryableException ex) {
            throw new ServiceUnavailableException("usuarios-service", ex);
        }
    }
}
