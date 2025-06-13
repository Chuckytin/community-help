import 'dart:convert';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences.dart';
import 'package:jwt_decoder/jwt_decoder.dart';
import 'package:frontend/models/auth_models.dart';
import 'package:frontend/models/user_models.dart';
import '../models/user.dart';
import '../models/request.dart';

class ApiService {
  final String baseUrl;
  String? _token;

  ApiService() : baseUrl = dotenv.env['API_URL'] ?? 'http://localhost:8080';

  void setToken(String token) {
    _token = token;
  }

  Map<String, String> get _headers {
    return {
      'Content-Type': 'application/json',
      if (_token != null) 'Authorization': 'Bearer $_token',
    };
  }

  Future<String?> getToken() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString('token');
  }

  Future<void> saveToken(String token) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('token', token);
  }

  Future<void> deleteToken() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('token');
  }

  bool isTokenExpired(String token) {
    return JwtDecoder.isExpired(token);
  }

  Future<JwtResponse> login(LoginRequest request) async {
    final response = await http.post(
      Uri.parse('$baseUrl/auth/login'),
      headers: _headers,
      body: json.encode(request.toJson()),
    );

    if (response.statusCode == 200) {
      final jwtResponse = JwtResponse.fromJson(json.decode(response.body));
      await saveToken(jwtResponse.token);
      return jwtResponse;
    } else {
      throw Exception('Error al iniciar sesión: ${response.body}');
    }
  }

  Future<JwtResponse> register(RegisterRequest request) async {
    final response = await http.post(
      Uri.parse('$baseUrl/auth/register'),
      headers: _headers,
      body: json.encode(request.toJson()),
    );

    if (response.statusCode == 200) {
      final jwtResponse = JwtResponse.fromJson(json.decode(response.body));
      await saveToken(jwtResponse.token);
      return jwtResponse;
    } else {
      throw Exception('Error al registrar: ${response.body}');
    }
  }

  Future<JwtResponse> refreshToken() async {
    final token = await getToken();
    if (token == null) {
      throw Exception('No hay token para refrescar');
    }

    final response = await http.post(
      Uri.parse('$baseUrl/auth/refresh-token'),
      headers: _headers,
    );

    if (response.statusCode == 200) {
      final jwtResponse = JwtResponse.fromJson(json.decode(response.body));
      await saveToken(jwtResponse.token);
      return jwtResponse;
    } else {
      throw Exception('Error al refrescar token: ${response.body}');
    }
  }

  Future<User> getUserProfile() async {
    final response = await http.get(
      Uri.parse('$baseUrl/user/me'),
      headers: _headers,
    );

    if (response.statusCode == 200) {
      return User.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Error al obtener el perfil: ${response.body}');
    }
  }

  Future<User> updateUserProfile(User user) async {
    final response = await http.put(
      Uri.parse('$baseUrl/user/me'),
      headers: _headers,
      body: jsonEncode(user.toJson()),
    );

    if (response.statusCode == 200) {
      return User.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Error al actualizar el perfil: ${response.body}');
    }
  }

  Future<List<UserDTO>> getAllUsers({int page = 0, int size = 20}) async {
    final response = await http.get(
      Uri.parse('$baseUrl/users?page=$page&size=$size'),
      headers: _headers,
    );

    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      return (data['content'] as List)
          .map((user) => UserDTO.fromJson(user))
          .toList();
    } else {
      throw Exception('Error al obtener usuarios: ${response.body}');
    }
  }

  Future<void> updateUserRoles(Long userId, Set<UserRole> roles) async {
    final response = await http.put(
      Uri.parse('$baseUrl/users/$userId/roles'),
      headers: _headers,
      body: json.encode(roles.map((r) => r.toString().split('.').last).toList()),
    );

    if (response.statusCode != 204) {
      throw Exception('Error al actualizar roles: ${response.body}');
    }
  }

  Future<List<Request>> getRequests() async {
    final response = await http.get(
      Uri.parse('$baseUrl/requests'),
      headers: _headers,
    );

    if (response.statusCode == 200) {
      final List<dynamic> data = jsonDecode(response.body);
      return data.map((json) => Request.fromJson(json)).toList();
    } else {
      throw Exception('Error al obtener las solicitudes: ${response.body}');
    }
  }

  Future<Request> createRequest(Request request) async {
    final response = await http.post(
      Uri.parse('$baseUrl/requests'),
      headers: _headers,
      body: jsonEncode(request.toJson()),
    );

    if (response.statusCode == 201) {
      return Request.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Error al crear la solicitud: ${response.body}');
    }
  }

  Future<Request> acceptRequest(int requestId) async {
    final response = await http.post(
      Uri.parse('$baseUrl/requests/$requestId/accept'),
      headers: _headers,
    );

    if (response.statusCode == 200) {
      return Request.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Error al aceptar la solicitud: ${response.body}');
    }
  }

  Future<Request> completeRequest(int requestId) async {
    final response = await http.post(
      Uri.parse('$baseUrl/requests/$requestId/complete'),
      headers: _headers,
    );

    if (response.statusCode == 200) {
      return Request.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Error al completar la solicitud: ${response.body}');
    }
  }

  Future<Request> cancelRequest(int requestId) async {
    final response = await http.post(
      Uri.parse('$baseUrl/requests/$requestId/cancel'),
      headers: _headers,
    );

    if (response.statusCode == 200) {
      return Request.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Error al cancelar la solicitud: ${response.body}');
    }
  }

  Future<Map<String, dynamic>> getTravelTime(
    double fromLat,
    double fromLon,
    double toLat,
    double toLon,
    String mode,
  ) async {
    final response = await http.get(
      Uri.parse(
        '$baseUrl/integration/travel-time?fromLat=$fromLat&fromLon=$fromLon&toLat=$toLat&toLon=$toLon&mode=$mode',
      ),
      headers: _headers,
    );

    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    } else {
      throw Exception('Error al obtener el tiempo de viaje: ${response.body}');
    }
  }
} 