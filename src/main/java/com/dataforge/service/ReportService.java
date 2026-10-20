package com.dataforge.service;

import com.dataforge.config.DatabaseConnection;
import com.dataforge.model.Report;
import com.dataforge.util.TablePrinter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Service class that catalogs and executes the 15 report queries
 * defined in the HR and Project Management problem statement.
 */
public class ReportService {

    private final Map<Integer, Report> reports = new LinkedHashMap<>();

    public ReportService() {
        registerReports();
    }

    private void registerReports() {
        // --- Easy Questions (Q1 - Q8) ---
        addReport(new Report(
                1,
                "Easy",
                "IT Department Employees",
                "List the name and job title of all employees working in the IT department.",
                "SELECT e.name, e.job_title " +
                "FROM employees e JOIN departments d ON e.dept_id = d.dept_id " +
                "WHERE d.dept_name = 'IT' " +
                "ORDER BY e.emp_id;"
        ));

        addReport(new Report(
                2,
                "Easy",
                "High Earners (> 60,000)",
                "Show employees earning more than 60000, highest salary first.",
                "SELECT name, salary " +
                "FROM employees " +
                "WHERE salary > 60000 " +
                "ORDER BY salary DESC;"
        ));

        addReport(new Report(
                3,
                "Easy",
                "Employees Hired After 2021-01-01",
                "List employees hired after 2021-01-01 in order of hire date.",
                "SELECT name, hire_date " +
                "FROM employees " +
                "WHERE hire_date > '2021-01-01' " +
                "ORDER BY hire_date;"
        ));

        addReport(new Report(
                4,
                "Easy",
                "Employee Salary Statistics",
                "Show the total number of employees along with the maximum, minimum and average salary.",
                "SELECT COUNT(*) AS total_employees, MAX(salary) AS max_salary, " +
                "       MIN(salary) AS min_salary, ROUND(AVG(salary), 2) AS avg_salary " +
                "FROM employees;"
        ));

        addReport(new Report(
                5,
                "Easy",
                "Departments in Chennai",
                "List the departments located in Chennai.",
                "SELECT dept_name, location " +
                "FROM departments " +
                "WHERE location = 'Chennai';"
        ));

        addReport(new Report(
                6,
                "Easy",
                "Names Starting with 'S'",
                "Find employees whose name starts with the letter S.",
                "SELECT name " +
                "FROM employees " +
                "WHERE name LIKE 'S%';"
        ));

        addReport(new Report(
                7,
                "Easy",
                "Employees Without Manager",
                "List the employees who have no manager.",
                "SELECT name, job_title " +
                "FROM employees " +
                "WHERE manager_id IS NULL;"
        ));

        addReport(new Report(
                8,
                "Easy",
                "High Budget Projects (> 700,000)",
                "Show the project name, department name and budget for projects with a budget above 700000, largest first.",
                "SELECT p.project_name, d.dept_name, p.budget " +
                "FROM projects p JOIN departments d ON p.dept_id = d.dept_id " +
                "WHERE p.budget > 700000 " +
                "ORDER BY p.budget DESC;"
        ));

        // --- Medium Questions (Q9 - Q15) ---
        addReport(new Report(
                9,
                "Medium",
                "Department Headcount and Average Salary",
                "For every department, show the employee count and average salary. Departments with no employees must still appear.",
                "SELECT d.dept_name, COUNT(e.emp_id) AS emp_count, ROUND(AVG(e.salary), 2) AS avg_salary " +
                "FROM departments d LEFT JOIN employees e ON d.dept_id = e.dept_id " +
                "GROUP BY d.dept_id, d.dept_name " +
                "ORDER BY d.dept_id;"
        ));

        addReport(new Report(
                10,
                "Medium",
                "Employee and Manager Directory",
                "Show each employee's name together with their manager's name (NULL if there is no manager).",
                "SELECT e.name AS employee, m.name AS manager " +
                "FROM employees e LEFT JOIN employees m ON e.manager_id = m.emp_id " +
                "ORDER BY e.emp_id;"
        ));

        addReport(new Report(
                11,
                "Medium",
                "Total Hours Logged Per Project",
                "Find the total hours logged on each project, highest first.",
                "SELECT p.project_name, SUM(a.hours) AS total_hours " +
                "FROM projects p JOIN project_assignments a ON p.project_id = a.project_id " +
                "GROUP BY p.project_id, p.project_name " +
                "ORDER BY total_hours DESC;"
        ));

        addReport(new Report(
                12,
                "Medium",
                "Unassigned Employees",
                "Find the employees who are not assigned to any project.",
                "SELECT e.name " +
                "FROM employees e LEFT JOIN project_assignments a ON e.emp_id = a.emp_id " +
                "WHERE a.assign_id IS NULL;"
        ));

        addReport(new Report(
                13,
                "Medium",
                "Departments with High Average Salary (> 60,000)",
                "Find the departments whose average salary is above 60000.",
                "SELECT d.dept_name, AVG(e.salary) AS avg_salary " +
                "FROM departments d JOIN employees e ON d.dept_id = e.dept_id " +
                "GROUP BY d.dept_id, d.dept_name " +
                "HAVING AVG(e.salary) > 60000 " +
                "ORDER BY avg_salary DESC;"
        ));

        addReport(new Report(
                14,
                "Medium",
                "Highest Paid Employee Per Department",
                "Find the highest-paid employee in each department.",
                "SELECT d.dept_name, e.name, e.salary " +
                "FROM employees e JOIN departments d ON e.dept_id = d.dept_id " +
                "WHERE e.salary = (SELECT MAX(salary) FROM employees WHERE dept_id = e.dept_id) " +
                "ORDER BY d.dept_name;"
        ));

        addReport(new Report(
                15,
                "Medium",
                "Cross-Department Project Assignments",
                "Find employees working on a project that belongs to a different department than their own. Show the employee, home department, project and project department.",
                "SELECT e.name AS employee, ed.dept_name AS home_dept, p.project_name, pd.dept_name AS project_dept " +
                "FROM project_assignments a " +
                "JOIN employees e ON a.emp_id = e.emp_id " +
                "JOIN departments ed ON e.dept_id = ed.dept_id " +
                "JOIN projects p ON a.project_id = p.project_id " +
                "JOIN departments pd ON p.dept_id = pd.dept_id " +
                "WHERE e.dept_id <> p.dept_id " +
                "ORDER BY e.name;"
        ));
    }

