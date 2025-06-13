enum UserRole {
  ROLE_USER,
  ROLE_ADMIN,
}

class UserDTO {
  final Long id;
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
                orElse: () => UserRole.ROLE_USER,
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
                orElse: () => UserRole.ROLE_USER,
              ))
          .toSet(),
      phone: json['phone'],
      address: json['address'],
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
    );
  }
} 