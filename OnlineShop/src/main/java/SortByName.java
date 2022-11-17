import java.util.Comparator;

public class SortByName implements Comparator<Inventory> {
    @Override
    public int compare(Inventory o1, Inventory o2) {
        char[] o1CharArray = o1.productName.toCharArray();
        char[] o2CharArray = o2.productName.toCharArray();
        return o1CharArray[0] - o2CharArray[0];
        //return o1.productName.indexOf(0) - o2.productName.indexOf(0);
    }
}
