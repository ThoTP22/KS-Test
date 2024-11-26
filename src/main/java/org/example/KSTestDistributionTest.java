package org.example;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class KSTestDistributionTest {
    public static void main(String[] args) throws IOException {

        testFindBestFitDistribution();
    }



    private static void testFindBestFitDistribution() throws IOException {
        // Đọc dữ liệu từ file
        Integer[] data = FileReaderUtil.readCSV("src/main/java/org/example/dataset_100mb.csv", Integer::parseInt);

        // Lấy mẫu ngẫu nhiên từ dữ liệu
        Integer[] sampleData = SampleGenerator.generateSampleData(data);

        // Sắp xếp dữ liệu mẫu
        Arrays.sort(sampleData);

        // Tìm phân phối phù hợp nhất
        String bestFitDistribution = DistributionCalculator.findBestFitDistribution(sampleData);

        // Kiểm tra kết quả
        assert bestFitDistribution != null && !bestFitDistribution.isEmpty() : "Error: Best fit distribution should not be null or empty.";

        // In kết quả
        System.out.println("testFindBestFitDistribution passed.");
        System.out.println("Best Fit Distribution: " + bestFitDistribution);
    }

}
