package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FileReaderUtil {
    public static <T extends Number> T[] readCSV(String filePath, Function<String, T> parser) throws IOException {
        List<T> numbers = new ArrayList<>();

        // Đọc file CSV
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;

            // Bỏ qua dòng đầu tiên nếu là header
            line = br.readLine();
            if (line != null && line.matches("[a-zA-Z,\\s]+")) {
                line = br.readLine();
            }

            // Đọc các dòng còn lại
            while (line != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                        numbers.add(parser.apply(line)); // Chuyển đổi chuỗi thành số
                    } catch (NumberFormatException e) {
                        System.err.println("Dữ liệu không hợp lệ, bỏ qua dòng: " + line);
                    }
                }
                line = br.readLine();
            }
        }

        // Chuyển danh sách sang mảng
        @SuppressWarnings("unchecked")
        T[] result = (T[]) java.lang.reflect.Array.newInstance(numbers.get(0).getClass(), numbers.size());
        return numbers.toArray(result);
    }
}
