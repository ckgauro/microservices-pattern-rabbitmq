package com.gauro.customer.mapper;

import com.gauro.customer.dto.CustomerDto;
import com.gauro.customer.entity.Customer;

public class CustomerMapper {
    public static CustomerDto maptTpCustomerDto(Customer customer, CustomerDto customerDto){
        customerDto.setCustomerId(customer.getCustomerId());
        customerDto.setName(customer.getName());
        customerDto.setEmail(customer.getEmail());
        customerDto.setMobileNumber(customer.getMobileNumber());
        customerDto.setActiveSw(customer.isActiveSw());
        return customerDto;
    }
    public static Customer mapToCustomer(CustomerDto customerDto, Customer customer){
        customer.setCustomerId(customerDto.getCustomerId());
        customer.setName(customerDto.getName());
        customer.setEmail(customerDto.getEmail());
        customer.setMobileNumber(customerDto.getMobileNumber());
        if(customerDto.isActiveSw()){
            customer.setActiveSw(customerDto.isActiveSw());
        }

        return customer;
    }
}