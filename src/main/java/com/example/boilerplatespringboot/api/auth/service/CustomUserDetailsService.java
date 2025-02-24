package com.example.boilerplatespringboot.api.auth.service;

import com.example.boilerplatespringboot.api.auth.dto.TokenDetails;
import com.example.boilerplatespringboot.api.auth.entity.UserEntity;
import com.example.boilerplatespringboot.api.auth.repository.UserRepository;
import com.example.boilerplatespringboot.common.enums.ApiStatus;
import com.example.boilerplatespringboot.common.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService  implements UserDetailsService {
  @Autowired
  private UserRepository userRepository;
  @Transactional
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    UserEntity entity = userRepository.findByUsrnm(username)
        .orElseThrow(() -> new ApiException(ApiStatus.RESOURCE_NOT_FOUND));
    return TokenDetails.from(entity);

  }
}
