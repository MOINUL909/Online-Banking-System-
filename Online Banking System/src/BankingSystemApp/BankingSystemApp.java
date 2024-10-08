package BankingSystemApp;


import java.io.*;
import java.util.*;

// Class representing a bank account
abstract class Account {
    protected int accountNumber;
    protected double balance;
    protected String accountType;

    public Account(int accountNumber, double balance, String accountType) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.accountType = accountType;
    }

    public abstract void deposit(double amount);

    public abstract void withdraw(double amount);

    public abstract void displayAccountDetails();

    public int getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public String getAccountType() {
        return accountType;
    }
}

// Class representing a savings account
class SavingsAccount extends Account {
    private static final double INTEREST_RATE = 0.05;  // 5% interest rate

    public SavingsAccount(int accountNumber, double balance) {
        super(accountNumber, balance, "Savings");
    }

    @Override
    public void deposit(double amount) {
        balance += amount + (amount * INTEREST_RATE);
        System.out.println("\u001B[32m" + "Deposited $" + amount + " with interest. New balance: $" + balance + "\u001B[0m");
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            System.out.println("\u001B[32m" + "Withdrawn $" + amount + ". New balance: $" + balance + "\u001B[0m");
        } else {
            System.out.println("\u001B[31m" + "Insufficient funds!" + "\u001B[0m");
        }
    }

    @Override
    public void displayAccountDetails() {
        System.out.println("\u001B[34m" + "Savings Account Number: " + accountNumber + "\nBalance: $" + balance + "\u001B[0m");
    }
}

// Class representing a checking account
class CheckingAccount extends Account {
    private static final double TRANSACTION_FEE = 1.00;  // $1 fee per transaction

    public CheckingAccount(int accountNumber, double balance) {
        super(accountNumber, balance, "Checking");
    }

    @Override
    public void deposit(double amount) {
        balance += amount - TRANSACTION_FEE;
        System.out.println("\u001B[32m" + "Deposited $" + amount + " after fee. New balance: $" + balance + "\u001B[0m");
    }

    @Override
    public void withdraw(double amount) {
        if (amount + TRANSACTION_FEE <= balance) {
            balance -= (amount + TRANSACTION_FEE);
            System.out.println("\u001B[32m" + "Withdrawn $" + amount + " after fee. New balance: $" + balance + "\u001B[0m");
        } else {
            System.out.println("\u001B[31m" + "Insufficient funds!" + "\u001B[0m");
        }
    }

    @Override
    public void displayAccountDetails() {
        System.out.println("\u001B[34m" + "Checking Account Number: " + accountNumber + "\nBalance: $" + balance + "\u001B[0m");
    }
}

// Class representing a customer
class Customer {
    private String name;
    private int customerID;
    private Account account;

    public Customer(String name, int customerID, Account account) {
        this.name = name;
        this.customerID = customerID;
        this.account = account;
    }

    public String getName() {
        return name;
    }

    public int getCustomerID() {
        return customerID;
    }

    public Account getAccount() {
        return account;
    }

    public void displayCustomerDetails() {
        System.out.println("\u001B[34m" + "Customer ID: " + customerID + "\nName: " + name + "\u001B[0m");
        account.displayAccountDetails();
    }
}

// Class representing the bank system
class Bank {
    private List<Customer> customers = new ArrayList<>();
    private int nextAccountNumber = 1000;

    // Method to create a new customer and account
    public void addCustomer(String name, String accountType, double initialDeposit) {
        Account account;
        if (accountType.equalsIgnoreCase("Savings")) {
            account = new SavingsAccount(nextAccountNumber++, initialDeposit);
        } else {
            account = new CheckingAccount(nextAccountNumber++, initialDeposit);
        }
        Customer customer = new Customer(name, customers.size() + 1, account);
        customers.add(customer);
        System.out.println("\u001B[32m" + "Account created successfully for " + name + " with " + accountType + " account.\u001B[0m");
        saveCustomerToFile(customer);
    }

    // Method to find a customer by ID
    public Customer findCustomer(int customerID) {
        for (Customer customer : customers) {
            if (customer.getCustomerID() == customerID) {
                return customer;
            }
        }
        return null;
    }

    // Method to deposit money into a customer's account
    public void deposit(int customerID, double amount) {
        Customer customer = findCustomer(customerID);
        if (customer != null) {
            customer.getAccount().deposit(amount);
            saveTransaction(customerID, "Deposit", amount);
        } else {
            System.out.println("\u001B[31m" + "Customer not found!" + "\u001B[0m");
        }
    }

    // Method to withdraw money from a customer's account
    public void withdraw(int customerID, double amount) {
        Customer customer = findCustomer(customerID);
        if (customer != null) {
            customer.getAccount().withdraw(amount);
            saveTransaction(customerID, "Withdraw", amount);
        } else {
            System.out.println("\u001B[31m" + "Customer not found!" + "\u001B[0m");
        }
    }

