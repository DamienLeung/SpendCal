import java.io.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class SpendCal {

    public static void main(String[] args) {
        System.out.println("Please input the type of data:");
        System.out.println("1. Input payment record file name.");
        System.out.println("2. Input payment manually.");
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        if (s.equals("1")) {
            ConcurrencyRateThread thread = new ConcurrencyRateThread();
            thread.start();

            System.out.println("Please input your file name with path:");
            String fileName;
            try {
                BufferedReader isr;
                while (!(fileName = scanner.nextLine()).equals("quit")) {
                    File file = new File(fileName);
                    isr = new BufferedReader(new FileReader(file));
                    String spend;
                    while ((spend = isr.readLine()) != null) {
                        soutSpend(spend, thread.spend);
                    }
                    isr.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (s.equals("2")) {
            ConcurrencyRateThread thread = new ConcurrencyRateThread();
            thread.start();
            System.out.println("Please input your spend and press enter key to add the next, if you want to quit, please input \"quit\": ");
            String spend;
            while (!(spend = scanner.nextLine()).equals("quit")) {
                soutSpend(spend, thread.spend);
            }
        }

        System.exit(1);

    }

    public static void soutSpend(String spend, ConcurrentHashMap<String, Double> spends) {
        String[] strings = spend.split(" ");
        if (strings.length != 2)
            System.out.println("Please enter spending with format(example: \"AUS 2000.00\")" + "\nYour input: " + spend);
        else {
            String concurrent = strings[0].toUpperCase();
            try {
                if (!strings[1].equals("0")) {
                    double amount = Double.parseDouble(strings[1]);
                    spends.computeIfPresent(concurrent, (s, aDouble) -> aDouble + amount);
                    spends.putIfAbsent(concurrent, amount);
                }
            } catch (NumberFormatException e) {
                System.out.println("Please input correct amount format(example: \"20000.00\"):" + "\nYour amount: " + strings[1]);
            }
        }
    }
}

class ConcurrencyRateThread extends Thread {

    public ConcurrentHashMap<String, Double> spend = new ConcurrentHashMap<>();
    public HashMap<String, Double> concurrency = new HashMap<>();

    public ConcurrencyRateThread() {
        concurrency = new Concurrency().initial();
    }

    @Override
    public void run() {
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        while (true) {
            try {
                sleep(1000 * 10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("-----------------------");
            System.out.println("Net amounts:");
            for (String concurrent : spend.keySet()) {
                if (concurrency.containsKey(concurrent)) {
                    System.out.println(concurrent + " " + decimalFormat.format(spend.get(concurrent)) + "(USD "
                            + decimalFormat.format(spend.get(concurrent) / concurrency.get(concurrent)) + ")");
                } else {
                    System.out.println(concurrent + " " + decimalFormat.format(spend.get(concurrent)));
                }
            }
            System.out.println("-----------------------");

        }
    }
}


class Concurrency {

    public HashMap<String, Double> initial() {
        HashMap<String, Double> concurrency = new HashMap<>();
        concurrency.put("EUR", 0.91);
        concurrency.put("GBP", 0.81);
        concurrency.put("AUD", 1.52);
        concurrency.put("CAD", 1.4);
        concurrency.put("SGD", 1.42);
        concurrency.put("JPY", 107.43);
        concurrency.put("CNY", 7.12);
        concurrency.put("TWD", 29.99);
        concurrency.put("HKD", 7.75);
        return concurrency;
    }

}
