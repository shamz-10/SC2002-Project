package enumeration;

public enum BTOApplicationStatus {
PENDING("Pending"),

SUCCESSFUL("Successful"),

UNSUCCESSFUL("Unsuccessful"),

BOOKED("Booked");

private final String displayName;

BTOApplicationStatus(String displayName) {
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