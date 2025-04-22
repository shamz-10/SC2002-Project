package enumeration;

public enum UserType {
APPLICANT("Applicant"),

HDB_OFFICER("HDB Officer"),

HDB_MANAGER("HDB Manager");

private final String displayName;

UserType(String displayName) {
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


