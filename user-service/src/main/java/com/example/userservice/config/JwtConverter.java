package com.example.userservice.config;
import com.example.userservice.model.Role;
import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =
            new JwtGrantedAuthoritiesConverter();
    private final UserService userService;

    @Value("${jwt.auth.converter.principle-attribute}")
    private String principleAttribute;

    @Value("${jwt.auth.converter.resource-id}")
    private String resourceId;

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        String identity = jwt.getClaim(principleAttribute);

        String email = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");
        String username = jwt.getClaimAsString("preferred_username");

        Collection<GrantedAuthority> resourceRoles = extractResourceRoles(jwt);
        Stream<? extends GrantedAuthority> authorities = resourceRoles.stream();
        String syncRole = Role.USER.name();

        if (!resourceRoles.isEmpty()) {
            boolean isAdmin = resourceRoles.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_client_admin"));
            boolean isSeller = resourceRoles.stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_client_seller"));

            if (isAdmin) {
                syncRole = Role.ADMIN.name();
            } else if (isSeller) {
                syncRole = Role.SELLER.name();
            }
        } else {
            authorities = userService.setRoles(identity).stream();
        }

        userService.addUser(identity, username, email, firstName, lastName, syncRole);

        return new JwtAuthenticationToken(
                jwt,
                Stream.concat(jwtGrantedAuthoritiesConverter.convert(jwt).stream(), authorities)
                        .collect(Collectors.toSet()),
                identity
        );

    }

    private Collection<GrantedAuthority> extractResourceRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess == null || !resourceAccess.containsKey(resourceId)) {
            return Set.of();
        }

        Map<String, Object> resource = (Map<String, Object>) resourceAccess.get(resourceId);
        Collection<String> resourceRoles = (Collection<String>) resource.get("roles");

        if (resourceRoles == null) return Set.of();

        return resourceRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

}
