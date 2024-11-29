package br.com.saldoamigo.dto;

public record LoginResponseDto(String token, String role, Long id) {
}
