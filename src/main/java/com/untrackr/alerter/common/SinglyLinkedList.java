package com.untrackr.alerter.common;

import java.util.LinkedList;
import java.util.List;

public class SinglyLinkedList<T> {

	private T head;
	private SinglyLinkedList<T> tail;

	public SinglyLinkedList(T head, SinglyLinkedList<T> tail) {
		this.head = head;
		this.tail = tail;
	}

	public T getHead() {
		return head;
	}

	public SinglyLinkedList<T> getTail() {
		return tail;
	}

	public T last() {
		SinglyLinkedList<T> cursor = this;
		while (cursor.tail != null) {
			cursor = cursor.tail;
		}
		return cursor.head;
	}

	public List<T> reverse() {
		LinkedList<T> list = new LinkedList<>();
		SinglyLinkedList<T> cursor = this;
		while (cursor != null) {
			list.push(cursor.head);
			cursor = cursor.tail;
		}
		return list;
	}

}
