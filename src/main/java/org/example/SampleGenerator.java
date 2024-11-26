package org.example;

import java.util.Random;

public class SampleGenerator {

    /**
     * Tạo mẫu ngẫu nhiên 10% từ dữ liệu đầu vào.
     *
     * @param data Mảng dữ liệu đầu vào (kiểu tổng quát T).
     * @return Mảng chứa 10% dữ liệu ngẫu nhiên.
     */
    public static <T extends Number> T[] generateSampleData(T[] data) {
        // Tính số lượng phần tử cần lấy
        int sampleSize = (int) Math.ceil(data.length * 0.1);

        // Tạo mảng chứa chỉ số của các phần tử
        int[] indices = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            indices[i] = i;
        }

        // Trộn ngẫu nhiên các chỉ số
        Random random = new Random();
        for (int i = indices.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = indices[i];
            indices[i] = indices[j];
            indices[j] = temp;
        }

        // Lấy mẫu dựa trên các chỉ số đã trộn
        @SuppressWarnings("unchecked")
        T[] sample = (T[]) java.lang.reflect.Array.newInstance(data.getClass().getComponentType(), sampleSize);
        for (int i = 0; i < sampleSize; i++) {
            sample[i] = data[indices[i]];
        }

        return sample;
    }
}
