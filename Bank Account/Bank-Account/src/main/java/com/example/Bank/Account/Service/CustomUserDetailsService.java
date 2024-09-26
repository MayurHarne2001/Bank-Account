package com.example.Bank.Account.Service;



import com.example.Bank.Account.Entity.CustomerDetails;
import com.example.Bank.Account.Repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {

        CustomerDetails customer =customerRepository.findByName(name)
                .orElseThrow(()->new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                customer.getName(),
                customer.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("Role_User"))
        );
    }
}
