#include <fcntl.h> //open//
#include <unistd.h> //close//
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>

void hadle_file( int cli, char* s, int sz) {
    memset(s, 0, sz);

    char filename[100];
    int len = read(cli, filename, sizeof(filename));
    printf("recelve file: %s", filename);

    int file = open(filename, O_RDWR);

    while((len = read(cli, s, sz)) > 0) {
        write(file, s, sz);
    }
    close(file);
}

int main() {
    int ss, cli, pid;
    struct sockaddr_in ad;
    char s[100];
    socklen_t ad_length = sizeof(ad);

    ss = socket(AF_INET, SOCK_STREAM, 0); //creates socket//

    memset(&ad, 0, sizeof(ad)); //bind the socket to port 12345//
    ad.sin_family = AF_INET;
    ad.sin_addr.s_addr = INADDR_ANY;
    ad.sin_port = htons(12345);
    bind(ss, (struct sockaddr *)&ad, ad_length);

    listen(ss, 0);

    while (1) {
        cli = accept(ss, (struct sockaddr *)&ad, &ad_length);  //incoming connection//

        pid = fork();
        if (pid == 0) { //The son serves this client//
            printf("client connected\n"); 
            hadle_file(cli, s, sizeof(s));
            return 0;
        }
        else { //The father continues the loop to accept more clients//
            continue;
        }
    }
    close(cli); //disconnect//
}