import java.util.*;
public class Test {

    static class Key {
        Integer id;

        Key(int id ) {
            this.id = id;
        }

        public int hashCode() {
            return id.hashCode();
        }

        public boolean equals(Object o) {
            boolean flag = false;
            if(o instanceof Key) {
                flag = (((Key)o).id).equals(this.id);
            }
            return flag;
        }


    }

    public static void main(String[] args) {
        Map m = new HashMap();
        while(true) {
            for (int i = 0; i < 1000; i++) {
                if(!m.containsKey(new Key(i))) {
                    m.put(new Key(i), "Number: " + i);
                }
            }

            System.out.println("---------m.size()= " + m.size());
        }
    }
}