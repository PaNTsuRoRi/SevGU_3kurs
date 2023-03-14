#include <stdio.h>
#include <list>
#include <iostream>

using namespace std;

//то, что будет храниться в элементе дерева как данные
struct T {
	int id;
	string name;
	string type;
	double size;
	string dateLastUpdate;
	string atributs[10];
};
//узел дерева
struct Node {
	T data;
	struct Node* left;
	struct Node* right;
	struct Node* parent;
};

bool operator>(const T& t1, const T& t2) {
	return (t1.id > t2.id);
}

bool operator<(const T& t1, const T& t2) {
	return (t1.id < t2.id);
}
//создает узел
Node* getFreeNode(T value, Node* parent) {
	Node* tmp = new Node();
	tmp->left = tmp->right = NULL;
	tmp->data = value;
	tmp->parent = parent;
	return tmp;
}
//вставка узла в дерево
void insert(Node** head, T value) {
	Node* tmp = NULL;
	Node* ins = NULL;
	if (*head == NULL)
	{
		Node* temp = NULL;
		*head = getFreeNode(value, temp);
		return;
	}
	tmp = *head;
	while (tmp)
	{
		if ((value > tmp->data)) {
			if (tmp->right) {
				tmp = tmp->right;
				continue;
			}
			else {
				tmp->right = getFreeNode(value, tmp);
				return;
			}
		}
		else if ((value < tmp->data)) {
			if (tmp->left) {
				tmp = tmp->left;
				continue;
			}
			else {
				tmp->left = getFreeNode(value, tmp);
				return;
			}
		}
		else {
			exit(2);
		}
	}
}

Node* getMinNode(Node* root) {
	while (root->left) {
		root = root->left;
	}
	return root;
}

Node* getMaxNode(Node* root) {
	while (root->right)
	{
		root = root->right;
	}
	return root;
}

//поиск узла в дереве по значению, передаваемом в параметре
Node* getNodeByValue(Node* root, T value) {
	while (root) {
		if ((root->data > value)) {
			root = root->left;
			continue;
		}
		else if (root->data < value) {
			root = root->right;
			continue;
		}
		else {
			return root;
		}
	}
	return NULL;
}

void removeNodeByPtr(Node* target) {
	if (!target) {
		return;
	};
	if (target->left && target->right) {
		Node* localMax = getMaxNode(target->left);
		target->data = localMax->data;
		removeNodeByPtr(localMax);
		return;
	}
	else if (target->left) {
		if (target == target->parent->left) {
			target->parent->left = target->left;
		}
		else {
			target->parent->right = target->left;
		}
	}
	else if (target->right) {
		if (target == target->parent->right) {
			target->parent->right = target->right;
		}
		else {
			target->parent->left = target->right;
		}
	}
	else {
		if (target == target->parent->left) {
			target->parent->left = NULL;
		}
		else {
			target->parent->right = NULL;
		}
	}
	free(target);
}

void deleteValue(Node* root, T value) {
	Node* target = getNodeByValue(root, value);
	removeNodeByPtr(target);
}

//записывает в список элементы (обратный обход)
void backwardTraversal(Node*& tree, list<T>& ret) {
	if (NULL == tree) return;
	backwardTraversal(tree->left, ret);
	backwardTraversal(tree->right, ret);
	ret.push_back(tree->data);
}

//сортировка (выбором)
list<T> inclusionSort(list<T>& list) {
	list.sort();
	return list;
}

//правило сортировки
bool compare(const T& first, const T& second) {
	return (first.id < second.id);
}

int main() {
	setlocale(LC_ALL, "Rus");
	Node* root = NULL;
	list<T> listTree;
	cout << "Добавление тестовых данных..." << endl;
	insert(&root, T{ 85, "Файл_номер_1", ".png", 120, "26.09.1987", {"1", "2", "3"} });
	insert(&root, T{ 28, "Файл_номер_2", ".rar", 230, "31.01.2002", {"1", "2", "3"} });
	insert(&root, T{ 88, "Файл_номер_3", ".pdf", 340, "10.12.2001", {"1", "2", "3"} });
	insert(&root, T{ 92, "Файл_номер_4", ".exel", 432, "22.04.2019", {"1", "2", "3"} });
	insert(&root, T{ 77, "Файл_номер_5", ".word", 897, "07.11.2011", {"1", "2", "3"} });
	printf("\n");
	cout << "Запись элементов в дерево..." << endl;
	backwardTraversal(root, listTree);
	printf("\n");
	cout << "Элементы дерева:" << endl;
	for (T n : listTree)
		printf("%d ", n.id);
	printf("\n");
	cout << "Отсортированные элементы дерева:" << endl;
	inclusionSort(listTree);
	for (T n : listTree)
		printf("%d ", n.id);
	cout << "\nДобавление элемента с ключом 26..." << endl;
	insert(&root, T{ 26, "Файл_номер_6", ".jpg", 632, "02.03.2004", {"1", "2", "3"} });
	printf("\n");
	listTree.clear();
	cout << "Запись элементов в дерево..." << endl;
	backwardTraversal(root, listTree);
	printf("\n");
	cout << "Элементы дерева:" << endl;
	for (T n : listTree)
		printf("%d ", n.id);
	printf("\n");
	cout << "Отсортированные элементы дерева:" << endl;
	inclusionSort(listTree);
	for (T n : listTree)
		printf("%d ", n.id);
	printf("\n");
	printf("\n");
	cout << "Удаление элемента с ID = 28..." << endl;
	deleteValue(root, T{ 28 });
	printf("\n");
	cout << "Запись элементов в дерево..." << endl;
	backwardTraversal(root, listTree);
	listTree.clear();
	backwardTraversal(root, listTree);
	printf("\n");
	cout << "Элементы дерева:" << endl;
	for (T n : listTree)
		printf("%d ", n.id);
	printf("\n");
	cout << "Отсортированные элементы дерева:" << endl;
	inclusionSort(listTree);
	for (T n : listTree)
		printf("%d ", n.id);
	printf("\n");
	printf("\n");
	cout << "Вывод элементов дерева:" << endl;
	for (T n : listTree)
		cout << "ID: " << (n.id) << "."
		<< " Имя файла: " << (n.name) << "."
		<< " Размер файла: " << (n.size) << " Мбайт."
		<< " Тип: " << (n.type) << "."
		<< " Дата изменения: " << (n.dateLastUpdate) << "."
		<< "\n";
}