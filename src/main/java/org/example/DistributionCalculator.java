package org.example;

import java.util.Arrays;

public class DistributionCalculator {
    public static <T extends Number> String findBestFitDistribution(T[] data) {
        double minKSStatistic = Double.MAX_VALUE;
        String bestFitDistribution = "";

        // Kiểm định với phân phối chuẩn
        double normalStatistic = kolmogorovSmirnovStatistic(data, "normal");
        if (normalStatistic < minKSStatistic) {
            minKSStatistic = normalStatistic;
            bestFitDistribution = "Normal Distribution";
        }

        // Kiểm định với phân phối đồng đều
        double uniformStatistic = kolmogorovSmirnovStatistic(data, "uniform");
         if(uniformStatistic < minKSStatistic) {
            minKSStatistic = uniformStatistic;
            bestFitDistribution = "Uniform Distribution";
        }
         double lognormalStatistic =kolmogorovSmirnovStatistic(data, "lognormal");
             if(lognormalStatistic < minKSStatistic) {
                 minKSStatistic = lognormalStatistic;
                 bestFitDistribution = "Log-normal Distribution";
             }
         double exponentialStatistic = kolmogorovSmirnovStatistic(data, "exponential");
                if(exponentialStatistic < minKSStatistic) {
                    minKSStatistic = exponentialStatistic;
                    bestFitDistribution = "Exponential Distribution";
                }
         if(minKSStatistic >= 0.5) {
             bestFitDistribution="Unknow Distribution";
         }

        return bestFitDistribution;
    }

    private static <T extends Number> double kolmogorovSmirnovStatistic(T[] data, String distributionType) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Data array must not be null or empty.");
        }

        double statistic = 0.0;
        int n = data.length;
        double[] empiricalCDF = new double[n];

        // Sắp xếp dữ liệu và tính CDF thực nghiệm
        double[] sortedData = Arrays.stream(data).mapToDouble(Number::doubleValue).sorted().toArray();

        for (int i = 0; i < n; i++) {
            empiricalCDF[i] = (double) (i + 1) / n;
        }

        switch (distributionType.toLowerCase()) {
            case "normal": {
                double mean = mean(sortedData);
                double stddev = standardDeviation(sortedData);

                for (int i = 0; i < n; i++) {
                    double theoreticalCDF = cumulativeNormalDistribution(sortedData[i], mean, stddev);
                    statistic = Math.max(statistic, Math.abs(empiricalCDF[i] - theoreticalCDF));
                }
                break;
            }
            case "uniform": {
                double min = min(sortedData);
                double max = max(sortedData);

                if (min == max) {
                    throw new IllegalArgumentException("Uniform distribution requires min != max.");
                }

                for (int i = 0; i < n; i++) {
                    double theoreticalCDF = cumulativeUniformDistribution(sortedData[i], min, max);
                    statistic = Math.max(statistic, Math.abs(empiricalCDF[i] - theoreticalCDF));
                }
                break;
            }
            case "lognormal": {
                double[] logData = Arrays.stream(sortedData).map(Math::log).toArray();
                double logMean = mean(logData);
                double logStddev = standardDeviation(logData);

                for (int i = 0; i < n; i++) {
                    double theoreticalCDF = cumulativeLogNormalDistribution(sortedData[i], logMean, logStddev);
                    statistic = Math.max(statistic, Math.abs(empiricalCDF[i] - theoreticalCDF));
                }
                break;
            }
            case "exponential": {
                double mean = mean(sortedData);
                for (int i = 0; i < n; i++) {
                    double theoreticalCDF = cumulativeExponentialDistribution(sortedData[i], mean);
                    statistic = Math.max(statistic, Math.abs(empiricalCDF[i] - theoreticalCDF));
                }
                break;
            }
            default:
                throw new IllegalArgumentException("Unsupported distribution type: " + distributionType);
        }

        // In kết quả cuối cùng
        System.out.printf("Kolmogorov-Smirnov Statistic (%s): %.4f%n", distributionType, statistic);
        return statistic;
    }




    private static double mean(double[] data) {
        return Arrays.stream(data).average().orElse(0.0);
    }
//-------------------------------Gaussian Distribution--------------------------------------------------
    private static double standardDeviation(double[] data) {
        double mean = mean(data);
        double variance = Arrays.stream(data).map(x -> Math.pow(x - mean, 2)).average().orElse(0.0);
        return Math.sqrt(variance);
    }



    private static double cumulativeNormalDistribution(double x, double mean, double stddev) {
        if (stddev <= 0) {
            throw new IllegalArgumentException("Standard deviation must be greater than 0.");
        }

        double z = (x - mean) / (stddev * Math.sqrt(2));
        return 0.5 * (1 + erfTaylor(z));
    }

    private static double cumulativeUniformDistribution(double x, double min, double max) {
        if (x < min) return 0.0;
        if (x > max) return 1.0;
        return (x - min) / (max - min);
    }

    private static double erfTaylor(double z) {
        double sum = 0.0;
        double term = z; // Term đầu tiên
        double factorial = 1.0; // n!
        double power = z; // z^(2n+1)
        int n = 0;

        // Hằng số cho chuỗi Taylor
        double constant = 2 / Math.sqrt(Math.PI);

        // Chuỗi Taylor: Lặp cho đến khi term rất nhỏ
        while (Math.abs(term) > 1e-6) {
            sum += term;
            n++;
            factorial *= n; // Tính giai thừa
            power *= z * z; // Cập nhật z^(2n+1)
            term = (Math.pow(-1, n) * power) / (factorial * (2 * n + 1)); // Term tiếp theo
        }

        return constant * sum;
    }
    //-------------------------------------------------------------------------------------------------------

    //-----------------------------------Uniform Distribution----------------------------------
    private static double min(double[] data) {
        return Arrays.stream(data).min().orElse(0);
    }

    private static double max(double[] data) {
        return Arrays.stream(data).max().orElse(0);
    }
    //------------------------------------------------------------------------------------------
    //-------------------------------------Log-normal Distribution--------------------------
    private static double cumulativeLogNormalDistribution(double x, double logMean, double logStddev) {
        if (x <= 0) return 0.0; // Log-Normal chỉ định nghĩa cho x > 0
        return cumulativeNormalDistribution(Math.log(x), logMean, logStddev);
    }
    //--------------------------------------------------------------------------------------

    //------------------------------------Exponential Distribution--------------------------------
    private static double cumulativeExponentialDistribution(double x, double mean) {
        if (x < 0) return 0.0; // Exponential is defined for x >= 0
        double lambda = 1.0 / mean; // Rate parameter λ = 1/mean
        return 1 - Math.exp(-lambda * x);
    }
    //------------------------------------------------------------------------------------------------

}
