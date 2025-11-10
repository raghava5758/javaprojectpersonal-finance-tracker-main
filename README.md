# Personal Finance Tracker

**Owner:** [@raghava5758](https://github.com/raghava5758)

#Report: [Java Project Report personal finance tracker.pdf](https://github.com/user-attachments/files/23453837/Java.Project.Report.personal.finance.tracker.pdf)
A comprehensive desktop application built in Java using Swing GUI for managing personal finances. Track income, expenses, categories, budgets, and generate reports with ease.

## Features

- **Transaction Management**: Add, edit, delete, and view income and expense transactions with detailed information including date, category, and description.
- **Category Management**: Organize transactions into customizable income and expense categories.
- **Budget Tracking**: Set monthly budgets for different categories and monitor spending against them.
- **Financial Reports**: Generate reports on income, expenses, and balance over time.
- **Statistics Dashboard**: Visualize financial data with charts and summaries.
- **Search and Filter**: Advanced filtering by type, category, date range, and search descriptions.
- **Data Export**: Export transaction data to CSV files for external analysis.
- **Data Persistence**: All data is automatically saved to local text files for persistence across sessions.

## Technologies Used

- **Java**: Core programming language
- **Swing**: GUI framework for the desktop interface
- **Java Time API**: For date handling
- **File I/O**: For data persistence using text files

## Installation and Setup

### Prerequisites
- Java Development Kit (JDK) 8 or higher installed on your system
- Basic knowledge of command-line operations

### Steps to Run

1. **Clone or Download the Project**:
   - Download the project files to your local machine.

2. **Navigate to the Source Directory**:
   - Open a terminal and navigate to the `personal finance tracker - java/src` directory.

3. **Compile the Code**:
   - Run the following command to compile all Java files:
     ```
     javac -d ../bin Main.java gui/*.java models/*.java util/*.java
     ```
   - This will compile the source files and place the class files in the `bin` directory.

4. **Run the Application**:
   - From the `personal finance tracker - java` directory, run:
     ```
     java -cp bin Main
     ```
   - The application window will open, ready for use.

### Data Files
- The application automatically creates a `data` directory to store:
  - `transactions.txt`: All transaction records
  - `categories.txt`: Category definitions
  - `budgets.txt`: Budget settings
- Default categories are pre-loaded if no data exists.

## Usage

The application features a tabbed interface with five main sections:

### Transactions Tab
- View all transactions in a table with color coding (green for income, red for expenses).
- Add new transactions with type, amount, category, description, and date.
- Edit or delete existing transactions.
- Search and filter transactions by description, type, category, or date range.
- Export filtered transactions to CSV.

### Categories Tab
- Manage income and expense categories.
- Add new categories or remove existing ones.

### Budgets Tab
- Set monthly budgets for specific categories.
- Track spending against budgets.

### Reports Tab
- Generate financial reports for selected periods.
- View income vs. expenses summaries.

### Statistics Tab
- Visualize financial data with charts.
- See total income, expenses, balance, and category breakdowns.

## Data Storage

All data is stored locally in plain text files within the `data` directory:
- **Transactions**: Stored with ID, type, amount, category, description, and date.
- **Categories**: Name and type (Income/Expense).
- **Budgets**: Category, amount, month, and year.

Data is automatically loaded on startup and saved on application close or data changes.

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bugs and feature requests.

## License

This project is open-source and available under the [MIT License](LICENSE).


