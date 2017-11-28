package xjw.app.hencoder.annotation;

/**
 * Created by xjw on 2017/9/4.
 * 单链表
 */

public class MyLinkClass {

    public static void main(String[] args) {
        MyLinkClass link = new MyLinkClass();
        link.add(1);
        link.add(2);
        link.add(3);
        System.out.println(String.valueOf(link.length()));
        link.remove(2);
        System.out.println(String.valueOf(link.length()));
    }

    class Node {
        Node next;
        int data;

        public Node(int data) {
            this.data = data;
        }
    }

    private Node head;

    private void add(int data) {
        Node node = new Node(data);
        if (head == null) {
            head = node;
            return;
        }
        Node tmp = head;
        while (tmp.next != null) {
            tmp = tmp.next;
        }
        tmp.next = node;
    }

    private int length() {
        if (head == null) {
            return 0;
        }
        int result = 1;
        Node tmp = head;
        while (tmp.next != null) {
            tmp = tmp.next;
            result++;
        }
        return result;
    }

    private boolean remove(int index) {
        if (index < 1 || index > length()) {
            return false;
        }
        if (index == 1) {
            head = head.next;
            return true;
        }
        int a = 2;
        Node cur = head;
        Node tmp = head.next;
        while (tmp.next != null) {
            if (a == index) {
                cur.next = tmp.next;
                return true;
            }
            cur = tmp;
            tmp = cur.next;
            a++;
        }
        return false;
    }


}