    private void addReport(Report report) {
        reports.put(report.getId(), report);
    }

    public Map<Integer, Report> getAllReports() {
        return Collections.unmodifiableMap(reports);
    }

    public Report getReport(int id) {
        return reports.get(id);
    }

    /**
     * Executes the report with the specified ID and displays the formatted result.
     *
     * @param reportId the report ID (1 - 15)
     * @return true if executed successfully, false otherwise
     */
    public boolean executeReport(int reportId) {
        Report report = getReport(reportId);
        if (report == null) {
            System.out.println("[ERROR] Invalid Report ID: " + reportId);
            return false;
        }

        System.out.println("================================================================================");
        System.out.println("REPORT Q" + report.getId() + " [" + report.getDifficulty() + "]: " + report.getTitle());
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Problem Statement:");
        System.out.println("  " + report.getQuestion());
        System.out.println("\nSQL Query:");
        System.out.println("  " + report.getSqlQuery());
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Query Execution Results:");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(report.getSqlQuery());
             ResultSet rs = stmt.executeQuery()) {

            TablePrinter.printResultSet(rs);
            return true;
        } catch (SQLException e) {
            System.err.println("[EXECUTION ERROR]: " + e.getMessage());
            return false;
        }
    }

    /**
     * Runs all 15 reports sequentially.
     */
    public void executeAllReports() {
        System.out.println("\n>>> Executing all 15 reports sequentially...\n");
        int successCount = 0;
        for (int id : reports.keySet()) {
            if (executeReport(id)) {
                successCount++;
            }
        }
        System.out.println(">>> Execution Summary: " + successCount + "/" + reports.size() + " reports completed successfully.\n");
    }
}
