package main.config;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
@Log
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
   @Autowired
    private CustomUserDetailService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/**", "/resources/static/**").permitAll()
                .antMatchers("/test/**").permitAll()
                    .antMatchers("/about").hasAnyRole("USER", "ADMIN")
                .antMatchers("/main/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/main/handbook/**").hasRole("ADMIN")
                .antMatchers("/main/redactor/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
                .and()
                    .formLogin()
                    .loginPage("/login")
                    .defaultSuccessUrl("/main")
                    .failureUrl("/login?error=true")
                    .permitAll()
                .and()
                    .exceptionHandling().accessDeniedPage("/403")
                .and()
                    .logout()
                    .deleteCookies("JSESSIONID")
                    .logoutSuccessUrl("/")
                    .permitAll()
                .and()
                    .csrf().disable();
                }

        @Bean
        @Override
        protected UserDetailsService userDetailsService() {
        //TODO: replace with custom password encoder
            UserDetails user = User.withDefaultPasswordEncoder()
                    .username("u")
                    .password("1")
                    .roles("USER")
                    .build();
            return new InMemoryUserDetailsManager(user);
        }




        @Override
        protected void configure(AuthenticationManagerBuilder auth)
                throws Exception {
            log.info("Error conf");
            auth.authenticationProvider(authenticationProvider());
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
            log.info("DaoAuthenticationProvider start");
            DaoAuthenticationProvider authProvider
                    = new DaoAuthenticationProvider();
            authProvider.setUserDetailsService(userDetailsService);
            authProvider.setPasswordEncoder(encoder());
            log.info("DaoAuthenticationProvider: "+ authProvider.toString());
            return authProvider;
        }
    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(11);
    }
}
