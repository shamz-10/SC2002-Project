package enumeration;

public enum MaritalStatus {
SINGLE("Single"),

MARRIED("Married");

private final String displayName;

MaritalStatus(String displayName) {
this.displayName = displayName;
}

public String getDisplayName() {
return displayName;
}

@Override
public String toString() {
return displayName;
}
}