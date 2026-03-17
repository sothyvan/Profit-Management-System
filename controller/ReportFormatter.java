package controller;

@FunctionalInterface
public interface ReportFormatter {
    String format(double value);
}
