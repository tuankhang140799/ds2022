#include <stdio.h>
#include <mpi.h>
#include <string.h>
#include <stdlib.h>
#define size_tag 1000
#define char_tag 1000

void send_file(FILE* fp, char* name, char* data, int rank_des) {
	fp = fopen(name, "r");
	if (fp == NULL) {
		perror("Failed to read file\n");
		exit(1);
	}
	else {
		printf("File has been read\n");
	}
	int buffer_size;
	while (1) {
		int buffer_size = fread(data, 1, 1024, fp);
		// printf("Buffer size %d \n", buffer_size);
		MPI_Send(&buffer_size, 1, MPI_INT, rank_des, size_tag, MPI_COMM_WORLD);
		MPI_Send(data, buffer_size, MPI_CHAR, rank_des, char_tag, MPI_COMM_WORLD);
		// printf("Buffer send: %s \n", buffer);                        
		if (buffer_size < 1024) {
			printf("\nUpload done.\n");
			break;
		}
	}


	fclose(fp);

}

void receive_file(FILE* fp, char* filename, char* buffer, int rank_from) {

	fp = fopen(filename, "a");
	if (fp == NULL) {
		perror("Error in reading file..\n");
		exit(1);
	}
	else {
		printf("Reading file received successfully..\n");
	}
	int buffer_size;
	while (1) {
		MPI_Recv(&buffer_size, 1, MPI_INT, rank_from, size_tag, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		MPI_Recv(buffer, buffer_size, MPI_CHAR, rank_from, char_tag, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
		fwrite(buffer, 1, buffer_size, fp);
		if (buffer_size < 1024) {
			printf("\nWrite finished.\n");
			break;
		}


	}
	fclose(fp);
}

int main(int argc, char* argv[]) {
	FILE* fp;
	int my_id, numprocs, len;
	char data[1024];
	char name[MPI_MAX_PROCESSOR_NAME];
	int client_to_server = 0;
	MPI_Init(&argc, &argv);

	MPI_Comm_rank(MPI_COMM_WORLD, &my_id);
	MPI_Comm_size(MPI_COMM_WORLD, &numprocs);

	if (numprocs < 2) {
		printf("2 processes at least");
	}
	MPI_Get_processor_name(name, &len);
	char* root_send = "send.txt";
	char* slave_received = "received.txt";
	memset(&data, 0, sizeof(data));
	if (client_to_server) {
		if (my_id == 0) {
			send_file(fp, root_send, data, 1);
		}
		else if (my_id == 1) {
			receive_file(fp, slave_received, data, 0);
		}
	}
	else {
		if (my_id == 0) {
			receive_file(fp, root_send, data, 1);
		}
		else if (my_id == 1) {
			send_file(fp, slave_received, data, 0);
		}
	}

	MPI_Finalize();
}