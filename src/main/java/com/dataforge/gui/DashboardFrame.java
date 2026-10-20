package com.dataforge.gui;

import com.dataforge.config.DatabaseConnection;
import com.dataforge.model.Report;
import com.dataforge.service.ReportService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

/**
 * Modern Swing GUI Dashboard for DATAFORGE HR and Project Management System.
 */
public class DashboardFrame extends JFrame {

    private final ReportService reportService = new ReportService();

    // KPI Card Labels
    private JLabel lblTotalEmployees;
    private JLabel lblTotalDepts;
    private JLabel lblTotalProjects;
    private JLabel lblTotalBudget;
    private JLabel lblTotalHours;

    // Report Viewer Controls
    private JComboBox<ReportItem> cmbReports;
    private JTextArea txtProblemDesc;
    private JTextArea txtSqlQuery;
    private JTable tblResults;
    private DefaultTableModel tableModel;
    private JLabel lblStatus;

    // Live Table Browser Controls
    private JComboBox<String> cmbTables;
    private JTable tblLiveBrowser;
    private DefaultTableModel liveTableModel;
    private JLabel lblLiveStatus;

    public DashboardFrame() {
        setTitle("DATAFORGE - HR & Project Management System Dashboard");
        setSize(1100, 750);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
        refreshKPIs();
        loadReport(1);
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        mainPanel.setBackground(new Color(245, 247, 250));

        // 1. Header Panel
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Central Content (KPIs + Tabbed Pane)
        JPanel centerPanel = new JPanel(new BorderLayout(12, 12));
        centerPanel.setOpaque(false);
        centerPanel.add(createKPICardsPanel(), BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.addTab("  Report Center (15 Queries)  ", createReportsTab());
        tabbedPane.addTab("  Live Database Tables  ", createLiveBrowserTab());

        centerPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // 3. Footer Bar
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 41, 59));
        header.setBorder(new EmptyBorder(14, 18, 14, 18));

