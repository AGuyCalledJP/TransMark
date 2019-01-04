public class Test {
    public static void main(String[] args) {
        int month = 0;
        for (int i = 0; i < 13; i++) {
            if (month < 10) {
                System.out.println("0" + month);
            } else {
                System.out.println(String.valueOf(month));
            }
            month++;
        }
    }
}
