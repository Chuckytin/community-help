import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import '../models/auth_models.dart';
import '../models/user_models.dart';
import '../models/request.dart';

class ApiService {
  static const String baseUrl = 'http://localhost:8080/api';
  static const String tokenKey = 'auth_token';
  static const String refreshTokenKey = 'refresh_token';

  // Métodos de autenticación
  Future<JwtResponse> login(LoginRequest request) async {
    final response = await http.post(
      Uri.parse('$baseUrl/auth/login'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(request.toJson()),
    );

    if (response.statusCode == 200) {
      final jwtResponse = JwtResponse.fromJson(jsonDecode(response.body));
      await _saveTokens(jwtResponse);
      return jwtResponse;
    } else {
      throw Exception('Error al iniciar sesión: ${response.body}');
    }
  }

  Future<JwtResponse> register(RegisterRequest request) async {
    final response = await http.post(
      Uri.parse('$baseUrl/auth/register'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(request.toJson()),
    );

    if (response.statusCode == 200) {
      final jwtResponse = JwtResponse.fromJson(jsonDecode(response.body));
      await _saveTokens(jwtResponse);
      return jwtResponse;
    } else {
      throw Exception('Error al registrar: ${response.body}');
    }
  }

  Future<void> logout() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(tokenKey);
    await prefs.remove(refreshTokenKey);
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

  // Métodos de usuario
  Future<User> getUserProfile() async {
    final token = await _getToken();
    final response = await http.get(
      Uri.parse('$baseUrl/users/profile'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
    );

    if (response.statusCode == 200) {
      return User.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Error al obtener el perfil: ${response.body}');
    }
  }

  Future<User> updateUserProfile(User user) async {
    final token = await _getToken();
    final response = await http.put(
      Uri.parse('$baseUrl/users/profile'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
      body: jsonEncode(user.toJson()),
    );

    if (response.statusCode == 200) {
      return User.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Error al actualizar el perfil: ${response.body}');
    }
  }

  // Métodos de solicitudes
  Future<List<HelpRequest>> getRequests() async {
    final token = await _getToken();
    final response = await http.get(
      Uri.parse('$baseUrl/requests'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
    );

    if (response.statusCode == 200) {
      final List<dynamic> jsonList = jsonDecode(response.body);
      return jsonList.map((json) => HelpRequest.fromJson(json)).toList();
    } else {
      throw Exception('Error al obtener las solicitudes: ${response.body}');
    }
  }

  Future<HelpRequest> createRequest(HelpRequest request) async {
    final token = await _getToken();
    final response = await http.post(
      Uri.parse('$baseUrl/requests'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
      body: jsonEncode(request.toJson()),
    );

    if (response.statusCode == 201) {
      return HelpRequest.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Error al crear la solicitud: ${response.body}');
    }
  }

  Future<HelpRequest> updateRequest(HelpRequest request) async {
    final token = await _getToken();
    final response = await http.put(
      Uri.parse('$baseUrl/requests/${request.id}'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
      body: jsonEncode(request.toJson()),
    );

    if (response.statusCode == 200) {
      return HelpRequest.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Error al actualizar la solicitud: ${response.body}');
    }
  }

  // Métodos auxiliares
  Future<void> _saveTokens(JwtResponse jwtResponse) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString(tokenKey, jwtResponse.accessToken);
    if (jwtResponse.refreshToken != null) {
      await prefs.setString(refreshTokenKey, jwtResponse.refreshToken!);
    }
  }

  Future<String> _getToken() async {
    final prefs = await SharedPreferences.getInstance();
    final token = prefs.getString(tokenKey);
    if (token == null) {
      throw Exception('No hay token disponible');
    }
    return token;
  }
} 