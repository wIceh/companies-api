package me.wiceh.companies.objects;

public class Company {

    private final int id;
    private final String name;
    private final String owner;
    private double balance;


    public Company(int id, String name, String owner, double balance) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.balance = balance;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
