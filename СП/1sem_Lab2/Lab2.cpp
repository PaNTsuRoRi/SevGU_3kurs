#include <iostream>
using namespace std;
const int N = 6; //размерность дека

struct Deq {
	char data[N]; //массив данных
	int last; //указатель на конец
};

void Creation(Deq* D) { //создание дека
	D->last = 0;
}

bool Full(Deq* D) { //проверка дека на пустое значение
	if (D->last == 0) return true;
	else return false;
}

void AddInBeg(Deq* D) { //добавление элемента в начало
	if (D->last == N) {
		cout << "Дек заполнен\n" << endl; 
		return;
	}
	char val;
	cout << "Значение:";
	cin >> val;
	for (int i = D->last; i > 0; i--)
		D->data[i] = D->data[i - 1];
	D->data[0] = val;
	D->last++;
	cout << "Элемент успешно добавлен\n" << endl;
}

void DeleteBeg(Deq* D) { //удаление первого элемента
	for (int i = 0; i < D->last; i++) 
		D->data[i] = D->data[i + 1]; D->last--;
}

void OutDeq(Deq* D) { //Вывод всего дека
	for (int i = 0; i < D->last; i++)
		cout << D->data[i] << " ";
	cout << endl;
}

int Size(Deq* D) { //размер дека
	return D->last;
}

int main() {
	setlocale(LC_ALL, "Rus");
	Deq D;
	Creation(&D);
	int num;
	do {
		//Меню
		cout << "1. Добавить элемент в начало" << endl;
		cout << "2. Удалить первый элемент" << endl;
		cout << "3. Вывести все элементы" << endl;
		cout << "4. Узнать размер дека" << endl;
		cout << "0. Выйти" << endl;
		cout << "Номер команды: ";
		cin >> num;
		switch (num) {
		case 1:
			AddInBeg(&D);
			break;
		case 2:
			DeleteBeg(&D);
			break;
		case 3:
			OutDeq(&D);
			break;
		case 4:
			cout << "Размер дека:" << Size(&D) << endl;
			break;
		case 0:
			break;
		default:
			cout << "Неверный ввод команды" << endl;
			break;
		}
	} while (num != 0);
}
