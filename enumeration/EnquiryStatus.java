package enumeration;


public enum EnquiryStatus {
SUBMITTED("Submitted"),

RESPONDED("Responded"),

CLOSED("Closed");

private final String displayName;

EnquiryStatus(String displayName) {
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