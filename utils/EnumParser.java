package utils;

import enumeration.BTOApplicationStatus;
import enumeration.FlatType;
import enumeration.MaritalStatus;
import enumeration.HDBOfficerRegistrationStatus;

public class EnumParser {
public static FlatType parseFlatType(String displayName) {
for (FlatType type : FlatType.values()) {
if (type.getDisplayName().equals(displayName)) {
return type;
}
}
throw new IllegalArgumentException("Invalid flat type: " + displayName);
}

public static MaritalStatus parseMaritalStatus(String status) {
String upperStatus = status.toUpperCase();
try {
return MaritalStatus.valueOf(upperStatus);
} catch (IllegalArgumentException e) {
for (MaritalStatus ms : MaritalStatus.values()) {
if (ms.getDisplayName().equalsIgnoreCase(status)) {
return ms;
}
}
throw new IllegalArgumentException("Invalid marital status: " + status);
}
}

public static BTOApplicationStatus parseBTOApplicationStatus(String status) {
for (BTOApplicationStatus s : BTOApplicationStatus.values()) {
if (s.getDisplayName().equals(status)) {
return s;
}
}
throw new IllegalArgumentException("Invalid BTO application status: " + status);
}

public static HDBOfficerRegistrationStatus parseHDBOfficerRegistrationStatus(String status) {
for (HDBOfficerRegistrationStatus s : HDBOfficerRegistrationStatus.values()) {
if (s.getDisplayName().equals(status)) {
return s;
}
}
throw new IllegalArgumentException("Invalid HDB officer registration status: " + status);
}
}