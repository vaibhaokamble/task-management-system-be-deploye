package com.organization.taskManagement.security;

import com.organization.taskManagement.Model.RefreshToken;
import com.organization.taskManagement.Repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {



    private final RefreshTokenRepository refreshTokenRepository;

   public RefreshToken createRefreshToken(String employeeId, String employeeRole ){
       refreshTokenRepository.deleteByEmployeeId(employeeId);
         RefreshToken refreshToken = new RefreshToken();
         refreshToken.setEmployeeId(employeeId);
         refreshToken.setToken(UUID.randomUUID().toString());
         refreshToken.setUserType(employeeRole);
         refreshToken.setExpiryDate(Instant.now().plus(604800000, ChronoUnit.MILLIS));
       return refreshTokenRepository.save(refreshToken);
   }


   public RefreshToken verifyExpiration(RefreshToken token){
       if(token.getExpiryDate().isBefore(Instant.now())){
           refreshTokenRepository.delete(token);
           throw new RuntimeException("Refresh token was expired. Please make a new signIn request");
       }
       return token;
   }






}
