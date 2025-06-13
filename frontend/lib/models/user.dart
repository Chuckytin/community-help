enum UserRole {
  roleUser,
  roleAdmin,
}

class UserDTO {
  final int id;
  final String name;
  final String email;
  final Set<UserRole> roles;

  UserDTO({
    required this.id,
    required this.name,
    required this.email,
    required this.roles,
  });

  factory UserDTO.fromJson(Map<String, dynamic> json) {
    return UserDTO(
      id: json['id'],
      name: json['name'],
      email: json['email'],
      roles: (json['roles'] as List)
          .map((role) => UserRole.values.firstWhere(
                (e) => e.toString() == 'UserRole.$role',
                orElse: () => UserRole.roleUser,
              ))
          .toSet(),
    );
  }
}

class UserProfileDTO extends UserDTO {
  final String? phone;
  final String? address;
  final DateTime createdAt;
  final DateTime updatedAt;

  UserProfileDTO({
    required super.id,
    required super.name,
    required super.email,
    required super.roles,
    this.phone,
    this.address,
    required this.createdAt,
    required this.updatedAt,
  });

  factory UserProfileDTO.fromJson(Map<String, dynamic> json) {
    return UserProfileDTO(
      id: json['id'],
      name: json['name'],
      email: json['email'],
      roles: (json['roles'] as List)
          .map((role) => UserRole.values.firstWhere(
                (e) => e.toString() == 'UserRole.$role',
                orElse: () => UserRole.roleUser,
              ))
          .toSet(),
      phone: json['phone'],
      address: json['address'],
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
    );
  }
}

class User {
  final String id;
  final String email;
  final String name;
  final String phoneNumber;
  final String? profilePicture;
  final double? latitude;
  final double? longitude;

  User({
    required this.id,
    required this.email,
    required this.name,
    required this.phoneNumber,
    this.profilePicture,
    this.latitude,
    this.longitude,
  });

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
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