    // Method to transfer funds between customers
    public void transfer(int fromCustomerID, int toCustomerID, double amount) {
        Customer fromCustomer = findCustomer(fromCustomerID);
        Customer toCustomer = findCustomer(toCustomerID);
        if (fromCustomer != null && toCustomer != null) {
            if (fromCustomer.getAccount().getBalance() >= amount) {
                fromCustomer.getAccount().withdraw(amount);
                toCustomer.getAccount().deposit(amount);
                saveTransaction(fromCustomerID, "Transfer to " + toCustomerID, amount);
                saveTransaction(toCustomerID, "Transfer from " + fromCustomerID, amount);
                System.out.println("\u001B[32m" + "Transfer successful.\u001B[0m");
            } else {
                System.out.println("\u001B[31m" + "Insufficient funds for transfer!" + "\u001B[0m");
            }
        } else {
            System.out.println("\u001B[31m" + "One or both customers not found!" + "\u001B[0m");
        }
    }

    // Method to save customer information to a file
    private void saveCustomerToFile(Customer customer) {
        try (FileWriter writer = new FileWriter("customers.txt", true)) {
            writer.write("Customer ID: " + customer.getCustomerID() + ", Name: " + customer.getName() +
                    ", Account Number: " + customer.getAccount().getAccountNumber() +
                    ", Account Type: " + customer.getAccount().getAccountType() +
                    ", Balance: " + customer.getAccount().getBalance() + "\n");
        } catch (IOException e) {
            System.out.println("\u001B[31m" + "Error saving customer information!" + "\u001B[0m");
        }
    }

    // Method to save transactions to a file
    private void saveTransaction(int customerID, String transactionType, double amount) {
        try (FileWriter writer = new FileWriter("transactions.txt", true)) {
            writer.write("Customer ID: " + customerID + ", Transaction: " + transactionType + ", Amount: $" + amount + "\n");
        } catch (IOException e) {
            System.out.println("\u001B[31m" + "Error saving transaction!" + "\u001B[0m");
        }
    }
}

// Main class to run the application
public class BankingSystemApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Bank bank = new Bank();

        // User authentication
        System.out.print("Enter UserID: ");
        String userID = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        if (!userID.equals("Moinul") || !password.equals("Moinul909")) {
            System.out.println("\u001B[31m" + "Invalid credentials! Access denied." + "\u001B[0m");
            return;
        }

        System.out.println("\u001B[32m" + "Login successful! Welcome to the Online Banking System." + "\u001B[0m");

        while (true) {
            System.out.println("\n\u001B[34m1. Create Account\n2. Deposit\n3. Withdraw\n4. Transfer\n5. Display Customer Details\n6. Exit\u001B[0m");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter Customer Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Account Type (Savings/Checking): ");
                    String accountType = scanner.nextLine();
                    System.out.print("Enter Initial Deposit: ");
                    double initialDeposit = scanner.nextDouble();
                    bank.addCustomer(name, accountType, initialDeposit);
                    break;
                case 2:
                    System.out.print("Enter Customer ID: ");
                    int customerID = scanner.nextInt();
                    System.out.print("Enter Deposit Amount: ");
                    double depositAmount = scanner.nextDouble();
                    bank.deposit(customerID, depositAmount);
                    break;
                case 3:
                    System.out.print("Enter Customer ID: ");
                    int withdrawCustomerID = scanner.nextInt();
                    System.out.print("Enter Withdraw Amount: ");
                    double withdrawAmount = scanner.nextDouble();
                    bank.withdraw(withdrawCustomerID, withdrawAmount);
                    break;
                case 4:
                    System.out.print("Enter Sender Customer ID: ");
                    int fromCustomerID = scanner.nextInt();
                    System.out.print("Enter Receiver Customer ID: ");
                    int toCustomerID = scanner.nextInt();
                    System.out.print("Enter Transfer Amount: ");
                    double transferAmount = scanner.nextDouble();
                    bank.transfer(fromCustomerID, toCustomerID, transferAmount);
                    break;
                case 5:
                    System.out.print("Enter Customer ID: ");
                    int displayCustomerID = scanner.nextInt();
                    Customer customer = bank.findCustomer(displayCustomerID);
                    if (customer != null) {
                        customer.displayCustomerDetails();
                    } else {
                        System.out.println("\u001B[31m" + "Customer not found!" + "\u001B[0m");
                    }
                    break;
                case 6:
                    System.out.println("\u001B[32m" + "Exiting the system. Thank you!" + "\u001B[0m");
                    return;
                default:
                    System.out.println("\u001B[31m" + "Invalid choice! Please try again." + "\u001B[0m");
            }
        }
    }
}
