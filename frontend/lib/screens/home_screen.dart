import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:latlong2/latlong.dart';
import 'package:geolocator/geolocator.dart';
import '../providers/requests_provider.dart';
import '../models/user_profile_dto.dart';
import '../services/auth_service.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final MapController _mapController = MapController();
  final AuthService _authService = AuthService();
  LatLng? _currentPosition;
  UserProfileDTO? _userProfile;

  @override
  void initState() {
    super.initState();
    _loadRequests();
    _getCurrentPosition();
    _loadUserProfile();
  }

  Future<void> _loadRequests() async {
    await context.read<RequestsProvider>().loadRequests();
  }

  Future<void> _getCurrentPosition() async {
    try {
      final permission = await Geolocator.checkPermission();
      if (permission == LocationPermission.denied) {
        final requestPermission = await Geolocator.requestPermission();
        if (requestPermission == LocationPermission.denied) {
          return;
        }
      }

      final position = await Geolocator.getCurrentPosition();
      if (mounted) {
        setState(() {
          _currentPosition = LatLng(position.latitude, position.longitude);
        });
      }
    } catch (e) {
      // Manejar el error de ubicación
    }
  }

  Future<void> _loadUserProfile() async {
    try {
      final profile = await _authService.getUserProfile();
      if (mounted) {
        setState(() {
          _userProfile = profile;
        });
      }
    } catch (e) {
      // Manejar el error de carga del perfil
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Solicitudes de Ayuda'),
        actions: [
          if (_userProfile != null)
            CircleAvatar(
              backgroundImage: _userProfile!.profilePicture != null
                  ? NetworkImage(_userProfile!.profilePicture!)
                  : null,
              child: _userProfile!.profilePicture == null
                  ? Text(_userProfile!.name[0].toUpperCase())
                  : null,
            ),
          IconButton(
            icon: const Icon(Icons.person),
            onPressed: () {
              Navigator.pushNamed(context, '/profile');
            },
          ),
        ],
      ),
      body: Consumer<RequestsProvider>(
        builder: (context, provider, child) {
          if (provider.isLoading) {
            return const Center(child: CircularProgressIndicator());
          }

          if (provider.error != null) {
            return Center(child: Text('Error: ${provider.error}'));
          }

          if (_currentPosition == null) {
            return const Center(child: Text('Obteniendo ubicación...'));
          }

          return FlutterMap(
            mapController: _mapController,
            options: MapOptions(
              initialCenter: _currentPosition!,
              initialZoom: 13.0,
            ),
            children: [
              TileLayer(
                urlTemplate: 'https://tile.openstreetmap.org/{z}/{x}/{y}.png',
                userAgentPackageName: 'com.example.community_help',
              ),
              MarkerLayer(
                markers: provider.requests.map((req) {
                  return Marker(
                    point: LatLng(req.latitude, req.longitude),
                    width: 40,
                    height: 40,
                    child: Icon(
                      Icons.location_on,
                      color: req.status == 'OPEN' ? Colors.red : Colors.green,
                      size: 40,
                    ),
                  );
                }).toList(),
              ),
            ],
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {
          Navigator.pushNamed(context, '/create-request');
        },
        child: const Icon(Icons.add),
      ),
    );
  }
} 