package crontabResourceMonitor;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ResourceMonitor {

    public static void main(String[] args) {
        try (FileWriter writer = new FileWriter("./resourceLog.txt", true);) {
            OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            String currentDateTime = formatter.format(new Date());
            
            double totalCpuUsage = 0;
            double totalMemorySizeGB, usedMemorySizeGB, freeMemorySizeGB;
            double rxMB = 0, txMB = 0;

            try {
                Process process = Runtime.getRuntime().exec("top -bn1");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("%Cpu(s):")) {
                        String[] cpuUsageParts = line.split(",");
                        String userCpu = cpuUsageParts[0].split(":")[1].trim();
                        String systemCpu = cpuUsageParts[1].trim();

                        double userCpuValue = Double.parseDouble(userCpu.split(" ")[0]);
                        double systemCpuValue = Double.parseDouble(systemCpu.split(" ")[0]);
                        totalCpuUsage = userCpuValue + systemCpuValue;

                        break;
                    }
                }
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            long totalMemorySize = osBean.getTotalPhysicalMemorySize();
            long freeMemorySize = osBean.getFreePhysicalMemorySize();
            long usedMemorySize = totalMemorySize - freeMemorySize;

            totalMemorySizeGB = (double) totalMemorySize / (1024 * 1024 * 1024);
            usedMemorySizeGB = (double) usedMemorySize / (1024 * 1024 * 1024);
            freeMemorySizeGB = (double) freeMemorySize / (1024 * 1024 * 1024);

            try {
                Process process = Runtime.getRuntime().exec("ip -s link");
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().startsWith("RX:")) {
                        String[] rxLineParts = reader.readLine().trim().split("\\s+");
                        long rxBytes = Long.parseLong(rxLineParts[0]);
                        rxMB = (double) rxBytes / (1024 * 1024);
                    } else if (line.trim().startsWith("TX:")) {
                        String[] txLineParts = reader.readLine().trim().split("\\s+");
                        long txBytes = Long.parseLong(txLineParts[0]);
                        txMB = (double) txBytes / (1024 * 1024);
                    }
                }

                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            writer.write(String.format("%s\tCPU: %.2f%%\tTotal Memory: %.2f GB\tUsed Memory: %.2f GB\tFree Memory: %.2f GB\tRX: %.2f MB\tTX: %.2f MB\n",
                    currentDateTime, totalCpuUsage, totalMemorySizeGB, usedMemorySizeGB, freeMemorySizeGB, rxMB, txMB));

            System.out.println("로그가 resourceLog.txt 파일에 저장되었습니다.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}