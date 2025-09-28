package com.soumen.upi.Service;

import com.soumen.upi.Model.User.UPIUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.soumen.upi.Repository.UserRepo;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepo repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UPIUser user = repo.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User Not Found");
        }
        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_USER")
                .build();
    }

    public UserDetails loadUserByEmail(String useremail)throws UsernameNotFoundException{

        UPIUser user=repo.findByEmail(useremail);
        if (user==null) {
           throw new UsernameNotFoundException(useremail+" not found");   
        }

        return User.builder()
        .username(useremail)
        .password(user.getPassword())
        .authorities("ROLE_USER")
        .build();

    }
    
}