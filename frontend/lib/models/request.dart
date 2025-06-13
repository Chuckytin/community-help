class Request {
  final String title;
  final String description;
  final double latitude;
  final double longitude;

  Request({
    required this.title,
    required this.description,
    required this.latitude,
    required this.longitude,
  });

  Map<String, dynamic> toJson() => {
        'title': title,
        'description': description,
        'latitude': latitude,
        'longitude': longitude,
      };
}

class HelpRequest extends Request {
  final String? id;
  final String status;
  final String? assignedTo;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  HelpRequest({
    this.id,
    required super.title,
    required super.description,
    required super.latitude,
    required super.longitude,
    this.status = 'OPEN',
    this.assignedTo,
    this.createdAt,
    this.updatedAt,
  });

  factory HelpRequest.fromJson(Map<String, dynamic> json) {
    return HelpRequest(
      id: json['id'],
      title: json['title'],
      description: json['description'],
      latitude: json['latitude'],
      longitude: json['longitude'],
      status: json['status'],
      assignedTo: json['assignedTo'],
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
    );
  }

  @override
  Map<String, dynamic> toJson() {
    final map = {
      ...super.toJson(),
      'status': status,
      'assignedTo': assignedTo,
    };

    if (id != null) map['id'] = id;
    if (createdAt != null) map['createdAt'] = createdAt!.toIso8601String();
    if (updatedAt != null) map['updatedAt'] = updatedAt!.toIso8601String();

    return map;
  }
}
