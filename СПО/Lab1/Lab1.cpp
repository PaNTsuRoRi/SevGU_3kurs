#include <iostream> 
#include <string> 

using namespace std;

struct Node {
	int x, address, value;
	Node* next, * prev;
	Node(int a, int val) : address(a), value(val), next(nullptr) {}
};

struct List {
	Node* head = nullptr; 
	Node* tail = nullptr;

	void add(int a, int val) {
		Node* p = new Node(a, val); 
		if (head != nullptr) {
			p->prev = tail; 
			tail->next = p; 
			tail = p;
		}
		else {
			p->next = nullptr; 
			p->prev = nullptr; 
			head = tail = p;
		}
	}

	bool isEmpty() {
		return head == nullptr;
	}

	string print() {
		string result; 
		Node* p = head; 
		while (p) {
			result = result + "[" + to_string(p->address) + ", " + to_string(p->value) + "]" +
				+((p->next != nullptr) ? " -> " : ";"); p = p->next;
		}
		return result;
	}

	void remove(int a) {
		if (isEmpty()) return;
		if (head->address == a) {
			Node* p = head; 
			head = p->next; 
			delete p;
			return;
		}
		else if (tail->address == a) {
			Node* p = tail;
			tail = p->prev;
			tail->next = nullptr;
			if (head->next == p) {
				head->next = nullptr;
			}
			delete p; 
			return;
		}

		Node *node1 = head;

		Node *node2 = head->next;

		while (node2 && node2->address != a) {
			node2 = node2->next;
			node1 = node1->next;
		}
		if (!node2) {
			cout << "Элемент не существует" << endl; 
			return;
		}

		node1->next = node2->next; 
		delete node2;
	}

	Node* operator[] (const int index) {
		if (isEmpty()) return nullptr; 
		Node* p = head;
		for (int i = 0; i < index; i++) {
			p = p->next;
			if (!p) return nullptr;
		}
		return p;
	}

	void swapData(Node* first, Node* second)
	{
		int value = first->value;
		int address = first->address; first->value = second->value;
		first->address = second->address; second->value = value;
		second->address = address;
	}

	int length() {
		int result = 0; 
		Node* p = head; 
		while (p) {
			result++;
			p = p->next;
		}
		return result;
	}
};

struct Memory {
	List filled; 
	List free; 
	int size;
	Memory(int s) : size(s) { free.add(0, s); }

	void sort(){
		Node* left = free.head;
		Node* right = free.head->next;

		Node* temp = nullptr;
		while (left->next) {
			while (right) {
				if ((left->x) < (right->x)) {
					temp->x = left->x;
					left->x = right->x; 
					right->x = temp->x;
				}
				right = right->next;
			}
			left = left->next; right = left->next;
		}
	}

	void add(int val) {
		for (int i = 0; i < free.length(); i++) {
			Node* cell = free[i];
			if (cell->value >= val) {
				filled.add(cell->address, val);
				cell->address = cell->address + val; 
				cell->value = cell->value - val; 
				sort();
				return;
			}
		}
		cout << "Недостаточно места" << endl;
	}

	void destroy(int a) {
		for (int i = 0; i < filled.length(); i++) {
			Node* cell = filled[i];
			if (cell->address == a) {
				free.add(a, cell->value);
				filled.remove(cell->address); 
				sort();
				merge(); 
				return;
			}
		}
		cout << "Неправильный адрес" << endl;
	}

	void merge() {
		for (int i = 0; i < free.length(); i++) {
			Node* cell = free[i];
			for (int j = 0; j < free.length(); j++) {
				if (cell->address + cell->value == free[j]->address) {
					cell->value = cell->value + free[j]->value; 
					free.remove(free[j]->address);
					merge(); 
					return;
				}
			}
		}
	}

	string print() {
		string result;
		result = "Список занятых областей: " + filled.print() + "\n" + "Список свободных областей: " +
			free.print() + "\n"; return result;
	}
};

int Menu(Memory* memory) {
	int choice;
	int value;

	cout << endl;
	cout << "1 - Выделить память" << endl; 
	cout << "2 - Освободить память" << endl; 
	cout << "3 - Просмотреть память" << endl; 
	cout << "4 - Выход" << endl;
	cin >> choice;

	switch (choice)
	{
	case 1:
		cout << "Введите размер выделяемой памяти: "; cin >> value;
		memory->add(value); return 1;
	case 2:
		cout << "Введите адрес: "; cin >> value;
		memory->destroy(value); return 1;
	case 3:
		cout << memory->print(); return 1;
	case 4:
	return 0; default:
		return 0; break;
	}
}

int main() {
	setlocale(LC_CTYPE, "rus");

	int size;
	cout << "Введите размер памяти: "; 
	cin >> size;
	Memory* memory = new Memory(size); 
	memory->add(250);
	memory->add(350);
	memory->add(150); 
	memory->destroy(250);

	cout << memory->print();

	while (true) { 
		if (Menu(memory) == 0) break;  } 
}
