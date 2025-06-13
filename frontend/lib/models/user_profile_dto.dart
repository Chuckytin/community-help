class UserProfileDTO {
  final String id;
  final String email;
  final String name;
  final String phoneNumber;
  final String? profilePicture;
  final double? latitude;
  final double? longitude;

  UserProfileDTO({
    required this.id,
    required this.email,
    required this.name,
    required this.phoneNumber,
    this.profilePicture,
    this.latitude,
    this.longitude,
  });

  factory UserProfileDTO.fromJson(Map<String, dynamic> json) {
    return UserProfileDTO(
      id: json['id'] as String,
      email: json['email'] as String,
      name: json['name'] as String,
      phoneNumber: json['phoneNumber'] as String,
      profilePicture: json['profilePicture'] as String?,
      latitude: json['latitude'] as double?,
      longitude: json['longitude'] as double?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'email': email,
      'name': name,
      'phoneNumber': phoneNumber,
      'profilePicture': profilePicture,
      'latitude': latitude,
      'longitude': longitude,
    };
  }
} 