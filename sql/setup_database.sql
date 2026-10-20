-- =====================================================================
-- DATAFORGE: HR and Project Management System
-- Database Setup Script
-- =====================================================================

CREATE DATABASE IF NOT EXISTS dataforge;
USE dataforge;

-- 1. Departments Table
CREATE TABLE IF NOT EXISTS departments (
    dept_id INT PRIMARY KEY,
    dept_name VARCHAR(50) NOT NULL,
    location VARCHAR(50) NOT NULL
);

-- 2. Employees Table (with self-referencing foreign key for manager_id)
CREATE TABLE IF NOT EXISTS employees (
    emp_id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    dept_id INT,
    manager_id INT,
    salary DECIMAL(10,2) NOT NULL,
    hire_date DATE NOT NULL,
    job_title VARCHAR(40) NOT NULL,
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id),
    FOREIGN KEY (manager_id) REFERENCES employees(emp_id)
);

-- 3. Projects Table
CREATE TABLE IF NOT EXISTS projects (
    project_id INT PRIMARY KEY,
    project_name VARCHAR(60) NOT NULL,
    dept_id INT,
    budget DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id)
);

-- 4. Project Assignments Table (bridge table)
CREATE TABLE IF NOT EXISTS project_assignments (
    assign_id INT PRIMARY KEY,
    emp_id INT NOT NULL,
    project_id INT NOT NULL,
    hours INT NOT NULL,
    FOREIGN KEY (emp_id) REFERENCES employees(emp_id),
    FOREIGN KEY (project_id) REFERENCES projects(project_id)
);

-- =====================================================================
-- Sample Data Insertion (Safe & Idempotent via INSERT IGNORE)
-- =====================================================================

-- Departments
INSERT IGNORE INTO departments (dept_id, dept_name, location) VALUES
(1, 'IT', 'Chennai'),
(2, 'HR', 'Bangalore'),
(3, 'Finance', 'Mumbai'),
(4, 'Marketing', 'Chennai'),
(5, 'Legal', 'Delhi');

-- Employees (Managers inserted first / safe order)
INSERT IGNORE INTO employees (emp_id, name, dept_id, manager_id, salary, hire_date, job_title) VALUES
(1, 'Ramesh', 1, NULL, 95000.00, '2018-04-01', 'Director'),
(2, 'Sunita', 1, 1, 60000.00, '2020-06-15', 'Developer'),
(3, 'Vikram', 1, 1, 55000.00, '2021-02-10', 'Developer'),
(4, 'Lata', 2, NULL, 70000.00, '2019-08-20', 'HR Manager'),
(5, 'Manoj', 2, 4, 40000.00, '2022-01-05', 'HR Executive'),
(6, 'Nisha', 3, NULL, 85000.00, '2017-11-30', 'Finance Head'),
(7, 'Omkar', 3, 6, 50000.00, '2023-03-18', 'Accountant'),
(8, 'Pooja', 4, NULL, 65000.00, '2020-09-09', 'Marketing Lead'),
(9, 'Rohit', 4, 8, 45000.00, '2024-05-21', 'Executive'),
(10, 'Sneha', 1, 2, 48000.00, '2024-07-01', 'Trainee');

-- Projects
INSERT IGNORE INTO projects (project_id, project_name, dept_id, budget) VALUES
(1, 'Website Revamp', 4, 500000.00),
(2, 'Payroll System', 3, 800000.00),
(3, 'Mobile App', 1, 1200000.00),
(4, 'Hiring Portal', 2, 300000.00),
(5, 'Data Warehouse', 1, 1500000.00);

-- Project Assignments
INSERT IGNORE INTO project_assignments (assign_id, emp_id, project_id, hours) VALUES
(1, 2, 3, 120),
(2, 3, 3, 100),
(3, 10, 3, 60),
(4, 2, 5, 80),
(5, 3, 5, 90),
(6, 7, 2, 150),
(7, 6, 2, 40),
(8, 5, 4, 110),
(9, 4, 4, 30),
(10, 9, 1, 70),
(11, 8, 1, 50),
(12, 3, 4, 25),
(13, 10, 1, 35);
