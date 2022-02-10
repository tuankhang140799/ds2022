struct file{
    char name[1024];
    char data[1024];
};

program FILESTRANSFER {
	version FILESTRANSFER_1 {
		int transfer_file(file)=1;
	} = 1;
} = 0x43521111;