package main.config;

import com.lis.qr_back.dao.UserDAO;
import com.lis.qr_back.model.User;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log
public class CustomUserDetailService implements UserDetailsService{

    final UserDAO userDAO;

    @Autowired
    public CustomUserDetailService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDAO.getByEmail(username);

        if (user == null) {
            log.info("User with username "+username+" was not found");
            throw new UsernameNotFoundException(username);
        }

            List<String> roles = userDAO.getRoleByUserId(user.getId());

            List<GrantedAuthority> authorities = new ArrayList<>();
            if(roles != null){
                for(String role: roles){
                    authorities.add(new SimpleGrantedAuthority(role));
                }
            }

        log.info("User found. Roles are "+ roles.toString());
        return new org.springframework.security.core.userdetails
                .User(user.getEmail(),user.getPassword(), authorities);
    }
}
