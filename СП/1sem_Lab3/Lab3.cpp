#include <iostream>
#include <string>
using namespace std;

struct Tree
{
	Tree* left;
	Tree* right;
	int num;
	Tree(int n = 0, Tree* l = 0, Tree* r = 0) :num(n), left(l), right(r) {}
};

class BTree
{
	Tree* root;
public:
	BTree() {
		root = NULL;
	}
	void add(Tree*& t, int number) {
		if (t == NULL) {
			t = new Tree(number);
		}
		else {
			if (number < t->num) {
				add(t->left, number);
			}
			else {
				add(t->right, number);
			}
		}
	}

	void show(Tree*& t) {
		if (t != NULL) {
			\
				cout << t->num << " ";
			show(t->left);
			show(t->right);
		}
	}

	void calc(Tree*& t, int& min, int& max) {
		int _min, _max;
		min = t->num;
		max = t->num;
		if (t != NULL) {
			if (t->left) {
				calc(t->left, _min, _max);
				if (_min < min) {
					min = _min;
				}
				if (_max > max) {
					max = _max;
				}
			}
			if (t->right) {
				calc(t->right, _min, _max);
				if (_min < min) {
					min = _min;
				}
				if (_max > max) {
					max = _max;
				}
			}
		}
	}

	void addItem(int number) {
		add(root, number);
	}

	void showtree() {
		cout << endl << "Вывод элементов дерева обратным проходом: \n";
		show(root);
		cout << endl;
	}

	void calcMinMaxAverage() {
		int _min, _max;
		calc(root, _min, _max);
		cout << endl << "Минимальный элемент дерева: " << _min << endl;
		cout << "Максимальный элемент дерева: " << _max << endl;
		cout << "Среднее арифметическое: " << (_min + _max) / 2 << endl;
	}
};

int main()
{
	setlocale(LC_ALL, "Rus");
	BTree tree;
	int n[] = { 10, 5, 15, 3,8,13,18 };
	cout << "Элементы дерева: " << endl;
	for (int i = 0; i != size(n); i++) {
		//int n = rand() % 99;
		int k = n[i];
		cout << k << " | ";
		tree.addItem(k);
	}
	cout << endl;
	tree.showtree();
	tree.calcMinMaxAverage();
	cout << endl;
	system("pause");
}