package com.example.employee_management_system.securityConfig;

import com.example.employee_management_system.model.Authorities;
import com.example.employee_management_system.model.Roles;
import com.example.employee_management_system.model.Users;
import com.example.employee_management_system.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Note: Not cached to avoid stale data during authentication
    @Override
    /// by me
    /// Spring Security intercepts the login request through authentication filters, creates an unauthenticated Authentication token, delegates authentication to AuthenticationManager and AuthenticationProvider, then calls UserDetailsService.loadUserByUsername() because Spring itself does not know how or where your users are stored, so you must implement it to provide user retrieval logic from your database.
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (!user.isEnabled()) {
            throw new UsernameNotFoundException("User account is disabled: " + username);
        }

        return User.builder()
                .username(user.getUserName())
                .password(user.getPassword())
                .authorities(getAuthorities(user.getRoles()))
                .accountLocked(!user.isEnabled())
                .credentialsExpired(false)
                .accountExpired(false)
                .disabled(!user.isEnabled())
                .build();
    }
    ///  by me
    /// why do need these both methods ?Why both are needed:
    ///
    /// Authentication needs fresh, uncached data for security
    /// Business logic needs cached entities for performance
    /// Different return types serve different purposes
    /// Separation of concerns: security vs application logic
    /// This is a common pattern - keeping authentication separate from business operations.
    /// so loadUserByUsername - authentication (Spring Security uses this during login)
    /// getUserByUsername - business logic (your controllers/services use this to get user data)
    @Cacheable(value = "user-entities-by-username", key = "#username", sync = true)
    public Users getUserByUsername(String username) {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Cacheable(value = "user-entities-by-email", key = "#email", sync = true)
    public Users getUserByEmail(String email) {
        return userRepository.findByEmployeeEmail(email).orElse(null);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Collection<Roles> roles) {
        Collection<SimpleGrantedAuthority> authorities = new HashSet<>();
        for (Roles role : roles) {
            // Include the role name as an authority
            SimpleGrantedAuthority roleAuthority = new SimpleGrantedAuthority("ROLE_" + role.getRole());
            authorities.add(roleAuthority);

            // Also include any additional authorities from the role_authorities table
            for (Authorities authority : role.getAuthorities()) {
                SimpleGrantedAuthority auth = new SimpleGrantedAuthority("ROLE_" + authority.getName());
                authorities.add(auth);
            }
        }
        return authorities;
    }
}
