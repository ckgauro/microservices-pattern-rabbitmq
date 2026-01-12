package com.gauro.customer.service.impl;

import com.gauro.customer.constants.CustomerConstants;
import com.gauro.customer.dto.CustomerDto;
import com.gauro.customer.entity.Customer;
import com.gauro.customer.exception.CustomerAlreadyExistsException;
import com.gauro.customer.exception.ResourceNotFoundException;
import com.gauro.customer.mapper.CustomerMapper;
import com.gauro.customer.repository.CustomerRepository;
import com.gauro.customer.service.ICustomerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class CustomerServiceImpl implements ICustomerService {
    private CustomerRepository customerRepository;


    @Override
    public void createCustomer(CustomerDto customerDto) {
        customerDto.setActiveSw(CustomerConstants.ACTIVE_SW);
        Customer customer= CustomerMapper.mapToCustomer(customerDto, new Customer());
        log.info(customer.toString());
        Optional<Customer> optionalCustomer=customerRepository.findByMobileNumberAndActiveSw(
          customerDto.getMobileNumber(),true
        );
        if(optionalCustomer.isPresent()){
            throw new CustomerAlreadyExistsException("Customer already registed with given mobileNumber"+customerDto.getMobileNumber());
        }
        Customer savedCustomer=customerRepository.save(customer);
    }

    @Override
    public CustomerDto fetchCustomer(String mobileNumber) {
        Customer customer=customerRepository.findByMobileNumberAndActiveSw(mobileNumber,CustomerConstants.ACTIVE_SW)
                .orElseThrow(()->new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));
        CustomerDto customerDto=CustomerMapper.maptTpCustomerDto(customer, new CustomerDto());
        return customerDto;
    }

    @Override
    public boolean updateCustomer(CustomerDto customerDto) {
        Customer customer=customerRepository.findByMobileNumberAndActiveSw(customerDto.getMobileNumber(),CustomerConstants.ACTIVE_SW)
                .orElseThrow(()->new ResourceNotFoundException("Customer","mobileNumber", customerDto.getMobileNumber()));
       CustomerMapper.mapToCustomer(customerDto,customer);
       customerRepository.save(customer);
        return true;
    }

    @Override
    public boolean deleteCustomer(String customerId) {
        Customer customer=customerRepository.findById(customerId)
                .orElseThrow(()->new ResourceNotFoundException("customer", "customerID", customerId.toString()));
        customer.setActiveSw(CustomerConstants.IN_ACTIVE_SW);
        customerRepository.save(customer);
        return true;
    }
}