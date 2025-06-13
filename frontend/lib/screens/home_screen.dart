import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:latlong2/latlong.dart';
import 'package:provider/provider.dart';
import 'package:frontend/models/request_model.dart';
import 'package:frontend/models/user_model.dart';
import 'package:frontend/services/api_service.dart';
import 'package:frontend/services/auth_service.dart';

class RequestsProvider extends ChangeNotifier {
  final ApiService apiService;
  List<HelpRequest> requests = [];
  bool isLoading = false;
  String? error;

  RequestsProvider(this.apiService);

  Future<void> fetchRequests() async {
    isLoading = true;
    error = null;
    notifyListeners();
    try {
      final result = await apiService.getRequests();
      requests = result;
    } catch (e) {
      error = e.toString();
    }
    isLoading = false;
    notifyListeners();
  }
}

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final MapController _mapController = MapController();
  Timer? _timer;

  @override
  void initState() {
    super.initState();
    final provider = Provider.of<RequestsProvider>(context, listen: false);
    provider.fetchRequests();
    _timer = Timer.periodic(const Duration(seconds: 30), (_) {
      provider.fetchRequests();
    });
  }

  @override
  void dispose() {
    _timer?.cancel();
    super.dispose();
  }

  Future<void> _onRefresh() async {
    await Provider.of<RequestsProvider>(context, listen: false).fetchRequests();
  }

  void _showRequestDetails(BuildContext context, HelpRequest request) {
    showModalBottomSheet(
      context: context,
      builder: (_) => _RequestDetailsSheet(request: request),
    );
  }

  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (context) => RequestsProvider(Provider.of<ApiService>(context, listen: false)),
      child: Consumer2<RequestsProvider, AuthService>(
        builder: (context, reqProvider, authService, _) {
          return Scaffold(
            appBar: AppBar(
              title: const Text('Solicitudes Cercanas'),
              backgroundColor: const Color(0xFF1EB980),
            ),
            drawer: _AppDrawer(authService: authService),
            body: reqProvider.isLoading
                ? const Center(child: CircularProgressIndicator())
                : RefreshIndicator(
                    onRefresh: _onRefresh,
                    child: FlutterMap(
                      mapController: _mapController,
                      options: MapOptions(
                        initialCenter: const LatLng(-34.6037, -58.3816), // Buenos Aires por defecto
                        initialZoom: 13,
                        onTap: (_, point) {
                          // Manejar tap en el mapa si es necesario
                        },
                      ),
                      children: [
                        TileLayer(
                          urlTemplate: 'https://tile.openstreetmap.org/{z}/{x}/{y}.png',
                          userAgentPackageName: 'com.example.community_help',
                        ),
                        MarkerLayer(
                          markers: reqProvider.requests.map((req) {
                            return Marker(
                              point: LatLng(req.latitude, req.longitude),
                              width: 40,
                              height: 40,
                              child: GestureDetector(
                                onTap: () => _showRequestDetails(context, req),
                                child: Icon(
                                  Icons.location_on,
                                  color: req.status == 'OPEN' ? Colors.green : Colors.red,
                                  size: 40,
                                ),
                              ),
                            );
                          }).toList(),
                        ),
                      ],
                    ),
                  ),
            floatingActionButton: FloatingActionButton(
              backgroundColor: const Color(0xFF1EB980),
              child: const Icon(Icons.add),
              onPressed: () {
                // Aquí deberías abrir la pantalla para crear una nueva solicitud
              },
            ),
          );
        },
      ),
    );
  }
}

class _RequestDetailsSheet extends StatelessWidget {
  final HelpRequest request;
  const _RequestDetailsSheet({required this.request});
  
  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            request.title,
            style: Theme.of(context).textTheme.titleLarge,
          ),
          const SizedBox(height: 8),
          Text(
            request.description,
            style: Theme.of(context).textTheme.bodyMedium,
          ),
          const SizedBox(height: 8),
          Row(
            children: [
              const Icon(Icons.location_on, size: 16),
              const SizedBox(width: 4),
              Text(
                '${request.latitude.toStringAsFixed(6)}, ${request.longitude.toStringAsFixed(6)}',
                style: Theme.of(context).textTheme.bodySmall,
              ),
            ],
          ),
        ],
      ),
    );
  }
}

class _AppDrawer extends StatelessWidget {
  final AuthService authService;
  const _AppDrawer({required this.authService});

  @override
  Widget build(BuildContext context) {
    return Drawer(
      child: FutureBuilder<UserProfileDTO?>(
        future: authService.getUserProfile(),
        builder: (context, snapshot) {
          final user = snapshot.data;
          return ListView(
            padding: EdgeInsets.zero,
            children: [
              UserAccountsDrawerHeader(
                decoration: const BoxDecoration(color: Color(0xFF1EB980)),
                accountName: Text(user?.name ?? ''),
                accountEmail: Text(user?.email ?? ''),
                currentAccountPicture: const CircleAvatar(
                  backgroundColor: Colors.white,
                  child: Icon(Icons.person, color: Color(0xFF1EB980)),
                ),
              ),
              ListTile(
                leading: const Icon(Icons.logout, color: Color(0xFFFF6859)),
                title: const Text('Cerrar sesión'),
                onTap: () async {
                  await authService.logout();
                  if (context.mounted) {
                    Navigator.pushReplacementNamed(context, '/login');
                  }
                },
              ),
            ],
          );
        },
      ),
    );
  }
} 