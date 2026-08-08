package com.erail.model;

public class LoginData {

    private final String testCaseId;
    private final String username;
    private final String password;
    private final String expectedResult;

    public LoginData(String testCaseId, String username, String password, String expectedResult) {
        this.testCaseId = testCaseId;
        this.username = username;
        this.password = password;
        this.expectedResult = expectedResult;
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getExpectedResult() {
        return expectedResult;
    }

    public boolean isValidLoginExpected() {
        return "Valid".equalsIgnoreCase(expectedResult);
    }

    @Override
    public String toString() {
        return testCaseId + " [" + expectedResult + "]";
    }
}
