#include <iostream>
#include <stdio.h>

using namespace std;

int i = 0;
string str;
char c;
void YC();
void PO();
void PO_();
void O();
void YC() {
	if (str[i] == 'p' && str[i + 1] == 'i' && str[i + 2] == 'n') {
		i += 3; PO();
		if (str[i] == 'e') {
			i++; PO();
			if (str[i] == 'f') { i++; }
			else cout << "From IF after f!" << endl;
		}
	}
	if (str[i] != '!') {
		cout << "Строка -" << str << ". Встречен неизвестный символ " << str[i] << endl; 
		exit(0);
	}
}
void PO() {
	O();
	PO_();
}
void PO_() {
	if (str[i] == ';') { i++; O(); PO_(); return; }
	if (str[i] == '$') { i++; return; }
}
void O() {
	if (str[i] == 'p') { i++; return; }
	if (str[i] == 'c') { i++; return; }
	if (str[i] == 'o') { i++; return; }
	else { YC(); }
}
int main() {
	setlocale(LC_ALL, "Rus");
	str = "pinpeo$f!";
	YC();
	cout << "Строка - " << str << " полностью корректна" << endl;
	return 0;
}