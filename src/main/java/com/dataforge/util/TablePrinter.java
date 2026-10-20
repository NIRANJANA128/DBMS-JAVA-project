package com.dataforge.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to print JDBC ResultSets as formatted ASCII tables on the console.
 */
public class TablePrinter {

    /**
     * Prints the given ResultSet formatted as an ASCII grid.
     *
     * @param rs the active ResultSet to render
     * @return the number of rows printed
     * @throws SQLException if a database access error occurs
     */
    public static int printResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        // Column headers
        String[] headers = new String[columnCount];
        int[] columnWidths = new int[columnCount];

        for (int i = 0; i < columnCount; i++) {
            headers[i] = meta.getColumnLabel(i + 1);
            if (headers[i] == null || headers[i].trim().isEmpty()) {
                headers[i] = meta.getColumnName(i + 1);
            }
            columnWidths[i] = headers[i].length();
        }

        // Buffer all rows to calculate dynamic column widths
        List<String[]> rows = new ArrayList<>();
        while (rs.next()) {
            String[] row = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                Object val = rs.getObject(i + 1);
                if (val == null) {
                    row[i] = "NULL";
                } else {
                    row[i] = String.valueOf(val);
                }
                if (row[i].length() > columnWidths[i]) {
                    columnWidths[i] = row[i].length();
                }
            }
            rows.add(row);
        }

        // Print table top border
        printSeparator(columnWidths);

        // Print header row
        printRow(headers, columnWidths);

        // Print header separator
        printSeparator(columnWidths);

        // Print data rows
        if (rows.isEmpty()) {
            System.out.println("| (No records found matching criteria) " + " ".repeat(Math.max(0, getTotalWidth(columnWidths) - 38)) + "|");
        } else {
            for (String[] row : rows) {
                printRow(row, columnWidths);
            }
        }

        // Print table bottom border
        printSeparator(columnWidths);

        System.out.println("Result: " + rows.size() + " row(s) returned.\n");
        return rows.size();
    }

    private static void printRow(String[] values, int[] widths) {
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < values.length; i++) {
            sb.append(" ");
            sb.append(values[i]);
            int padding = widths[i] - values[i].length();
            if (padding > 0) {
                sb.append(" ".repeat(padding));
            }
            sb.append(" |");
        }
        System.out.println(sb);
    }

    private static void printSeparator(int[] widths) {
        StringBuilder sb = new StringBuilder("+");
        for (int width : widths) {
            sb.append("-".repeat(width + 2));
            sb.append("+");
        }
        System.out.println(sb);
    }

    private static int getTotalWidth(int[] widths) {
        int sum = 1;
        for (int w : widths) {
            sum += w + 3;
        }
        return sum;
    }
}
