import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import '../models/auth_models.dart';
import '../models/user_profile_dto.dart';

class AuthService {
  final String baseUrl = 'http://localhost:8080/api';
  final http.Client _client = http.Client();

  Future<UserProfileDTO> getUserProfile() async {
    final prefs = await SharedPreferences.getInstance();
    final token = prefs.getString('token');
    
    if (token == null) {
      throw Exception('No hay token de autenticación');
    }

    final response = await _client.get(
      Uri.parse('$baseUrl/auth/profile'),
      headers: {
        'Authorization': 'Bearer $token',
        'Content-Type': 'application/json',
      },
    );

    if (response.statusCode == 200) {
      return UserProfileDTO.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Error al obtener el perfil del usuario');
    }
  }

  Future<JwtResponse> login(LoginRequest request) async {
    final response = await _client.post(
      Uri.parse('$baseUrl/auth/login'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(request.toJson()),
    );

    if (response.statusCode == 200) {
      final jwtResponse = JwtResponse.fromJson(jsonDecode(response.body));
      await _saveTokens(jwtResponse);
      return jwtResponse;
    } else {
      throw Exception('Error en el inicio de sesión');
    }
  }

  Future<JwtResponse> register(RegisterRequest request) async {
    final response = await _client.post(
      Uri.parse('$baseUrl/auth/register'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(request.toJson()),
    );

    if (response.statusCode == 201) {
      final jwtResponse = JwtResponse.fromJson(jsonDecode(response.body));
      await _saveTokens(jwtResponse);
      return jwtResponse;
    } else {
      throw Exception('Error en el registro');
    }
  }

  Future<void> logout() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('token');
    await prefs.remove('refreshToken');
  }

  Future<JwtResponse> refreshToken() async {
    final token = await _getToken();
    final response = await http.post(
      Uri.parse('$baseUrl/auth/refresh-token'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
    );

    if (response.statusCode == 200) {
      final jwtResponse = JwtResponse.fromJson(jsonDecode(response.body));
      await _saveTokens(jwtResponse);
      return jwtResponse;
    } else {
      throw Exception('Error al actualizar el token: ${response.body}');
    }
  }

  // Métodos auxiliares
  Future<void> _saveTokens(JwtResponse jwtResponse) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('token', jwtResponse.accessToken);
    if (jwtResponse.refreshToken != null) {
      await prefs.setString('refreshToken', jwtResponse.refreshToken!);
    }
  }

  Future<String> _getToken() async {
    final prefs = await SharedPreferences.getInstance();
    final token = prefs.getString('token');
    if (token == null) {
      throw Exception('No hay token disponible');
    }
    return token;
  }

  Future<bool> isTokenValid() async {
    try {
      final token = await _getToken();
      final response = await http.get(
        Uri.parse('$baseUrl/auth/health'),
        headers: {
          'Authorization': 'Bearer $token',
        },
      );
      return response.statusCode == 200;
    } catch (e) {
      return false;
    }
  }
} 