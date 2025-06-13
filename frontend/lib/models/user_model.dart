import 'package:json_annotation/json_annotation.dart';

part 'user_model.g.dart';

@JsonSerializable()
class UserProfileDTO {
  final String id;
  final String name;
  final String email;
  final String? phone;
  final String? address;
  final double? latitude;
  final double? longitude;

  UserProfileDTO({
    required this.id,
    required this.name,
    required this.email,
    this.phone,
    this.address,
    this.latitude,
    this.longitude,
  });

  factory UserProfileDTO.fromJson(Map<String, dynamic> json) => _$UserProfileDTOFromJson(json);
  Map<String, dynamic> toJson() => _$UserProfileDTOToJson(this);
}
