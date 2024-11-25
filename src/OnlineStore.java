import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class OnlineStore {

    public static void main(String[] args) {

        System.out.println("Система онлайн-магазину запускається...");

        CompletableFuture<Void> systemStart = CompletableFuture.runAsync(() -> {
            System.out.println("runAsync(): Ініціалізація системи...");
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("runAsync(): Система запущена!");
        });

        CompletableFuture<List<Product>> productLoader = CompletableFuture.supplyAsync(() -> {
            System.out.println("supplyAsync(): Завантаження списку товарів...");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Arrays.asList(
                    new Product("Ноутбук", 1500),
                    new Product("Смартфон", 800),
                    new Product("Навушники", 200),
                    new Product("Монітор", 300)
            );
        });

        CompletableFuture<List<Product>> filteredProducts = productLoader.thenApplyAsync(products -> {
            System.out.println("thenApplyAsync(): Фільтрація товарів за ціною > 300...");
            return products.stream()
                    .filter(product -> product.getPrice() > 300)
                    .collect(Collectors.toList());
        });

        CompletableFuture<Void> resultPrinter = filteredProducts.thenAcceptAsync(products -> {
            System.out.println("thenAcceptAsync(): Відібрані товари:");
            products.forEach(product -> System.out.println(product.getName() + " - $" + product.getPrice()));
        });

        CompletableFuture<Void> finalTask = systemStart.thenRunAsync(() -> {
            System.out.println("thenRunAsync(): Усі задачі завершено. Система готова до роботи.");
        });

        CompletableFuture.allOf(systemStart, productLoader, filteredProducts, resultPrinter, finalTask).join();

        System.out.println("Програма завершена.");
    }
}

class Product {
    private String name;
    private double price;

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}
