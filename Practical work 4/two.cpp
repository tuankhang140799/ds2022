#include<iostream>
#include<vector>
#include <stdio.h>
#include <mpi.h>
#include <string>
#include <string.h>
#include <stdlib.h>
#include <fstream>
#include <unistd.h>
#include <sstream>
#include <string.h>
#include <map>
#include <iterator>
#include <cctype>
#include<algorithm>
int main(int argc, char* argv[]) {

	MPI_Init(&argc, &argv);
	MPI_Comm c;
	MPI_Status status;
	char port[MPI_MAX_PORT_NAME];
	char port1[53];
	FILE* fp;
	int option, key, rand;
	char value[20];
	char content[299] = { 0 };
	std::string line, word;
	std::vector<std::pair<std::string, int>>data;
	char buf;
	MPI_Open_port(MPI_INFO_NULL, port);
	int is_primary = 1;
	int again = 1;
	std::cout << port << std::endl;
	fp = fopen("b.txt", "w");
	if (fp == NULL) {
		perror("Cannot read file\n");
		exit(1);
	}
	else {
		printf("File has been read\n");
	}
	fprintf(fp, "%s\n", port);
	fclose(fp);
	printf("File has been written\n");

	while (fopen("server.txt", "r") != NULL) {
		fread(port1, 1, 53, fp);
		fclose(fp);
		break;
	}
	MPI_Comm_connect(port1, MPI_INFO_NULL, 0, MPI_COMM_WORLD, &c);
	MPI_Recv(&content, 298, MPI_CHAR, MPI_ANY_SOURCE, 2003, c, &status);
	std::cout << content;
	line = content;
	for (int i = 0, len = line.size(); i < len; i++) {
		if (ispunct(line[i])) {
			line.erase(i--, 1);
			len = line.size();
		}
	}
	std::istringstream ss(line);
	while (ss >> word) {
		std::pair<std::string, int> pair1;
		pair1.first = word;
		pair1.second = 1;
		data.push_back(pair1);
	}
	std::sort(data.begin(), data.end());
	for (int i = 0; i < data.size(); i++) {
		std::cout << data[i].first << data[i].second << std::endl;
	}
	std::map<std::string, int> counts;
	for (const auto& p : data) {
		if (!counts[p.first]) {
			counts[p.first] = p.second;
		}
		else {
			counts[p.first] += p.second;
		}
	}
	sleep(1.0);
	MPI_Comm_connect(port1, MPI_INFO_NULL, 0, MPI_COMM_WORLD, &c);
	int total = counts.size();
	MPI_Send(&(total), 1, MPI_INT, 0, 2004, c);
	for (const auto& p : counts) {
		std::string p1 = p.first;
		int count = p.second;
		char buff[20] = { 0 };
		memset(&buff, 0, sizeof(buff));
		strcpy(buff, p1.c_str());
		MPI_Send(&buff, 20, MPI_CHAR, 0, 2004, c);
		MPI_Send(&count, 1, MPI_INT, 0, 2004, c);
	}
	MPI_Comm_disconnect(&c);
	MPI_Finalize();
	return 0;
}