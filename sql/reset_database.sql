-- =====================================================================
-- DATAFORGE: HR and Project Management System
-- Database Reset Script
-- Drops all tables in correct foreign key dependency order,
-- then runs the fresh schema and sample data setup.
-- =====================================================================

USE dataforge;

-- Drop dependent tables first to respect foreign key constraints
DROP TABLE IF EXISTS project_assignments;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS employees;
DROP TABLE IF EXISTS departments;

-- Re-run the table creation and insertion
-- (or run setup_database.sql)