        JLabel title = new JLabel("DATAFORGE  |  HR & Project Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Enterprise Analytics & Query Engine (MySQL 8.0 + JDBC)");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(148, 163, 184));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        textPanel.setOpaque(false);
        textPanel.add(title);
        textPanel.add(subtitle);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        boolean connected = DatabaseConnection.testConnection();
        JLabel badge = new JLabel(connected ? "● DB Connected" : "○ DB Disconnected");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        badge.setForeground(connected ? new Color(34, 197, 94) : new Color(239, 68, 68));

        JButton btnRefreshAll = new JButton("Refresh Data");
        btnRefreshAll.setFocusPainted(false);
        btnRefreshAll.addActionListener(e -> {
            refreshKPIs();
            executeSelectedReport();
        });

        rightPanel.add(badge);
        rightPanel.add(btnRefreshAll);

        header.add(textPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel createKPICardsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 10, 10));
        panel.setOpaque(false);

        lblTotalEmployees = new JLabel("--", SwingConstants.CENTER);
        lblTotalDepts = new JLabel("--", SwingConstants.CENTER);
        lblTotalProjects = new JLabel("--", SwingConstants.CENTER);
        lblTotalBudget = new JLabel("--", SwingConstants.CENTER);
        lblTotalHours = new JLabel("--", SwingConstants.CENTER);

        panel.add(createCard("Employees", lblTotalEmployees, new Color(59, 130, 246)));
        panel.add(createCard("Departments", lblTotalDepts, new Color(16, 185, 129)));
        panel.add(createCard("Projects", lblTotalProjects, new Color(139, 92, 246)));
        panel.add(createCard("Total Budget", lblTotalBudget, new Color(245, 158, 11)));
        panel.add(createCard("Logged Hours", lblTotalHours, new Color(236, 72, 153)));

        return panel;
    }

    private JPanel createCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(10, 12, 10, 12)
        ));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLbl.setForeground(new Color(100, 116, 139));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(accentColor);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createReportsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 5, 5, 5));

        // Top Query Selector Toolbar
        JPanel topBar = new JPanel(new BorderLayout(10, 10));
        topBar.setOpaque(false);

        cmbReports = new JComboBox<>();
        cmbReports.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        Map<Integer, Report> reports = reportService.getAllReports();
        for (Report r : reports.values()) {
            cmbReports.addItem(new ReportItem(r));
        }

        cmbReports.addActionListener(e -> {
            ReportItem item = (ReportItem) cmbReports.getSelectedItem();
            if (item != null) {
                loadReport(item.report.getId());
            }
        });

        JButton btnRun = new JButton("▶ Run Query");
        btnRun.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRun.setBackground(new Color(37, 99, 235));
        btnRun.setForeground(Color.WHITE);
        btnRun.setFocusPainted(false);
        btnRun.addActionListener(e -> executeSelectedReport());

        topBar.add(new JLabel("Select Report: "), BorderLayout.WEST);
        topBar.add(cmbReports, BorderLayout.CENTER);
        topBar.add(btnRun, BorderLayout.EAST);

        // Details Panel (Problem Statement + SQL Query)
        JPanel detailsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        detailsPanel.setOpaque(false);
        detailsPanel.setPreferredSize(new Dimension(800, 110));

        txtProblemDesc = new JTextArea();
        txtProblemDesc.setEditable(false);
        txtProblemDesc.setLineWrap(true);
        txtProblemDesc.setWrapStyleWord(true);
        txtProblemDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtProblemDesc.setBackground(new Color(248, 250, 252));
        txtProblemDesc.setBorder(new EmptyBorder(6, 6, 6, 6));

        JScrollPane spProblem = new JScrollPane(txtProblemDesc);
        spProblem.setBorder(BorderFactory.createTitledBorder("Problem Statement"));

        txtSqlQuery = new JTextArea();
        txtSqlQuery.setEditable(false);
        txtSqlQuery.setLineWrap(true);
        txtSqlQuery.setWrapStyleWord(true);
        txtSqlQuery.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtSqlQuery.setBackground(new Color(248, 250, 252));
        txtSqlQuery.setForeground(new Color(15, 23, 42));
        txtSqlQuery.setBorder(new EmptyBorder(6, 6, 6, 6));

        JScrollPane spSql = new JScrollPane(txtSqlQuery);
        spSql.setBorder(BorderFactory.createTitledBorder("SQL Query (from Answer Key)"));

        detailsPanel.add(spProblem);
        detailsPanel.add(spSql);

        // Results Table
        tableModel = new DefaultTableModel();
        tblResults = new JTable(tableModel);
        tblResults.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblResults.setRowHeight(26);
        tblResults.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblResults.getTableHeader().setBackground(new Color(241, 245, 249));
        tblResults.setFillsViewportHeight(true);

        JScrollPane spTable = new JScrollPane(tblResults);
        spTable.setBorder(BorderFactory.createTitledBorder("Query Execution Output"));

        lblStatus = new JLabel("Ready.");
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblStatus.setForeground(new Color(100, 116, 139));

        JPanel centerReports = new JPanel(new BorderLayout(6, 6));
        centerReports.setOpaque(false);
        centerReports.add(detailsPanel, BorderLayout.NORTH);
        centerReports.add(spTable, BorderLayout.CENTER);
        centerReports.add(lblStatus, BorderLayout.SOUTH);

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(centerReports, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLiveBrowserTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 5, 5, 5));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topBar.setOpaque(false);

        cmbTables = new JComboBox<>(new String[]{"departments", "employees", "projects", "project_assignments"});
        cmbTables.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton btnLoadTable = new JButton("View Table Data");
        btnLoadTable.setFocusPainted(false);
        btnLoadTable.addActionListener(e -> loadLiveTable((String) cmbTables.getSelectedItem()));

        topBar.add(new JLabel("Database Table:"));
        topBar.add(cmbTables);
        topBar.add(btnLoadTable);

        liveTableModel = new DefaultTableModel();
        tblLiveBrowser = new JTable(liveTableModel);
        tblLiveBrowser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblLiveBrowser.setRowHeight(25);
        tblLiveBrowser.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblLiveBrowser.setFillsViewportHeight(true);

        JScrollPane spLive = new JScrollPane(tblLiveBrowser);

        lblLiveStatus = new JLabel("Select a table to view live records.");
        lblLiveStatus.setFont(new Font("Segoe UI", Font.ITALIC, 11));

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(spLive, BorderLayout.CENTER);
        panel.add(lblLiveStatus, BorderLayout.SOUTH);

        // Auto load first table
        loadLiveTable("departments");

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        JLabel lblCopyright = new JLabel("College Project | Team DATAFORGE | Java JDK 26 + MySQL Connector/J");
        lblCopyright.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCopyright.setForeground(new Color(100, 116, 139));

        footer.add(lblCopyright, BorderLayout.WEST);
        return footer;
    }

    private void loadReport(int reportId) {
        Report report = reportService.getReport(reportId);
        if (report != null) {
            txtProblemDesc.setText(report.getQuestion());
            txtSqlQuery.setText(report.getSqlQuery());
            executeSelectedReport();
        }
    }

    private void executeSelectedReport() {
        ReportItem item = (ReportItem) cmbReports.getSelectedItem();
        if (item == null) return;

        Report report = item.report;
        long startTime = System.currentTimeMillis();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(report.getSqlQuery());
             ResultSet rs = stmt.executeQuery()) {

            populateTable(rs, tableModel);
            long elapsed = System.currentTimeMillis() - startTime;
            lblStatus.setText(String.format("Successfully executed Report Q%d in %d ms (%d rows returned)",
                    report.getId(), elapsed, tableModel.getRowCount()));
        } catch (SQLException e) {
            lblStatus.setText("SQL Error: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Execution Error:\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadLiveTable(String tableName) {
        if (tableName == null) return;
        String sql = "SELECT * FROM " + tableName;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            populateTable(rs, liveTableModel);
            lblLiveStatus.setText("Table '" + tableName + "' loaded (" + liveTableModel.getRowCount() + " rows)");
        } catch (SQLException e) {
            lblLiveStatus.setText("Error loading table: " + e.getMessage());
        }
    }

    private void populateTable(ResultSet rs, DefaultTableModel model) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        Vector<String> columnNames = new Vector<>();
        for (int i = 1; i <= columnCount; i++) {
            String label = meta.getColumnLabel(i);
            columnNames.add(label != null && !label.isEmpty() ? label : meta.getColumnName(i));
        }

        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                Object val = rs.getObject(i);
                row.add(val == null ? "NULL" : val);
            }
            data.add(row);
        }

        model.setDataVector(data, columnNames);
    }

    private void refreshKPIs() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Employees count
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM employees")) {
                if (rs.next()) lblTotalEmployees.setText(String.valueOf(rs.getInt(1)));
            }

            // Departments count
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM departments")) {
                if (rs.next()) lblTotalDepts.setText(String.valueOf(rs.getInt(1)));
            }

            // Projects count
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM projects")) {
                if (rs.next()) lblTotalProjects.setText(String.valueOf(rs.getInt(1)));
            }

            // Total Budget
            try (ResultSet rs = stmt.executeQuery("SELECT COALESCE(SUM(budget), 0) FROM projects")) {
                if (rs.next()) {
                    double budget = rs.getDouble(1);
                    lblTotalBudget.setText(currencyFormat.format(budget).replace(".00", ""));
                }
            }

            // Total Hours
            try (ResultSet rs = stmt.executeQuery("SELECT COALESCE(SUM(hours), 0) FROM project_assignments")) {
                if (rs.next()) {
                    lblTotalHours.setText(rs.getInt(1) + " hrs");
                }
            }

        } catch (SQLException e) {
            System.err.println("Could not load KPI metrics: " + e.getMessage());
        }
    }

    /**
     * Helper wrapper for combo box display.
     */
    private static class ReportItem {
        final Report report;

        ReportItem(Report report) {
            this.report = report;
        }

        @Override
        public String toString() {
            return String.format("Q%02d [%s] - %s", report.getId(), report.getDifficulty(), report.getTitle());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new DashboardFrame().setVisible(true);
        });
    }
}
