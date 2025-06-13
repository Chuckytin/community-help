import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:shared_preferences.dart';
import '../models/user.dart';

class AuthService {
  final String baseUrl;
  String? _token;
  final _storage = SharedPreferences.getInstance();

  AuthService() : baseUrl = dotenv.env['API_URL'] ?? 'https://community-help-d87d.onrender.com';

  Future<String?> getToken() async {
    final prefs = await _storage;
    return prefs.getString('token');
  }

  Future<void> saveToken(String token) async {
    final prefs = await _storage;
    await prefs.setString('token', token);
    _token = token;
  }

  Future<void> deleteToken() async {
    final prefs = await _storage;
    await prefs.remove('token');
    _token = null;
  }

  Map<String, String> get _headers {
    return {
      'Content-Type': 'application/json',
      if (_token != null) 'Authorization': 'Bearer $_token',
    };
  }

  Future<String> loginWithGoogle(String idToken) async {
    final response = await http.post(
      Uri.parse('$baseUrl/auth/google'),
      headers: _headers,
      body: jsonEncode({'idToken': idToken}),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      await saveToken(data['token']);
      return data['token'];
    } else {
      throw Exception('Error en el login con Google: ${response.body}');
    }
  }

  Future<String> login(String email, String password) async {
    final response = await http.post(
      Uri.parse('$baseUrl/auth/login'),
      headers: _headers,
      body: jsonEncode({
        'email': email,
        'password': password,
      }),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      await saveToken(data['token']);
      return data['token'];
    } else {
      throw Exception('Error en el login: ${response.body}');
    }
  }

  Future<String> register(String name, String email, String password) async {
    final response = await http.post(
      Uri.parse('$baseUrl/auth/register'),
      headers: _headers,
      body: jsonEncode({
        'name': name,
        'email': email,
        'password': password,
      }),
    );

    if (response.statusCode == 201) {
      final data = jsonDecode(response.body);
      await saveToken(data['token']);
      return data['token'];
    } else {
      throw Exception('Error en el registro: ${response.body}');
    }
  }

  Future<void> logout() async {
    await deleteToken();
  }

  Future<bool> isAuthenticated() async {
    final token = await getToken();
    if (token == null) return false;

    try {
      final response = await http.get(
        Uri.parse('$baseUrl/auth/validate'),
        headers: _headers,
      );
      return response.statusCode == 200;
    } catch (e) {
      return false;
    }
  }

  Future<String> refreshToken() async {
    final token = await getToken();
    if (token == null) throw Exception('No hay token para refrescar');

    final response = await http.post(
      Uri.parse('$baseUrl/auth/refresh-token'),
      headers: _headers,
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      await saveToken(data['token']);
      return data['token'];
    } else {
      throw Exception('Error al refrescar el token: ${response.body}');
    }
  }
} 