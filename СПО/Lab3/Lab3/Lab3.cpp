#include <iostream>
#include <string>
#include <vector>

using namespace std;

int main() {
	string str = "/*01010101*//*010101010101*/0101";
	vector<vector<char> > tp{
	  { ' ', 'H', '1', '2', '3', '4', '5', '6', '7'},
	  { '0', '5', ' ', '3', '3', ' ', ' ', '7', ' '},
	  { '1', ' ', ' ', '3', '3', ' ', '6', ' ', 'B'},
	  { '/', '1', ' ', ' ', ' ', 'A', ' ', ' ', ' '},
	 { '*', 'C', '2', ' ', '4', ' ', ' ', ' ', ' '}
	};
	char CS = 'H';
	string word;
	vector<vector<string> > tk{ { "", "" } };
	bool error = false;
	string so;
	for (int i = 0; i < str.size(); i++) {
		int j;
		bool flag = true;
		for (j = 0; j < tp.size(); j++) {
			if (str[i] == tp[j][0]) {
				flag = false;
				break;
			}
		}
		int k;
		if (flag) j = 0;
		else {
			for (k = 0; k < tp[j].size() - 1; k++) {
				if (CS == tp[0][k]) {
					flag = false;
					break;
				}
			}
		}
		if (flag) k = 0;
		if (tp[j][k] != ' ') {
			CS = tp[j][k];
			word += str[i];
			if ((i == str.size() - 1) && (CS != 'A') && (CS != 'B') && (CS != 'C')) {
				so = str;
				error = true;
				break;
			}
			if (CS == 'A') {
				int l;
				int p = 1;
				for (l = 0; l < tk.size(); l++) {
					if (word == tk[l][0]) {
						word = "";
						break;
					}
					else if (l == tk.size() - 1) {
						for (int o = 0; o < tk.size(); o++) 
							if (tk[o][1][0] == '1') p++;
						if (tk[0][0] == "") tk.clear();
						tk.push_back({ word, "1" + to_string(p) });
						word = "";
						break;
					}
				}
				CS = 'H';
			}
			if (CS == 'B') {
				int l;
				int p = 1;
				for (l = 0; l < tk.size(); l++) {
					if (word == tk[l][0]) {
						word = "";
						break;
					}
					else if (l == tk.size() - 1) {
						for (int o = 0; o < tk.size(); o++) 
							if (tk[o][1][0] == '2') p++;
						if (tk[0][0] == "") tk.clear();
						tk.push_back({ word, "2" + to_string(p) });
						word = "";
						break;
					}
				}
				CS = 'H';
			}
			if (CS == 'C') {
				int l;
				int p = 1;
				for (l = 0; l < tk.size(); l++) {
					if (word == tk[l][0]) {
						word = "";
						break;
					}
					else if (l == tk.size() - 1) {
						for (int o = 0; o < tk.size(); o++) 
							if (tk[o][1][0] == '3') p++;
						if (tk[0][0] == "") tk.clear();
						tk.push_back({ word, "3" + to_string(p) });
						word = "";
						break;
					}
				}
				CS = 'H';
			}
		}
		else {
			so = str;
			error = true;
			break;
		}
	}
	string kod;
	if (error) cout << so << " - error";
	else {
		for (int i = 0; i < tk.size(); i++) {
			cout << tk[i][0] << " " << tk[i][1] << endl;
		}
		cout << endl << str << endl;
		for (int i = 0; i < str.size(); i++) {
			int j;
			for (j = 0; j < tp.size(); j++) {
				if (str[i] == tp[j][0]) break;
			}
			int k;
			for (k = 0; k < tp[j].size() - 1; k++) {
				if (CS == tp[0][k]) break;
			}
			if (tp[j][k] != ' ') {
				CS = tp[j][k];
				word += str[i];
				if (CS == 'A') {
					for (int l = 0; l < tk.size(); l++) {
						if (word == tk[l][0]){
							word = "";
							kod = tk[l][1];
							cout << kod << " ";
							break;
						}
					}
					CS = 'H';
				}
				if (CS == 'B') {
					for (int l = 0; l < tk.size(); l++) {
						if (word == tk[l][0]) {
							word = "";
							kod = tk[l][1];
							cout << kod << " ";
							break;
						}
					}
					CS = 'H';
				}
				if (CS == 'C') {
					for (int l = 0; l < tk.size(); l++) {
						if (word == tk[l][0]) {
							word = "";
							kod = tk[l][1];
							cout << kod << " ";
							break;
						}
					}
					CS = 'H';
				}
			}
		}
	}
}