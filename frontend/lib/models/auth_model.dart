import 'package:frontend/models/user_model.dart';

class LoginRequest {
  final String email;
  final String password;

  LoginRequest({
    required this.email,
    required this.password,
  });

  Map<String, dynamic> toJson() => {
        'email': email,
        'password': password,
      };
}

class RegisterRequest {
  final String name;
  final String email;
  final String password;

  RegisterRequest({
    required this.name,
    required this.email,
    required this.password,
  });

  Map<String, dynamic> toJson() => {
        'name': name,
        'email': email,
        'password': password,
      };
}

class JwtResponse {
  final String token;
  final String refreshToken;
  final UserProfileDTO userProfile;

  JwtResponse({
    required this.token,
    required this.refreshToken,
    required this.userProfile,
  });

  factory JwtResponse.fromJson(Map<String, dynamic> json) {
    return JwtResponse(
      token: json['token'],
      refreshToken: json['refreshToken'],
      userProfile: UserProfileDTO.fromJson(json['userProfile']),
    );
  }

  Map<String, dynamic> toJson() => {
        'token': token,
        'refreshToken': refreshToken,
        'userProfile': userProfile.toJson(),
      };
} 