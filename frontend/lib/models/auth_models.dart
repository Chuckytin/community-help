import 'user_models.dart';

class LoginRequest {
  final String email;
  final String password;

  LoginRequest({required this.email, required this.password});

  Map<String, dynamic> toJson() => {
    'email': email,
    'password': password,
  };
}

class RegisterRequest {
  final String email;
  final String password;
  final String name;
  final String phoneNumber;

  RegisterRequest({
    required this.email,
    required this.password,
    required this.name,
    required this.phoneNumber,
  });

  Map<String, dynamic> toJson() => {
    'email': email,
    'password': password,
    'name': name,
    'phoneNumber': phoneNumber,
  };
}

class JwtResponse {
  final String accessToken;
  final String? refreshToken;
  final User? user;
  final DateTime? expiresAt;

  JwtResponse({
    required this.accessToken,
    this.refreshToken,
    this.user,
    this.expiresAt,
  });

  factory JwtResponse.fromJson(Map<String, dynamic> json) => JwtResponse(
    accessToken: json['accessToken'],
    refreshToken: json['refreshToken'],
    user: json['user'] != null ? User.fromJson(json['user']) : null,
    expiresAt: json['expiresAt'] != null ? DateTime.parse(json['expiresAt']) : null,
  );

  Map<String, dynamic> toJson() => {
    'accessToken': accessToken,
    'refreshToken': refreshToken,
    'user': user?.toJson(),
    'expiresAt': expiresAt?.toIso8601String(),
  };
}
