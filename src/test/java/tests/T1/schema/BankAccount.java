package tests.T1.schema;


import grphaqladapter.annotations.GraphqlType;

@GraphqlType
public class BankAccount {

    private final String id;
    private final String username;
    private final Double balance;

    public BankAccount(String id, String username, Double balance) {
        this.id = id;
        this.username = username;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Double getBalance() {
        return balance;
    }
}
