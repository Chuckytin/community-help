import 'package:flutter/material.dart';
import '../models/user.dart';

class AppState extends ChangeNotifier {
  UserProfileDTO? currentUser;
  bool isLoading = false;
  String? error;

  Future<void> loadUserProfile() async {
    isLoading = true;
    error = null;
    notifyListeners();
    // Aquí iría la lógica para cargar el perfil del usuario desde la API
    // Por ahora, simulamos una carga exitosa con datos de ejemplo
    await Future.delayed(const Duration(seconds: 1));
    currentUser = UserProfileDTO(
      id: 1,
      name: 'Usuario Ejemplo',
      email: 'usuario@ejemplo.com',
      roles: {UserRole.roleUser},
      phone: '1234567890',
      address: 'Calle Ejemplo 123',
      createdAt: DateTime.now(),
      updatedAt: DateTime.now(),
    );
    isLoading = false;
    notifyListeners();
  }

  Future<void> updateUserProfile(UserProfileDTO user) async {
    isLoading = true;
    error = null;
    notifyListeners();
    // Aquí iría la lógica para actualizar el perfil del usuario en la API
    // Por ahora, simulamos una actualización exitosa
    await Future.delayed(const Duration(seconds: 1));
    currentUser = user;
    isLoading = false;
    notifyListeners();
  }
}
