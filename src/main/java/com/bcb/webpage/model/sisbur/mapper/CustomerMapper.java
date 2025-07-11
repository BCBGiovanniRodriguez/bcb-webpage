package com.bcb.webpage.model.sisbur.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;

import com.bcb.webpage.model.backend.entity.customers.CustomerCustomer;

public class CustomerMapper implements RowMapper<CustomerCustomer>{

    @Override
    @Nullable
    public CustomerCustomer mapRow(ResultSet rs, int rowNum) throws SQLException {
        CustomerCustomer customer = new CustomerCustomer();

        customer.setCustomerKey(rs.getString("CVECLIENTE"));
        customer.setName(rs.getString("NOMCLIENTE"));
        customer.setLastName(rs.getString("APEPATCLIENTE"));
        customer.setSecondLastName(rs.getString("APEMATCLIENTE"));
        customer.setPhoneNumber(rs.getString("TELDOM"));
        customer.setEmail(rs.getString("EMAIL"));
        customer.setInitial(rs.getInt(rs.getInt("INICIAL")));
        customer.setLocked(rs.getInt("BLOQUEADO"));
        customer.setPassword(rs.getString("PASSWORD"));

        return customer;
    }

}
