import 'package:flutter/material.dart';
import '../models/request.dart';
import '../services/api_service.dart';

class RequestsProvider extends ChangeNotifier {
  final ApiService _apiService;
  List<HelpRequest> _requests = [];
  bool _isLoading = false;
  String? _error;

  RequestsProvider(this._apiService);

  List<HelpRequest> get requests => _requests;
  bool get isLoading => _isLoading;
  String? get error => _error;

  Future<void> loadRequests() async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      _requests = await _apiService.getRequests();
    } catch (e) {
      _error = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> createRequest(HelpRequest request) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final createdRequest = await _apiService.createRequest(request);
      _requests.add(createdRequest);
    } catch (e) {
      _error = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<void> updateRequest(HelpRequest request) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      final updatedRequest = await _apiService.updateRequest(request);
      final index = _requests.indexWhere((r) => r.id == request.id);
      if (index != -1) {
        _requests[index] = updatedRequest;
      }
    } catch (e) {
      _error = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }
} 