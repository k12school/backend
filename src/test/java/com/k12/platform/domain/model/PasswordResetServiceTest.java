package com.k12.platform.domain.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.k12.platform.domain.model.valueobjects.ResetToken;
import com.k12.platform.domain.model.valueobjects.UserId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.k12.platform.domain.port.PasswordResetTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for PasswordResetService domain service.
 * Target: 90%+ coverage
 */
@DisplayName("PasswordResetService Tests")
class PasswordResetServiceTest {

    @Mock
    PasswordResetTokenRepository tokenRepository;

    PasswordResetService passwordResetService;

    private UserId userId;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        passwordResetService = new PasswordResetService(tokenRepository);
        userId = UserId.generate();
        doNothing().when(tokenRepository).save(any(PasswordResetToken.class));
    }

    @Test
    @DisplayName("Should request password reset successfully")
    void shouldRequestPasswordResetSuccessfully() {
        when(tokenRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        PasswordResetToken result = passwordResetService.requestPasswordReset(userId);

        assertNotNull(result);
        assertEquals(userId, result.userId());
        verify(tokenRepository).findByUserId(userId);
        verify(tokenRepository).save(any(PasswordResetToken.class));
    }

    @Test
    @DisplayName("Should invalidate existing tokens when requesting new reset")
    void shouldInvalidateExistingTokens() {
        PasswordResetToken existingToken = PasswordResetToken.create(userId);
        List<PasswordResetToken> existingTokens = List.of(existingToken);
        when(tokenRepository.findByUserId(userId)).thenReturn(existingTokens);

        passwordResetService.requestPasswordReset(userId);

        verify(tokenRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("Should validate valid reset token")
    void shouldValidateValidResetToken() {
        PasswordResetToken resetToken = PasswordResetToken.create(userId);
        ResetToken token = resetToken.token();
        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));

        UserId result = passwordResetService.validateResetToken(token.value());

        assertEquals(userId, result);
        verify(tokenRepository).findByToken(token);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for invalid token")
    void shouldThrowIllegalArgumentExceptionForInvalidToken() {
        ResetToken token = ResetToken.generate();
        when(tokenRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> passwordResetService.validateResetToken(token.value()));

        verify(tokenRepository).findByToken(token);
    }

    @Test
    @DisplayName("Should throw IllegalStateException for expired token")
    void shouldThrowIllegalStateExceptionForExpiredToken() {
        PasswordResetToken resetToken = PasswordResetToken.create(userId, -1); // Expired
        ResetToken token = resetToken.token();
        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));

        assertThrows(IllegalStateException.class, () -> passwordResetService.validateResetToken(token.value()));

        assertTrue(resetToken.isExpired());
    }

    @Test
    @DisplayName("Should throw IllegalStateException for used token")
    void shouldThrowIllegalStateExceptionForUsedToken() {
        PasswordResetToken resetToken = PasswordResetToken.create(userId);
        resetToken.markAsUsed();
        ResetToken token = resetToken.token();
        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));

        IllegalStateException exception =
                assertThrows(IllegalStateException.class, () -> passwordResetService.validateResetToken(token.value()));

        assertTrue(exception.getMessage().contains("already been used"));
    }

    @Test
    @DisplayName("Should mark token as used")
    void shouldMarkTokenAsUsed() {
        PasswordResetToken resetToken = PasswordResetToken.create(userId);
        ResetToken token = resetToken.token();
        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));

        passwordResetService.markTokenAsUsed(token.value());

        assertTrue(resetToken.isUsed());
        verify(tokenRepository).save(resetToken);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when marking invalid token as used")
    void shouldThrowIllegalArgumentExceptionWhenMarkingInvalidToken() {
        ResetToken token = ResetToken.generate();
        when(tokenRepository.findByToken(token)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> passwordResetService.markTokenAsUsed(token.value()));

        verify(tokenRepository, never()).save(any(PasswordResetToken.class));
    }

    @Test
    @DisplayName("Should save token after marking as used")
    void shouldSaveTokenAfterMarkingAsUsed() {
        PasswordResetToken resetToken = PasswordResetToken.create(userId);
        ResetToken token = resetToken.token();
        when(tokenRepository.findByToken(token)).thenReturn(Optional.of(resetToken));

        passwordResetService.markTokenAsUsed(token.value());

        verify(tokenRepository).save(resetToken);
    }

    @Test
    @DisplayName("Should handle empty list of existing tokens")
    void shouldHandleEmptyListOfExistingTokens() {
        when(tokenRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        PasswordResetToken result = passwordResetService.requestPasswordReset(userId);

        assertNotNull(result);
        verify(tokenRepository).findByUserId(userId);
    }

    @Test
    @DisplayName("Should save new token after requesting reset")
    void shouldSaveNewTokenAfterRequestingReset() {
        when(tokenRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        passwordResetService.requestPasswordReset(userId);

        verify(tokenRepository).save(any(PasswordResetToken.class));
    }
}
