#include <iostream>
#include <Windows.h>
#include <bitset>

using namespace std;

int main()
{
    SetConsoleCP(1251);
    SetConsoleOutputCP(1251);

    //Задание 1
    cout << "___Задание 1___" << endl;
    cout << "short: " << sizeof(short) << " bytes" << endl;
    cout << "int: " << sizeof(int) << " bytes" << endl;
    cout << "long: " << sizeof(long) << " bytes" << endl;
    cout << "float: " << sizeof(float) << " bytes" << endl;
    cout << "double: " << sizeof(double) << " bytes" << endl;
    cout << "long double: " << sizeof(long double) << " bytes\n" << endl;

    //Задание 2
    cout << "___Задание 2___" << endl;
    cout << "Signed char: " << bitset <10> ((signed char) -15) << endl;
    cout << "Unsigned char: " << bitset <10> ((unsigned char) -15) << "\n" << endl;

    //Задание 3
    cout << "___Задание 3___" << endl;
    cout << "Целые числа:" << endl;
    const int c = 2;
    const int u = 2u;
    const int l = 2l;
    const int ul = 2ul;
    const int e = 02;
    const int s = 0x2;
    cout << "const: " << c << endl;
    cout << "U const: " << u << endl;
    cout << "L const: " << l << endl;
    cout << "UL const: " << ul << endl;
    cout << "e const: " << e << endl;
    cout << "s const: " << s << endl;
    cout << "Вещественные числа: " << endl;
    const double con = 131.7;
    const double exp = 131.7;
    const float f = 131.7f;
    const double l2 = 131.7l;
    const double e2 = 1.317e2;
    cout << "const: " << con << endl;
    cout << "exponent: " << exp << endl;
    cout << "float: " << f << endl;
    cout << "L: " << l2 << endl;
    cout << "E: " << e2 << endl;
    cout << "Символьных и строковых:";
    const char x = 's';
    cout << "\0" << x << endl;
    cout << "\a" << x << endl;
    cout << "\b" << x << endl;
    cout << "\t" << x << endl;
    cout << "\n" << x << endl;
    cout << "\v" << x << endl;
    cout << "\f" << x << endl;
    cout << "\r" << x << "\n" << endl;

    //Задание 4
    cout << "___Задание 4___" << endl;
    for (int i = 48; i <= 57; i++)
        cout << "code: " << i << "   " << "char: " << (char)i << endl;
    for (int i = 97; i <= 122; i++)
        cout << "code: " << i << "   " << "char: " << (char)i << endl;
    for (int i = 65; i <= 90; i++)
        cout << "code: " << i << "   " << "char: " << (char)i << endl;
    cout << "code: " << "127" << "   " << "char: " << (char)127 << endl;
    
    //Задание 5
    /*cout << "___Задание 5___" << endl;
    const int p = 09;
    const double o = 1.7e;
    const char i = 6r;*/

    //Задание 6
    int r = 7; //копирующая инициализация
    int t(7); //прямая инициализация
    int y{ 7 }; //uniform инизиализация
    int m{};  //инициализация по умолчанию
    int rdron{ 32 }; //ошибка целочисленного переменная не может содержать нецелочисленные значения

    //Задание 7
    /*static const int conint = 6;
    static const string constr = "Constant string";
    auto const connum = 7;
    auto const aconstr = "Auto constant string";
    conint++;
    constr = "String";
    connum--;
    aconstr = "A String";*/

    //Задание 8
    cout << "___Задание 8___" << endl;
    int a1 = 5 % -4;
    int a2 = -5 % 4;
    int a3 = -5 % -4;
    cout << a1 << endl;
    cout << a2 << endl;
    cout << a3 << endl;

    //Задание 9
    cout << "___Задание 9___" << endl;
    bool b1 = true, b2 = true, b3 = false, c1, c2, c3, c4;
    c1 = b1 == b2;
    c2 = b1 == b2 && b3;
    c3 = b1 != b2;
    c4 = b1 != b2 && b3;
    cout << c1 << endl;
    cout << c2 << endl;
    cout << c3 << endl;
    cout << c4 << endl;

    //Задание 10
    cout << "___Задание 10___" << endl;
    cout << "short: " << sizeof(short) << endl;
    cout << "int: " << sizeof(int) << endl;
    cout << "long: " << sizeof(long) << endl;
    cout << "float: " << sizeof(float) << endl;
    cout << "double: " << sizeof(double) << endl;
    cout << "long double: " << sizeof(long double) << endl;

    //Задание 12
    cout << "___Задание 12___" << endl;
    char number = 31;
    cout << bitset < sizeof(number) * CHAR_BIT>(number) << endl;
    int number2 = number;
    cout << bitset<sizeof(number2)* CHAR_BIT>(number2) << endl;
    number = -127;
    cout << bitset < sizeof(number)* CHAR_BIT>(number) << endl;
    number2 = number;
    cout << bitset<sizeof(number2)* CHAR_BIT>(number2) << endl;
}